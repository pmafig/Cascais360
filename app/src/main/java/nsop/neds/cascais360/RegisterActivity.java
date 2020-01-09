package nsop.neds.cascais360;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import nsop.neds.cascais360.Encrypt.MessageEncryption;
import nsop.neds.cascais360.Manager.ControlsManager.InputValidatorManager;
import nsop.neds.cascais360.Manager.MenuManager;
import nsop.neds.cascais360.Manager.SessionManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Manager.WeatherManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.ReportManager;
import nsop.neds.cascais360.WebApi.WebApiCalls;
import nsop.neds.cascais360.WebApi.WebApiClient;
import nsop.neds.cascais360.WebApi.WebApiMessages;

public class RegisterActivity extends AppCompatActivity {

    LinearLayout menuFragment;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        new MenuManager(this, toolbar, menuFragment, Settings.labels.CreateAccount);

        Button registerButton = findViewById(R.id.createAccount);
        registerButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount(){
        EditText accountEmailField = findViewById(R.id.accountEmail);
        EditText accountNifField = findViewById(R.id.accountNif);
        EditText accountPhoneField = findViewById(R.id.accountPhone);
        EditText accountNameField = findViewById(R.id.accountName);
        EditText accountPasswordField = findViewById(R.id.accountPassword);
        EditText accountRePasswordField = findViewById(R.id.accountRePassword);

        CheckBox accountAgreementField = findViewById(R.id.accountCheckboxAgreement);

        String accountEmail = accountEmailField.getText().toString();
        String accountNif = accountNifField.getText().toString();
        String accountPhone = accountPhoneField.getText().toString();
        String accountName = accountNameField.getText().toString();
        String accountPassword = accountPasswordField.getText().toString();
        String accountRePassword = accountRePasswordField.getText().toString();

        Boolean agreement = accountAgreementField.isChecked();

        AlertDialog.Builder alertMessage = new AlertDialog.Builder(this, R.style.AlertMessageDialog);
        alertMessage.setTitle("Criar Conta");

        if(PasswordValidation(accountPassword, accountRePassword)){
            if(DataValidation(accountEmail, accountNif, accountPhone, accountName)){
                if(agreement){
                    CreateUser(accountEmail, accountNif, accountPhone, accountName, accountPassword, accountRePassword, "1", "+351");
                }else{
                    alertMessage.setMessage("Para criar a conta necessita concordar com as condições.").show();
                }
            }
            else{
                alertMessage.setMessage("Dados inválidos.").show();
            }
        }else{
            alertMessage.setMessage("Password inválida.").show();
        }
    }

    private boolean DataValidation(String accountEmail, String accountNif, String accountPhone, String accountName){

        InputValidatorManager validator = new InputValidatorManager();

        boolean valid = true;

        if(!validator.isValidEmail(accountEmail)){
            final EditText accountEmailField = findViewById(R.id.accountEmail);
            accountEmailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    accountEmailField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                }
            });
            accountEmailField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));

            valid = false;
        }

        if(!validator.isValidNif(accountNif)){
            final EditText accountNifField = findViewById(R.id.accountNif);
            accountNifField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    accountNifField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                }
            });

            accountNifField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));

            valid = false;
        }

        if(!validator.isValidPhone(accountPhone)){
            final EditText accountPhoneField = findViewById(R.id.accountPhone);
            accountPhoneField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    accountPhoneField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                }
            });
            accountPhoneField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));

            valid = false;
        }

        if( validator.isNullOrEmpty(accountName)){
            final EditText accountNameField = findViewById(R.id.accountName);
            accountNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    accountNameField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                }
            });
            accountNameField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));

            valid = false;
        }

        return valid;
    }

    private boolean PasswordValidation(String accountPassword, String accountRePassword){

        InputValidatorManager validator = new InputValidatorManager();

        return !(validator.isValidPassword(accountPassword)
                || validator.isValidPassword(accountRePassword)
                || !accountPassword.equals(accountRePassword));
    }

    private void CreateUser(String userEmail, String userNif, final String userPhone, String userName, String password, String confPassword, String countryId, String countryCode){

        String jsonRequest = String.format("{\"Email\":\"%s\", \"Nif\":\"%s\", \"PhoneNumber\":\"%s\", \"FullName\":\"%s\", \"Password\":\"%s\", \"ConfirmPassword\":\"%s\", \"CountryID\":\"%s\", \"CountryCode\":\"%s\"}",
                userEmail, userNif, userPhone, userName, new MessageEncryption().Encrypt(password, WebApiClient.SITE_KEY),
                new MessageEncryption().Encrypt(confPassword, WebApiClient.SITE_KEY), countryId, countryCode);

        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("A criar utilizador...");

        progressDialog.show();

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.CreateMobileTemporaryAuthentication), jsonRequest, true,  new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();
                postSuccess(response);
            }
        });
    }

    private void postSuccess(String response){
        String message = WebApiMessages.DecryptMessage(response);

        if(ReportManager.operationSuccess(message)){
            Intent intent = new Intent(RegisterActivity.this, EmailSentActivity.class);
            EditText accountEmailField = findViewById(R.id.accountEmail);
            EditText accountMobileNumberField = findViewById(R.id.accountPhone);
            intent.putExtra(Variables.Email, accountEmailField.getText());

            SessionManager sm = new SessionManager(this);
            sm.setNewAccount();
            sm.setMobileNumber(accountMobileNumberField.getText().toString());

            startActivity(intent);
        }else{
            /*Intent intent = new Intent(this, NoServiceActivity.class);
            String errorList = ReportManager.getErrorReportList(message);
            intent.putExtra("errorMessage", errorList);
            startActivity(intent);*/
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}