package nsop.neds.mycascais;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import nsop.neds.mycascais.Entities.UserEntity;
import nsop.neds.mycascais.Manager.ContactAsAuth;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

public class EmailSentActivity extends AppCompatActivity {

    LinearLayout menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        menuFragment = findViewById(R.id.menu);

        toolbar.findViewById(R.id.toolbar_image).setVisibility(View.GONE);

        Uri appLinkData = getIntent().getData();

        String tokenEnc = appLinkData.getQueryParameter(Variables.VT);

        String token = new MessageEncryption().Decrypt(WebApiClient.SITE_KEY, tokenEnc);

        ValidateSmsToken(token);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void ValidateSmsToken(String token) {
        SessionManager sm = new SessionManager(EmailSentActivity.this);

        String jsonRequest = "";

        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Validando código...");

        final UserEntity user = AccountGeneral.getUser(this);

        jsonRequest = String.format("{\"Token\":\"%s\", \"ssk\":\"%s\", \"userid\":\"%s\", LanguageID:%s}", token, user.getSsk(), user.getUserId(), sm.getLangCodePosition() + 1);
        String url = String.format("/%s/%s", WebApiClient.API.crm, WebApiClient.METHODS.ValidateEntityState);

        progressDialog.show();

        WebApiClient.post(url, jsonRequest, true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
                //TODO neste momento não é possivel fazer esta operação - message
                //startActivity(new Intent(SmsTokenValidationActivity.this, ErrorActivity.class));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {

                progressDialog.dismiss();

                final String message = WebApiMessages.DecryptMessage(response);

                JSONObject jsonMessage = null;
                String receivedMessage = "";

                try {
                    jsonMessage = new JSONObject(message);
                } catch (JSONException ex) { }

                try {
                    if (jsonMessage.has("ReportList") && !jsonMessage.isNull("ReportList")) {

                        Type ReportListType = new TypeToken<ArrayList<ReportList>>() {}.getType();

                        List<ReportList> reportList = null;

                        reportList = new Gson().fromJson(jsonMessage.getJSONArray(Variables.ReportList).toString(), ReportListType);


                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < reportList.size(); i++) {
                            sb.append(reportList.get(i).Description);
                            if (i + 1 < reportList.size()) {
                                sb.append("\n");
                            }
                        }

                        /*if(sb.length() > 0) {
                            receivedMessage = sb.toString();
                            LayoutManager.alertMessage(ValidateSMSTokenActivity.this, receivedMessage);
                        }else{
                            SessionManager sm = new SessionManager(ValidateSMSTokenActivity.this);

                            if(sm.getMobileNumber() == null || sm.getMobileNumber().equals("")){
                                sm.setEmail(mobileNumber);
                            }

                            if(isAuth){
                                new ContactAsAuth().execute(WebApiCalls.setMobileAuth(user.getSsk(), user.getUserId(), mobileId));
                            }

                            Intent intent = new Intent(ValidateSMSTokenActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }*/

                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(EmailSentActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
