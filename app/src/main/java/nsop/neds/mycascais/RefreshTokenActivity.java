package nsop.neds.mycascais;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

public class RefreshTokenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_token);

        final Context context = this;
        final int _nid = getIntent().getIntExtra("nid", 0);

        SessionManager sm = new SessionManager(this);

        AccountManager mAccountManager = AccountManager.get(context);
        Account[] availableAccounts  = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        String ssk = mAccountManager.getUserData(availableAccounts[0], "SSK");
        String userid = mAccountManager.getUserData(availableAccounts[0], "UserId");
        String refreshToken = mAccountManager.getUserData(availableAccounts[0], "RefreshToken");

        String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"RefreshToken\":\"%s\", \"FirebaseToken\":\"%s\", AppType:2, LanguageID:%s}", ssk, userid, refreshToken, sm.getFirebaseToken(), sm.getLangCodePosition() + 1);

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.RefreshLoginUser), jsonRequest, true, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                String message = WebApiMessages.DecryptMessage(response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {

                String message = WebApiMessages.DecryptMessage(response);

                String ssk = ReportManager.getSSk(message);
                String userId = ReportManager.getUserID(message);
                String refreshToken = ReportManager.getRefreshToken(message);

                SessionManager sm = new SessionManager(context);
                sm.setRefreshToken(refreshToken);

                AccountManager mAccountManager = AccountManager.get(context);

                final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                boolean asAccount = availableAccounts.length != 0;

                Account account = availableAccounts[0];

                if(!ssk.isEmpty()) {
                    mAccountManager.setUserData(account, "SSK", ssk);
                    mAccountManager.setUserData(account, "UserId", userId);
                    mAccountManager.setUserData(account, "RefreshToken", refreshToken);

                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(Variables.Id, _nid);

                    startActivity(intent);
                }else{
                    if(AccountGeneral.logout(context)){
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra(Variables.LogoutSession, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        /*Intent intent = new Intent(context, NoServiceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*/
                    }
                }
            }
        });

    }
}
