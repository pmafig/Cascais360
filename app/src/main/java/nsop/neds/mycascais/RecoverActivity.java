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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Entities.Json.ReportList;
import nsop.neds.mycascais.Entities.WebApi.CreateTemporaryLoginUserResponse;
import nsop.neds.mycascais.Entities.WebApi.ResetLoginRequest;
import nsop.neds.mycascais.Entities.WebApi.ResetLoginResponse;
import nsop.neds.mycascais.Manager.ControlsManager.InputValidatorManager;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;
import nsop.neds.mycascais.WebApi.WebApiMethods;

public class RecoverActivity extends AppCompatActivity {

    private EditText recover;

    private Button recoverButton;

    LinearLayout menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);

        recover = findViewById(R.id.recoverEmail);
        recover.setHint(Settings.labels.Username);

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
        recoverButton.setText(Settings.labels.Send);

        recoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverAccount(recover.getText().toString(), Settings.LangCode.equals("pt") ? 1 : 2);
            }
        });

    }

    private void recoverAccount(final String userName, int languageID){
        if(!userName.isEmpty()){

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Processando...");

            ResetLoginRequest request = new ResetLoginRequest();
            request.UserName = userName;
            request.LanguageID = languageID;

            progressDialog.show();
            WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.ResetLoginUser), new Gson().toJson(request), true, new TextHttpResponseHandler(){
                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(RecoverActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    progressDialog.dismiss();
                    try {
                        postSuccess(response, userName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            LayoutManager.alertMessage(this, Settings.labels.ForgotPassword, Settings.labels.LoginSubtitle);
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

    private void postSuccess(String response, final String userName) throws JSONException {
        final String message = WebApiMessages.DecryptMessage(response);

        JSONObject responseMessage = null;

        try {
            responseMessage = new JSONObject(message);

            ResetLoginResponse responseData = null;

            if (responseMessage.has("ResponseData")) {
                responseData = new Gson().fromJson(responseMessage.getJSONObject("ResponseData").toString(), ResetLoginResponse.class);
            }

            Type ReportListType = new TypeToken<ArrayList<ReportList>>() {}.getType();

            if (responseMessage.has("ReportList")) {
                List<ReportList> reportList = new Gson().fromJson(responseMessage.getJSONArray("ReportList").toString(), ReportListType);

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < reportList.size(); i++) {
                    sb.append(reportList.get(i).Description);
                    if (i + 1 < reportList.size()) {
                        sb.append("\n");
                    }
                }

                if(sb.length() > 0) {
                    Toast.makeText(this, Settings.labels.PasswordRecovery, Toast.LENGTH_LONG);
                    //LayoutManager.alertMessage(this, Settings.labels.PasswordRecovery, sb.toString());
                }else{
                    Toast.makeText(this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                }
            }

            if (responseData != null ) { //&& responseData.OperationSucess
                if(responseData.IsReset || responseData.Email || responseData.SMS) {
                    Data.CurrentAccountName = userName;
                    if (responseData.Email) {
                        Data.RecoverByEmail = true;
                    } else if (responseData.SMS) {
                        Data.RecoverBySms = true;

                        SmsRetrieverClient client = SmsRetriever.getClient(this);
                        Task<Void> task = client.startSmsRetriever();

                        Data.SmsValidationContext = Data.ValidationContext.newAccount;
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //alertMessage.setMessage("Foi enviada uma mensagem para a seu telemóvel.");
                                //alertMessage.show();
                            }
                        });

                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //alertMessage.setMessage("Ocorreu um problema com o pedido, por favor, tente novamente!");
                                // alertMessage.show();
                            }
                        });

                        Intent intent = new Intent(RecoverActivity.this, ValidateSMSTokenActivity.class);
                        intent.putExtra(Variables.MobileNumber, userName);
                        intent.putExtra(Variables.Token, responseData.Token);
                        startActivity(intent);

                    }
                }
            }
        } catch (JSONException ex) {

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