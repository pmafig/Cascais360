package nsop.neds.mycascais;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Manager.ControlsManager.InputValidatorManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;
import nsop.neds.mycascais.WebApi.WebApiMethods;

public class RecoverActivity extends AppCompatActivity {

    private EditText recoverEmail;

    private Button recoverButton;

    LinearLayout menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);

        recoverEmail = findViewById(R.id.recoverEmail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        menuFragment = findViewById(R.id.menu);

        toolbar.findViewById(R.id.toolbar_image).setVisibility(View.GONE);

        LinearLayout backButton = toolbar.findViewById(R.id.menu_back_frame);
        backButton.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());

        new MenuManager(this, toolbar, menuFragment, Settings.labels.PasswordRecovery);

        recoverButton = findViewById(R.id.recoverAccount);
        recoverButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));

        recoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverAccount(recoverEmail.getText().toString());
            }
        });

    }

    private void recoverAccount(final String email){

        String jsonRequest = String.format("{\"Email\":\"%s\"}", email);

        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Processando...");

        if(inputValidation(email)){
            progressDialog.show();
            WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiMethods.RECOVERACCOUNT), jsonRequest, true, new TextHttpResponseHandler(){
                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    progressDialog.dismiss();
                    try {
                        postFailure(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    progressDialog.dismiss();
                    try {
                        postSuccess(response, email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private boolean inputValidation(String accountEmail){

        InputValidatorManager validator = new InputValidatorManager();
        boolean validAutentication = true;

        if(!validator.isValidEmail(accountEmail) ){
            validAutentication = false;

            AlertDialog.Builder alertMessage = new AlertDialog.Builder(RecoverActivity.this, R.style.AlertMessageDialog);

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(RecoverActivity.this);
            builder.setTitle(Settings.labels.AlertMessage);

            alertMessage.setMessage(Settings.labels.InvalidEmailAddress);

            alertMessage.show();
        }

        return validAutentication;
    }

    private void postSuccess(String response, final String email) throws JSONException {
        final String message = WebApiMessages.DecryptMessage(response);

        if(ReportManager.success(message)){
            if(ReportManager.smsSent(message)){
                if(ReportManager.mobileApp(message)){
                    SmsRetrieverClient client = SmsRetriever.getClient(this);
                    Task<Void> task = client.startSmsRetriever();
                    Data.SmsValidationContext = Data.ValidationContext.recoverAccount;

                    task.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(RecoverActivity.this, ValidateSMSTokenActivity.class);
                            try {
                                intent.putExtra("phoneNumber", ReportManager.getAppContact(message));
                                intent.putExtra("email", email);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String test = "ok";
                        }
                    });
                }
            }else if(ReportManager.asContacts(message)){
                //SEND USER TO SET PHONE APP FROM LIST
                //Intent intent = new Intent(RecoverActivity.this, ShowContactsActivity.class);

                ArrayList<String> phones =  new ArrayList<>();

                phones = ReportManager.getPhoneContacts(message);

                //intent.putStringArrayListExtra("MobilePhones", phones);
                //intent.putExtra("Email", email);

                //startActivity(intent);
            }else{ //Informar que irá enviar email para recuperação
                //Intent intent = new Intent(RecoverActivity.this, RecoverMessageActivity.class);
                //intent.putExtra("message", ReportManager.getSuccessReportList(message));
                //intent.putExtra("email", email);
                //startActivity(intent);
            }




            /*String appNumber = ReportManager.getAppContacts(message);


            if(appNumber != null){

            }else {
                String[] numbers = ReportManager.getPhonesContacts(message);

                if(numbers.length > 0){

                }else{

                }
            }*/

            //startActivity(new Intent(RecoverActivity.this, NewAccountEmailValidationActivity.class));

        }else{
            //Intent intent = new Intent(this, ErrorActivity.class);
            //String errorList = ReportManager.getErrorReportList(message);
            //intent.putExtra("errorMessage", errorList);
            //startActivity(intent);
        }
    }

    private void postFailure(String response) throws JSONException {
        String message = WebApiMessages.DecryptMessage(response);

        /*Intent intent = new Intent(this, ErrorActivity.class);
        String errorList = ReportManager.getErrorReportList(message);
        intent.putExtra("errorMessage", errorList);
        startActivity(intent);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void enableButton(){

        //TODO: Incorporar a validação do restantes textview valid_1 && valid_2 && valid_3 && valid_4 &
        /*if(valid1 && valid2 && valid3 && valid4 && valid6){
            registerButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
            registerButton.setEnabled(true);
        }else{
            registerButton.setBackgroundColor(Color.parseColor(Settings.colors.Gray2));
            registerButton.setEnabled(false);
        }*/
    }
}