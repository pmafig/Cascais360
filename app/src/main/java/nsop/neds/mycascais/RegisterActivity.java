package nsop.neds.mycascais;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.CreateLoginUserRequest;
import nsop.neds.mycascais.Entities.Json.CreateTemporaryLoginUserRequest;
import nsop.neds.mycascais.Entities.Json.CreateTemporaryLoginUserResponse;
import nsop.neds.mycascais.Entities.Json.Node;
import nsop.neds.mycascais.Entities.Json.ReportList;
import nsop.neds.mycascais.Manager.ControlsManager.InputValidatorManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

public class RegisterActivity extends AppCompatActivity {

    private EditText newPassword;
    private EditText rePassword;

    private ImageView icon1;
    private ImageView icon2;
    private ImageView icon3;
    private ImageView icon4;
    private ImageView icon5;
    private ImageView icon6;


    private boolean valid_1;
    private boolean valid_2;
    private boolean valid_3;
    private boolean valid_4;

    private boolean valid1;
    private boolean valid2;
    private boolean valid3;
    private boolean valid4;
    private boolean valid5;
    private boolean valid6;


    EditText accountEmailField;
    EditText accountNifField;
    EditText accountPhoneField;
    EditText accountNameField;
    EditText accountPasswordField;
    EditText accountRePasswordField;

    CheckBox accountAgreementField;

    //String accountEmail = accountEmailField.getText().toString();
    //String accountNif = accountNifField.getText().toString();
    //String accountPhone = accountPhoneField.getText().toString();
    //String accountName = accountNameField.getText().toString();
    //String accountPassword = accountPasswordField.getText().toString();
    //String accountRePassword = accountRePasswordField.getText().toString();


    private Button registerButton;

    LinearLayout menuFragment;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        icon1 = findViewById(R.id.rule_icon_1);
        icon2 = findViewById(R.id.rule_icon_2);
        icon3 = findViewById(R.id.rule_icon_3);
        icon4 = findViewById(R.id.rule_icon_4);
        icon5 = findViewById(R.id.rule_icon_5);
        icon6 = findViewById(R.id.rule_icon_6);

        TextView rule1 = findViewById(R.id.rule_1);
        TextView rule2 = findViewById(R.id.rule_2);
        TextView rule3 = findViewById(R.id.rule_3);
        TextView rule4 = findViewById(R.id.rule_4);
        TextView rule5 = findViewById(R.id.rule_5);
        TextView rule6 = findViewById(R.id.rule_6);


        rule1.setText(Settings.labels.PasswordRuleLength);
        rule2.setText(Settings.labels.PasswordRuleUppercase);
        rule3.setText(Settings.labels.PasswordRuleLowercase);
        rule4.setText(Settings.labels.PasswordRuleNumber);
        rule5.setText(Settings.labels.PasswordRuleSpecial);
        rule6.setText(Settings.labels.PasswordMismatchMessage);

        accountEmailField = findViewById(R.id.accountEmail);
        accountNifField = findViewById(R.id.accountNif);
        accountPhoneField = findViewById(R.id.accountPhone);
        accountNameField = findViewById(R.id.accountName);
        accountPasswordField = findViewById(R.id.accountPassword);
        accountRePasswordField = findViewById(R.id.accountRePassword);

        accountAgreementField = findViewById(R.id.accountCheckboxAgreement);

        newPassword = findViewById(R.id.accountPassword);
        rePassword = findViewById(R.id.accountRePassword);

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

        registerButton = findViewById(R.id.createAccount);
        //registerButton.setBackgroundColor(Color.parseColor(Settings.colors.Gray2));
        registerButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
        registerButton.setEnabled(true);

        Bundle bundle = getIntent().getExtras();
        Uri appLinkData = getIntent().getData();


        if(bundle != null && bundle.containsKey(Variables.Token)) {
            token = bundle.getString(Variables.Token);
        }else if(appLinkData != null){
            token = appLinkData.getQueryParameter("vt");
        }

        if(token != null){
            validateAccountLayout();
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateAccount();
                }
            });
        }else {
            createAccountLayout();
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createAccount();
                }
            });
        }
    }

    private void createAccountLayout(){
        registerButton.setText(Settings.labels.Continue);
    }

    private void validateAccountLayout(){
        registerButton.setText(Settings.labels.CreateAccount);

        accountEmailField.setVisibility(View.GONE);
        accountNifField.setVisibility(View.GONE);
        accountAgreementField.setVisibility(View.GONE);


        newPassword.setVisibility(View.VISIBLE);
        rePassword.setVisibility(View.VISIBLE);

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(new InputValidatorManager().isValidPassword_digit(s.toString())){
                    icon1.setImageResource(R.drawable.ic_validationmark);
                    valid1 = true;
                }else{
                    icon1.setImageResource(R.drawable.ic_crossmark);
                }

                if(new InputValidatorManager().isValidPassword_lowerCase(s.toString())){
                    icon2.setImageResource(R.drawable.ic_validationmark);
                    valid2 = true;
                }else{
                    icon2.setImageResource(R.drawable.ic_crossmark);
                }

                if(new InputValidatorManager().isValidPassword_upperCase(s.toString())){
                    icon3.setImageResource(R.drawable.ic_validationmark);
                    valid3 = true;
                }else{
                    icon3.setImageResource(R.drawable.ic_crossmark);
                }

                if(new InputValidatorManager().isValidPassword_size(s.toString())){
                    icon4.setImageResource(R.drawable.ic_validationmark);
                    valid4 = true;
                }else{
                    icon4.setImageResource(R.drawable.ic_crossmark);
                }

                if(new InputValidatorManager().isValidPassword_specialCharacter(s.toString())){
                    icon5.setImageResource(R.drawable.ic_validationmark);
                    valid5 = true;
                }else{
                    icon5.setImageResource(R.drawable.ic_crossmark);
                }

                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        rePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(newPassword.getText().toString())){
                    icon6.setImageResource(R.drawable.ic_validationmark);
                    valid6 = true;
                }else{
                    icon6.setImageResource(R.drawable.ic_crossmark);
                }
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void createAccount(){
        EditText accountEmailPhoneField = findViewById(R.id.accountEmail);
        EditText accountNifField = findViewById(R.id.accountNif);

        CheckBox accountAgreementField = findViewById(R.id.accountCheckboxAgreement);

        String accountEmail = "";
        String accountPhoneNumber = "";

        String userData = accountEmailPhoneField.getText().toString();

        if(InputValidatorManager.isValidEmail(userData)){
            accountEmail = userData;
        }else if (InputValidatorManager.isValidPhone(userData)){
            accountPhoneNumber = userData;
        }

        String accountNif = accountNifField.getText().toString();

        //TODO 3 party info business
        String accountVatin = "";//accountNifField.getText().toString();

        Boolean agreement = accountAgreementField.isChecked();

        AlertDialog.Builder alertMessage = new AlertDialog.Builder(this, R.style.AlertMessageDialog);
        alertMessage.setTitle(Settings.labels.CreateAccount);

        if(agreement){
            CreateUser(accountEmail, accountPhoneNumber, "+351", accountNif, accountVatin, 1, Settings.LangCode.equals("pt") ? 1 : 2);
        }else{
            alertMessage.setMessage("Para criar a conta necessita concordar com as condições.").show();
        }
    }

    private void validateAccount(){

        String accountName = accountNameField.getText().toString();
        String accountPassword = accountPasswordField.getText().toString();
        String accountRePassword = accountRePasswordField.getText().toString();

        ValidateUser(accountName, accountPassword, accountRePassword, Settings.LangCode.equals("pt") ? 1 : 2);
    }

    private boolean DataValidation(String accountEmail, String accountNif, String accountPhone, String accountName){

        InputValidatorManager validator = new InputValidatorManager();

        boolean valid = true;

        if(!validator.isValidEmail(accountEmail)){
            final EditText accountEmailField = findViewById(R.id.accountEmail);
            accountEmailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    accountEmailField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorInactive)));
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
                    accountNifField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorInactive)));
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
                    accountPhoneField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorInactive)));
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
                    accountNameField.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorInactive)));
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

    private void CreateUser(String userEmail, String userPhone, String countryCode, String userNif, String userVatin,  int countryId, int languageId){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        //TODO Change label source
        progressDialog.setMessage("Validando dados...");
        progressDialog.show();

        CreateTemporaryLoginUserRequest request = new CreateTemporaryLoginUserRequest();

        if(userEmail != null && !userEmail.isEmpty()) {
            request.Email = userEmail;
        }

        if(userPhone != null && !userPhone.isEmpty()) {
            request.PhoneNumber = userPhone;
        }

        if(countryCode != null && !countryCode.isEmpty()) {
            request.CountryCode = countryCode;
        }

        if(userNif != null && !userNif.isEmpty()) {
            request.Nif = Integer.valueOf(userNif);
        }

        if(userVatin != null && !userVatin.isEmpty()) {
            request.Vatin = userVatin;
        }

        if(countryId > 0) {
            request.CountryID = 1;
        }

        if(languageId > 0) {
            request.LanguageID = languageId;
        }

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.CreateTemporaryLoginUser), new Gson().toJson(request), true,  new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();
                createUserResponse(response);
            }
        });
    }

    private void ValidateUser(String userName, String password, String confPassword, int languageId){

        CreateLoginUserRequest request = new CreateLoginUserRequest();
        request.FullName = userName;
        request.Password = new MessageEncryption().Encrypt(password, WebApiClient.SITE_KEY);
        request.ConfirmPassword = new MessageEncryption().Encrypt(confPassword, WebApiClient.SITE_KEY);
        request.Token = token;
        request.LanguageID = languageId;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("A criar utilizador...");
        progressDialog.show();

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.CreateLoginUser), new Gson().toJson(request), true,  new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();
                validateUserResponse(response);
            }
        });
    }

    private void createUserResponse(String response) {
        final String message = WebApiMessages.DecryptMessage(response);

        final AlertDialog.Builder alertMessage = new AlertDialog.Builder(this, R.style.AlertMessageDialog);
        alertMessage.setTitle(Settings.labels.CreateAccount);

        JSONObject responseMessage = null;

        try {
            responseMessage = new JSONObject(message);

            CreateTemporaryLoginUserResponse responseData = null;

            if (responseMessage.has("ResponseData")) {
                responseData = new Gson().fromJson(responseMessage.getJSONObject("ResponseData").toString(), CreateTemporaryLoginUserResponse.class);
            }

            Type ReportListType = new TypeToken<ArrayList<ReportList>>() {
            }.getType();


            if (responseMessage.has("ReportList")) {
                List<ReportList> reportList = new Gson().fromJson(responseMessage.getJSONArray("ReportList").toString(), ReportListType);

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < reportList.size(); i++) {
                    sb.append(reportList.get(i).Description);
                    if (i + 1 < reportList.size()) {
                        sb.append("\n");
                    }
                }

                alertMessage.setMessage(sb.toString());
                alertMessage.show();
            }


            //TODO change operationsucces to true case re entry same data
            if (responseData != null ) { //&& responseData.OperationSucess

                if (responseData.EmailSent) {
                    alertMessage.setMessage("Foi enviado um email para a sua conta.");<#
                    alertMessage.show();
                } else if (responseData.SMSSent) {
                    SmsRetrieverClient client = SmsRetriever.getClient(this);
                    Task<Void> task = client.startSmsRetriever();
                    Data.SmsValidationContext = Data.ValidationContext.newAccount;
                    task.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            alertMessage.setMessage("Foi enviada uma mensagem para a seu telemóvel.");
                            alertMessage.show();
                        }
                    });

                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            alertMessage.setMessage("Ocorreu um problema com o pedido, por favor, tente novamente!");
                            alertMessage.show();
                        }
                    });
                }

            }
        } catch (JSONException ex) {

        }

    }

    private void validateUserResponse(String response){
        String message = WebApiMessages.DecryptMessage(response);

        CreateTemporaryLoginUserResponse responseData = new Gson().fromJson(message, CreateTemporaryLoginUserResponse.class);

        if(responseData.OperationSucess){
            validateAccountLayout();
        }else{

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void enableButton(){

        //TODO: Incorporar a validação do restantes textview valid_1 && valid_2 && valid_3 && valid_4 &
        if(valid1 && valid2 && valid3 && valid4 && valid6){
            registerButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
            registerButton.setEnabled(true);
        }else{
            registerButton.setBackgroundColor(Color.parseColor(Settings.colors.Gray2));
            registerButton.setEnabled(false);
        }
    }
}