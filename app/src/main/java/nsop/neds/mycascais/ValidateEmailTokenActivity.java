package nsop.neds.mycascais;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

public class ValidateEmailTokenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_email_token);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView title = toolbar.findViewById(R.id.title_quero_ver);
        title.setTextColor(Color.parseColor(Settings.color));
        title.setText(R.string.title_activity_mycascais);

        Button validateButton = findViewById(R.id.accountValidateEmailToken);
        validateButton.setBackgroundColor(Color.parseColor(Settings.color));

        EditText emailTokenField = findViewById(R.id.emailTokenPhone);

        Uri appLinkData = getIntent().getData();
        final String emailToken = appLinkData.getQueryParameter("vt");

        emailTokenField.setText(emailToken);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateEmailToken(emailToken);
            }
        });*/
    }

    private void ValidateEmailToken(final String token){

        String jsonRequest = String.format("{\"TemporaryToken\":\"%s\"}", token);

        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Validando email token...");

        progressDialog.show();

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.ValidateMobileTemporaryLoginUser), jsonRequest, true, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();

                String json = WebApiMessages.DecryptMessage(response);

                SmsRetrieverClient client = SmsRetriever.getClient(getBaseContext());

                Task<Void> task = client.startSmsRetriever();

                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(ValidateEmailTokenActivity.this, String.format(getResources().getString(R.string.info_message_sms_codevalidation), new SessionManager(getBaseContext()).getMobileNumber()), Toast.LENGTH_SHORT).show();
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(ValidateEmailTokenActivity.this, getResources().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
