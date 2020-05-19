package nsop.neds.mycascais;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.ReportList;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Entities.WebApi.ResendSMSTokenRequest;
import nsop.neds.mycascais.Entities.WebApi.ResendSMSTokenResponse;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

public class NoDataActivity extends AppCompatActivity {

    Button backButton;
    TextView noDataMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_data);

        refreshToken();

        backButton = findViewById(R.id.accountBack);
        noDataMessage = findViewById(R.id.noDataMessage);

        backButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
        backButton.setText(Settings.labels.Back);

        noDataMessage.setText(Settings.labels.AppInMaintenanceMessage);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                finish();
            }
        });
    }

    private void refreshToken(){
        final Context context = this;
        final int _nid = getIntent().getIntExtra("nid", 0);

        SessionManager sm = new SessionManager(this);

        LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

        /*AccountManager mAccountManager = AccountManager.get(context);
        Account[] availableAccounts  = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        String ssk = mAccountManager.getUserData(availableAccounts[0], "SSK");
        String userid = mAccountManager.getUserData(availableAccounts[0], "UserId");
        String refreshToken = mAccountManager.getUserData(availableAccounts[0], "RefreshToken");*/

        String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"RefreshToken\":\"%s\", \"FirebaseToken\":\"%s\", AppType:2, LanguageID:%s}",
                user.SSK, user.AuthID, user.RefreshToken, sm.getFirebaseToken(), sm.getLangCodePosition() + 1);

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.RefreshLoginUser), jsonRequest, true, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {

                String message = WebApiMessages.DecryptMessage(response);
                SessionManager sm = new SessionManager(context);

                LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                String ssk = user.SSK; //ReportManager.getSSk(message);
                String userId = user.AuthID; //ReportManager.getUserID(message);
                String refreshToken = ReportManager.getRefreshToken(message);

                sm.setRefreshToken(refreshToken);

                AccountManager mAccountManager = AccountManager.get(context);

                final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                boolean asAccount = availableAccounts.length != 0;

                Account account = availableAccounts[0];

                if(!ssk.isEmpty()) {
                    mAccountManager.setUserData(account, "SSK", ssk);
                    mAccountManager.setUserData(account, "UserId", userId);
                    mAccountManager.setUserData(account, "RefreshToken", refreshToken);
                }
            }
        });
    }
}
