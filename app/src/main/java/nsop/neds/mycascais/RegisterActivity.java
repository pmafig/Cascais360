package nsop.neds.mycascais;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.Response;
import nsop.neds.mycascais.Entities.Json.ResponseData;
import nsop.neds.mycascais.Entities.WebApi.CreateLoginUserRequest;
import nsop.neds.mycascais.Entities.WebApi.CreateLoginUserResponse;
import nsop.neds.mycascais.Entities.WebApi.CreateTemporaryLoginUserRequest;
import nsop.neds.mycascais.Entities.WebApi.CreateTemporaryLoginUserResponse;
import nsop.neds.mycascais.Entities.Json.ReportList;
import nsop.neds.mycascais.Entities.WebApi.LoginUserRequest;
import nsop.neds.mycascais.Entities.WebApi.ResetLoginRequest;
import nsop.neds.mycascais.Entities.WebApi.ValidateResetLoginUserRequest;
import nsop.neds.mycascais.Entities.WebApi.ValidateResetLoginUserResponse;
import nsop.neds.mycascais.Manager.CommonManager;
import nsop.neds.mycascais.Manager.ControlsManager.InputValidatorManager;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

import static android.accounts.AccountManager.KEY_ERROR_MESSAGE;
import static nsop.neds.mycascais.Authenticator.AccountGeneral.sServerAuthenticate;

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

    EditText accountToken;

    EditText accountEmailField;
    EditText accountNifField;
    EditText accountPhoneField;
    EditText accountNameField;
    EditText accountPasswordField;
    EditText accountRePasswordField;

    CheckBox accountAgreementField;

    LinearLayout accountPolicyPrivacy;
    LinearLayout accountRequirements;

    private Button registerButton;

    LinearLayout menuFragment;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private String token;
    private Boolean recover;

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

        //accountToken = findViewById(R.id.accountToken);

        accountEmailField = findViewById(R.id.accountEmail);
        accountNifField = findViewById(R.id.accountNif);
        accountPhoneField = findViewById(R.id.accountPhone);
        accountNameField = findViewById(R.id.accountName);
        accountPasswordField = findViewById(R.id.accountPassword);
        accountRePasswordField = findViewById(R.id.accountRePassword);

        accountAgreementField = findViewById(R.id.accountCheckboxAgreement);
        accountPolicyPrivacy = findViewById(R.id.accountPolicyPrivacy_frame);

        newPassword = findViewById(R.id.accountPassword);
        rePassword = findViewById(R.id.accountRePassword);

        accountRequirements = findViewById(R.id.regiter_password_requirements);

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

        recover = (Data.RecoverByEmail != null && Data.RecoverByEmail)  || (Data.RecoverBySms != null && Data.RecoverBySms);

        if(bundle != null && bundle.containsKey(Variables.Token)) {
            token = bundle.getString(Variables.Token);
        }else if(appLinkData != null){
            token = appLinkData.getQueryParameter("vt");
        }

        if(bundle != null && bundle.containsKey(Variables.RecoverPassword)) {
            recover = bundle.getBoolean(Variables.RecoverPassword);
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
        accountEmailField.setHint(Settings.labels.Username);
        registerButton.setText(Settings.labels.Continue);

    }

    private void validateAccountLayout(){
        registerButton.setText(Settings.labels.CreateAccount);

        accountEmailField.setVisibility(View.GONE);
        accountNifField.setVisibility(View.GONE);
        accountAgreementField.setVisibility(View.GONE);
        accountPolicyPrivacy.setVisibility(View.GONE);

        accountRequirements.setVisibility(View.VISIBLE);
        accountNameField.setVisibility(recover ? View.GONE : View.VISIBLE);
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

        if(accountAgreementField.isChecked()){
            CreateUser(accountEmail, accountPhoneNumber, "+351", accountNif, accountVatin, 1, Settings.LangCode.equals("pt") ? 1 : 2);
        }else{
            LayoutManager.alertMessage(this, Settings.labels.TermsAgree);
        }
    }

    private void validateAccount(){
        String accountName = accountNameField.getText().toString();
        String accountPassword = accountPasswordField.getText().toString();
        String accountRePassword = accountRePasswordField.getText().toString();

        if(recover != null && recover){
            ValidateResetLoginUser(Data.CurrentAccountName, accountPassword, accountRePassword, Settings.LangCode.equals("pt") ? 1 : 2);
        }else {
            ValidateUser(accountName, accountPassword, accountRePassword, Settings.LangCode.equals("pt") ? 1 : 2);
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
            Data.CurrentAccountName = userEmail;
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
                Toast.makeText(RegisterActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegisterActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();
                validateUserResponse(response);
            }
        });
    }

    private void ValidateResetLoginUser(String userName, String password, String confPassword, int languageId){

        ValidateResetLoginUserRequest request = new ValidateResetLoginUserRequest();
        request.UserName = userName;
        request.Password = new MessageEncryption().Encrypt(password, WebApiClient.SITE_KEY);
        request.ConfirmPassword = new MessageEncryption().Encrypt(confPassword, WebApiClient.SITE_KEY);
        request.Token = token;
        request.LanguageID = languageId;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("A recuperar utilizador...");
        progressDialog.show();

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.ValidateResetLoginUser), new Gson().toJson(request), true,  new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();
                validateResetLoginResponse(response);
            }
        });
    }

    private void createUserResponse(String response) {
        final String message = WebApiMessages.DecryptMessage(response);

        JSONObject responseMessage = null;

        try {
            responseMessage = new JSONObject(message);

            CreateTemporaryLoginUserResponse responseData = null;

            if (responseMessage.has("ResponseData")) {
                responseData = new Gson().fromJson(responseMessage.getJSONObject("ResponseData").toString(), CreateTemporaryLoginUserResponse.class);
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

                LayoutManager.alertMessage(this, Settings.labels.CreateAccount, sb.toString());
            }


            //TODO change operationsucces to true case re entry same data
            if (responseData != null ) { //&& responseData.OperationSucess

                if(responseData.OperationSucess){

                }

                if (responseData.EmailSent) {

                } else if (responseData.SMSSent) {

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

                    Intent verify = new Intent(RegisterActivity.this, ValidateSMSTokenActivity.class);
                    startActivity(verify);
                }

            }
        } catch (JSONException ex) {

        }

    }

    private void validateUserResponse(String response){
        String message = WebApiMessages.DecryptMessage(response);

        JSONObject responseMessage = null;
        try {
            responseMessage = new JSONObject(message);
        }catch (JSONException ex){
            Toast.makeText(this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
        }

        CreateLoginUserResponse responseData = null;

        if(responseMessage.has("ResponseData")) {
            try {
                responseData = new Gson().fromJson(responseMessage.getJSONObject("ResponseData").toString(), CreateLoginUserResponse.class);
            }catch (JSONException ex){
                //TODO add error message
            }
        }

        if(responseData != null && responseData.IsCreated){
            String username = Data.CurrentAccountName;
            String password = accountPasswordField.getText().toString();
            autoLogin(username, password);
        }else {
            Type ReportListType = new TypeToken<ArrayList<ReportList>>() {
            }.getType();



            if (responseMessage.has("ReportList")) {
                try {
                    List<ReportList> reportList = new Gson().fromJson(responseMessage.getJSONArray("ReportList").toString(), ReportListType);


                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < reportList.size(); i++) {
                        sb.append(reportList.get(i).Description);
                        if (i + 1 < reportList.size()) {
                            sb.append("\n");
                        }
                    }

                    LayoutManager.alertMessage(this, Settings.labels.CreateAccount, sb.toString());
                }catch (JSONException ex){}
            }
        }
    }

    private void validateResetLoginResponse(String response){
        String message = WebApiMessages.DecryptMessage(response);

        JSONObject responseMessage = null;
        try {
            responseMessage = new JSONObject(message);
        }catch (JSONException ex){
            Toast.makeText(this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
        }

        ValidateResetLoginUserResponse responseData = null;

        if(responseMessage.has("ResponseData")) {
            try {
                responseData = new Gson().fromJson(responseMessage.getJSONObject("ResponseData").toString(), ValidateResetLoginUserResponse.class);
            }catch (JSONException ex){
                //TODO add error message
            }
        }

        if(responseData != null && responseData.PasswordChanged){
            String username = Data.CurrentAccountName;
            String password = accountPasswordField.getText().toString();
            autoLogin(username, password);
        }else {
            Type ReportListType = new TypeToken<ArrayList<ReportList>>() {
            }.getType();

            if (responseMessage.has("ReportList")) {
                try {
                    List<ReportList> reportList = new Gson().fromJson(responseMessage.getJSONArray("ReportList").toString(), ReportListType);


                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < reportList.size(); i++) {
                        sb.append(reportList.get(i).Description);
                        if (i + 1 < reportList.size()) {
                            sb.append("\n");
                        }
                    }

                    LayoutManager.alertMessage(this, Settings.labels.CreateAccount, sb.toString());
                }catch (JSONException ex){}
            }
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

    private void autoLogin(String userName, String password){
        String encPass = "";

        SessionManager sm = new SessionManager(this);

        LoginUserRequest request = new LoginUserRequest();
        request.UserName = userName;
        request.Password = new MessageEncryption().Encrypt(password, WebApiClient.SITE_KEY);
        request.FirebaseToken = sm.getFirebaseToken();
        request.AppType = 2;
        request.LanguageID = sm.getLangCodePosition() + 1;

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.login), new Gson().toJson(request),true, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String message = WebApiMessages.DecryptMessage(responseString);
                Toast.makeText(RegisterActivity.this, "Conta criada com sucesso.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                String message = WebApiMessages.DecryptMessage(responseString);
                postSuccess(message);
            }
        });
    }

    private void postSuccess(String json){
        Response data = new Gson().fromJson(json, Response.class);

        if(data.ReportList != null && data.ReportList.size() > 0){
            LayoutManager.alertMessage(this, Settings.labels.CreateAccount, data.ReportList.get(0).Description);
        }else {
            if (data.ResponseData.IsAuthenticated) { // ReportManager.isAuthenticated(json)
                try {
                    SessionManager sm = new SessionManager(this);

                    menuFragment.findViewById(R.id.user_loggedon_header).setVisibility(View.VISIBLE);
                    menuFragment.findViewById(R.id.menu_button_login_frame).setVisibility(View.GONE);
                    TextView name = menuFragment.findViewById(R.id.user_name);
                    name.setText(data.ResponseData.DisplayName);

                    sm.setDisplayname(data.ResponseData.DisplayName);
                    sm.setFullName(ReportManager.getFullName(json));
                    sm.setEmail(ReportManager.getEmail(json));

                    if (data.ResponseData.PhoneContacts != null && data.ResponseData.PhoneContacts.size() > 0) {
                        sm.setMobileNumber(data.ResponseData.PhoneContacts.get(0).Number);
                    }

                    sm.setAddress(ReportManager.getAddress(json));

                    submit(data.ResponseData.SSK, data.ResponseData.UserID, data.ResponseData.RefreshToken);
                } catch (Exception e) {
                    AccountGeneral.logout(this);

                    Intent i = new Intent(this, MainActivity.class);
                    i.putExtra(Variables.Autologin, true);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                    //TODO toast message
                    Toast.makeText(this, Settings.labels.LoginSubtitle, Toast.LENGTH_SHORT).show();
                }
            } 
        }
    }

    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    public void submit(String ssk, String userId, String refreshToken) {
        final String userName = ((TextView) findViewById(R.id.accountName)).getText().toString();
        final String userPass = ((TextView) findViewById(R.id.accountPassword)).getText().toString();

        final String accountType = AccountGeneral.ACCOUNT_TYPE;

        String authtoken = null;
        Bundle data = new Bundle();

        try {
            authtoken = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);

            data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
            data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
            data.putString(PARAM_USER_PASS, userPass);
            data.putString("SSK", ssk);
            data.putString("UserId", userId);
            data.putString("RefreshToken", refreshToken);

        } catch (Exception e) {
            data.putString(KEY_ERROR_MESSAGE, e.getMessage());
        }

        final Intent res = new Intent();
        res.putExtras(data);

        if (res.hasExtra(KEY_ERROR_MESSAGE)) {
            Toast.makeText(getBaseContext(), res.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
        } else {
            finishLogin(res);
        }
    }

    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);

        final Account account = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);

        AccountManager mAccountManager = AccountManager.get(this);

        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, true)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            mAccountManager.addAccountExplicitly(account, accountPassword, intent.getExtras());
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        }else{
            mAccountManager.setPassword(account, accountPassword);
        }

        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra(Variables.Autologin, true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}