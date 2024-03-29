package nsop.neds.mycascais;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import nsop.neds.mycascais.Entities.Json.Email;
import nsop.neds.mycascais.Entities.Json.Labels;
import nsop.neds.mycascais.Entities.Json.PhoneContacts;
import nsop.neds.mycascais.Entities.Json.ReportList;
import nsop.neds.mycascais.Entities.Json.Response;
import nsop.neds.mycascais.Entities.Json.User;
import nsop.neds.mycascais.Entities.Json.ValidationStates;
import nsop.neds.mycascais.Entities.UserEntity;
import nsop.neds.mycascais.Entities.WebApi.ChangeEntityValidationStateResponse;
import nsop.neds.mycascais.Entities.WebApi.CreateLoginUserResponse;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Manager.Broadcast.AppSignatureHelper;
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
import nsop.neds.mycascais.WebApi.WebApiMethods;

public class EditAccountActivity extends AppCompatActivity {

    LinearLayout menuFragment;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private SessionManager sm;

    private boolean addEmail = false;
    private boolean addPhoneNumber = false;

    private LoginUserResponse user;

    ProgressDialog progressDialog;

    boolean userEmailLogin = false;
    boolean userPhoneNumberLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        sm = new SessionManager(this);

        progressDialog = new ProgressDialog(this);

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

        new MenuManager(this, toolbar, menuFragment, Settings.labels.MyProfile);


        TextView title = findViewById(R.id.account_data_title);
        title.setText(Settings.labels.PersonalData);
        title.setTextColor(Color.parseColor(Settings.colors.YearColor));

        Button editButton = findViewById(R.id.editAccount);
        editButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
        Button editCancelButton = findViewById(R.id.editAccountCancel);
        editCancelButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
        Button editSubmitButton = findViewById(R.id.editAccountSubmit);
        editSubmitButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));

        ImageView starEmailAuth = findViewById(R.id.accountEmailValidationImage);
        TextView labelEmailAuth = findViewById(R.id.accountEmailValidationLabel);

        ImageView starPhoneAuth = findViewById(R.id.accountPhoneValidationImage);
        TextView labelPhoneAuth = findViewById(R.id.accountPhoneValidationLabel);


        String userPhoneNumber = "";
        userPhoneNumberLogin = false;
        boolean userPhoneNumberValid = false;

        String userEmail = "";
        userEmailLogin = false;
        boolean userEmailValid = false;

        if(sm != null){
            user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);
        }

        if(user.IsAuthenticated) {
            //PhoneNumber
            if(userPhoneNumber.isEmpty()) {
                for (PhoneContacts phone : user.PhoneContacts) {
                    if (phone.Login) {
                        userPhoneNumber = phone.Number;
                        userPhoneNumberLogin = true;
                        userPhoneNumberValid = phone.ValidationStates != null && phone.ValidationStates.size() > 0 && phone.ValidationStates.get(0).TypeID == 2;
                        break;
                    }
                }
            }

            if(userPhoneNumber.isEmpty()) {
                for (PhoneContacts phone : user.PhoneContacts) {
                    if (phone.Main) {
                        userPhoneNumber = phone.Number;
                        userPhoneNumberValid = phone.ValidationStates.size() > 0 && phone.ValidationStates.get(0).TypeID == 2;
                        break;
                    }
                }
            }

            if(userPhoneNumber.isEmpty()) {
                if(user.PhoneContacts.size() > 0){
                    userPhoneNumber = user.PhoneContacts.get(0).Number;
                    userPhoneNumberValid = user.PhoneContacts.get(0).ValidationStates != null &&  user.PhoneContacts.get(0).ValidationStates.size() > 0 && user.PhoneContacts.get(0).ValidationStates.get(0).TypeID == 2;
                }
            }

            if(!userPhoneNumber.isEmpty()){
                labelPhoneAuth.setVisibility(View.VISIBLE);
            }

            //Email
            if(userEmail.isEmpty()) {
                for (Email email : user.Emails) {
                    if (email.Login) {
                        userEmail = email.EmailAddress;
                        userEmailLogin = true;
                        userEmailValid = email.ValidationStates.size() > 0 && email.ValidationStates.get(0).TypeID == 2;
                        break;
                    }
                }
            }

            if(userEmail.isEmpty()) {
                for (Email email : user.Emails) {
                    if (email.Main) {
                        userEmail = email.EmailAddress;
                        userEmailValid = email.ValidationStates.size() > 0 && email.ValidationStates.get(0).TypeID == 2;
                        break;
                    }
                }
            }

            if(userEmail.isEmpty()) {
                if(user.Emails.size() > 0){
                    userEmail = user.Emails.get(0).EmailAddress;
                    userEmailValid = user.Emails.get(0).ValidationStates.size() > 0 && user.Emails.get(0).ValidationStates.get(0).TypeID == 2;
                }
            }

            if(!userEmail.isEmpty()){
                labelEmailAuth.setVisibility(View.VISIBLE);
            }

        }

        if(userPhoneNumberLogin) {
            starPhoneAuth.setVisibility(View.VISIBLE);
            starPhoneAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EditAccountActivity.this, Settings.labels.AuthenticationContact, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            starPhoneAuth.setVisibility(View.INVISIBLE);
        }

        labelPhoneAuth.setText(userPhoneNumberValid ? Settings.labels.Validated : Settings.labels.NotValidated);
        labelPhoneAuth.setTextColor(userPhoneNumberValid ? Color.parseColor("#00FF00") : Color.parseColor("#FF0000"));


        if(userEmailLogin){
            starEmailAuth.setVisibility(View.VISIBLE);
            starEmailAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EditAccountActivity.this, Settings.labels.AuthenticationContact, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            starEmailAuth.setVisibility(View.INVISIBLE);
        }

        labelEmailAuth.setText(userEmailValid ? Settings.labels.Validated : Settings.labels.NotValidated);
        labelEmailAuth.setTextColor(userEmailValid ? Color.parseColor("#00FF00") : Color.parseColor("#FF0000"));


        EditText name = findViewById(R.id.accountName);
        //TODO change displayName to FullName
        name.setHint(user.DisplayName);
        //name.setFocusable(false);

        EditText email = findViewById(R.id.accountEmail);
        email.setHint(userEmail);
        //email.setFocusable(false);

        //EditText address = findViewById(R.id.accountAddress);
        //address.setHint(sm.getAddress());
        //address.setFocusable(false);

        EditText phone = findViewById(R.id.accountPhone);
        phone.setHint(userPhoneNumber);//);

        View view = this.findViewById(R.id.accountPhone);

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        if(userEmailLogin && userPhoneNumberLogin){
            editButton.setVisibility(View.GONE);
        }


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAccount();
            }
        });

        editCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAccount();
            }
        });

        editSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addPhoneNumber || addEmail) {
                    validateChangeUserData();
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void editAccount(){
        EditText accountPhoneField = findViewById(R.id.accountPhone);
        EditText accountEmailField = findViewById(R.id.accountEmail);

        String contact = accountPhoneField.getHint().toString();
        String email = accountEmailField.getHint().toString();

        if(!userPhoneNumberLogin) {
            addPhoneNumber = true;

            accountPhoneField.setEnabled(true);
            accountPhoneField.setBackground(getDrawable(R.drawable.mycascais_edittext));
            accountPhoneField.requestFocus();

            findViewById(R.id.editAccountSubmitFrame).setVisibility(View.VISIBLE);
            findViewById(R.id.editAccount).setVisibility(View.GONE);
        }

        if(!userEmailLogin){
            addEmail = true;

            accountEmailField.setEnabled(true);
            accountEmailField.setBackground(getDrawable(R.drawable.mycascais_edittext));
            accountEmailField.requestFocus();

            findViewById(R.id.editAccountSubmitFrame).setVisibility(View.VISIBLE);
            findViewById(R.id.editAccount).setVisibility(View.GONE);
        }

        if(userEmailLogin && userPhoneNumberLogin){
            LayoutManager.alertMessage(this, "Não é possível alterar os dados.");
        }
    }

    private void cancelAccount(){
        if(addPhoneNumber) {
            EditText accountPhoneField = findViewById(R.id.accountPhone);
            accountPhoneField.setEnabled(false);
            accountPhoneField.setHint(sm.getMobileNumber());
            accountPhoneField.setBackground(getDrawable(R.drawable.border_bottom));

            findViewById(R.id.editAccount).setVisibility(View.VISIBLE);
            findViewById(R.id.editAccountSubmitFrame).setVisibility(View.GONE);
        }else if(addEmail){
            EditText accountEmailField = findViewById(R.id.accountEmail);
            accountEmailField.setEnabled(false);
            accountEmailField.setHint(sm.getEmail());
            accountEmailField.setBackground(getDrawable(R.drawable.border_bottom));

            findViewById(R.id.editAccount).setVisibility(View.VISIBLE);
            findViewById(R.id.editAccountSubmitFrame).setVisibility(View.GONE);
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

        if(validator.isNullOrEmpty(accountName)){
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

    /*private void CreateUser(String userEmail, String userNif, final String userPhone, String userName, String password, String confPassword, String countryId, String countryCode){

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
    }*/

    private void postSuccess(String response){
        String message = WebApiMessages.DecryptMessage(response);

        if(ReportManager.operationSuccess(message)){
            Intent intent = new Intent(EditAccountActivity.this, EmailSentActivity.class);
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

    private void validateChangeUserData(){
        EditText emailField = findViewById(R.id.accountEmail);
        EditText phoneContactField = findViewById(R.id.accountPhone);

        final String phoneContact = phoneContactField.getText().toString();
        final String emailContact = emailField.getText().toString();

        boolean validPhoneNumber = new InputValidatorManager().isValidPhone(phoneContact);
        boolean validEmail = new InputValidatorManager().isValidEmail(emailContact);

        String validationMessage = "";

        if(emailField.isEnabled() && !validEmail){
            validationMessage = Settings.labels.InvalidEmail;
        }

        if(phoneContactField.isEnabled() && !validPhoneNumber){
            if(!validationMessage.isEmpty()){
                validationMessage += "\n";
            }
            validationMessage += Settings.labels.InvalidContact;
        }

        if(!validationMessage.isEmpty()){
            LayoutManager.alertMessage(this, validationMessage);
        }else {

            final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            if(addEmail){
                                addCustomerEmail(true);
                            }else if (addPhoneNumber){

                                boolean _exist = false;
                                LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                                for(PhoneContacts phone : user.PhoneContacts){
                                    if(phone.Number.equals(phoneContact)){
                                        _exist = true;

                                        if(phone.ValidationStates != null && phone.ValidationStates.size() > 0){
                                            ValidationStates validation = phone.ValidationStates.get(0);
                                            if(validation.Active && validation.TypeID == 2){
                                                addAuthPhoneContact(phone.ID);
                                            }else{
                                                changeEntityValidationState(true, phone.ID, phoneContact, false);
                                                break;
                                            }
                                        }else{
                                            changeEntityValidationState(true, phone.ID, phoneContact, false);
                                            break;
                                        }
                                    }
                                }

                                if(!_exist){
                                    addCustomerPhoneContact(true);
                                }
                            }
                            builder.show().dismiss();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            if(addEmail){
                                addCustomerEmail(false);
                            }else if (addPhoneNumber){
                                addCustomerPhoneContact(false);
                            }
                            builder.show().dismiss();
                            break;
                    }
                }
            };

            builder.setMessage(Settings.labels.AskForAuthentication).setPositiveButton(Settings.labels.Yes, dialogClickListener)
                    .setNegativeButton(Settings.labels.No, dialogClickListener).show();
        }
    }

    private void addCustomerEmail(final boolean isAuthenticator){
        UserEntity user = AccountGeneral.getUser(this);
        EditText emailField = findViewById(R.id.accountEmail);

        final String emailContact = emailField.getText().toString();

        boolean validEmail = new InputValidatorManager().isValidEmail(emailContact);

        String validationMessage = "";

        if(emailField.isEnabled() && !validEmail){
            if(!validationMessage.isEmpty()){
            }
            validationMessage = Settings.labels.InvalidEmail;
        }

        if(!validationMessage.isEmpty()){
            LayoutManager.alertMessage(this, validationMessage);
        }else {

            progressDialog.setMessage(Settings.labels.AddingEmail);
            progressDialog.show();

            if (isAuthenticator) {
                Data.SmsValidationContext = Data.ValidationContext.addAuth;
            }

            LoginUserResponse loginUserResponse = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);;

            String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"Email\":\"%s\", LanguageID:%s}",
                    loginUserResponse.SSK, loginUserResponse.AuthID, emailContact, sm.getLangCodePosition() + 1);

            WebApiClient.post(String.format("/%s/%s", WebApiClient.API.crm, WebApiMethods.ADDCUSTOMEREMAIL), jsonRequest, true, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    progressDialog.dismiss();
                    LayoutManager.alertMessage(EditAccountActivity.this, Settings.labels.TryAgain);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    AppSignatureHelper appSignatureHelper = new AppSignatureHelper(EditAccountActivity.this);
                    appSignatureHelper.getAppSignatures();

                    String message = WebApiMessages.DecryptMessage(response);

                    JSONObject jsonMessage = null;
                    try {
                        jsonMessage = new JSONObject(message);
                    } catch (JSONException ex) {
                        Toast.makeText(EditAccountActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();

                    }

                    ChangeEntityValidationStateResponse responseData = null;

                    if (jsonMessage.has(Variables.ResponseData)) {
                        try {

                            if (jsonMessage.has("ReportList") && !jsonMessage.isNull("ReportList")) {

                                String receivedMessage = "";

                                Type ReportListType = new TypeToken<ArrayList<ReportList>>() {}.getType();

                                List<ReportList> reportList = new Gson().fromJson(jsonMessage.getJSONArray(Variables.ReportList).toString(), ReportListType);

                                if(reportList.size() > 0) {
                                    StringBuilder sb = new StringBuilder();

                                    for (int i = 0; i < reportList.size(); i++) {
                                        sb.append(reportList.get(i).Description);
                                        if (i + 1 < reportList.size()) {
                                            sb.append("\n");
                                        }
                                    }

                                    if (sb.length() > 0) {
                                        receivedMessage = sb.toString();
                                    } else {
                                        Toast.makeText(EditAccountActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                                    }

                                    progressDialog.dismiss();
                                    LayoutManager.alertMessage(EditAccountActivity.this, Settings.labels.CreateAccount, receivedMessage);
                                }
                            }

                            responseData = new Gson().fromJson(jsonMessage.getJSONObject(Variables.ResponseData).toString(), ChangeEntityValidationStateResponse.class);

                            if (responseData.IsAdded) {

                                LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                                Email  addedEmail = new Email();
                                addedEmail.EmailAddress = emailContact;

                                ValidationStates validation = new ValidationStates();
                                validation.TypeID = isAuthenticator ? 2 : 1;
                                validation.Active = true;

                                addedEmail.ValidationStates = new ArrayList<>();
                                addedEmail.ValidationStates.add(validation);

                                user.Emails.add(addedEmail);

                                sm.setUser(new Gson().toJson(user));

                                changeEntityValidationState(isAuthenticator, responseData.EmailID, emailContact, true);
                            } else {
                                //TODO add error message
                            }


                        } catch (JSONException ex) {
                            //TODO add error message
                        }
                    }
                }
            });
        }
    }

    private void addCustomerPhoneContact(final boolean isAuthenticator){
        UserEntity user = AccountGeneral.getUser(this);
        EditText phoneContactField = findViewById(R.id.accountPhone);

        final String phoneContact = phoneContactField.getText().toString();
        final String mobileNumber = new SessionManager(this).getMobileNumber();

        LoginUserResponse loginUser = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

        boolean validPhoneNumber = new InputValidatorManager().isValidPhone(phoneContact);

        String validationMessage = "";

        /*if(mobileNumber.startsWith(phoneContact)){
            validationMessage = Settings.labels.ChangePhoneNumberMessageMustBeDifferent;
        }*/

        if(phoneContactField.isEnabled() && !validPhoneNumber){
            if(!validationMessage.isEmpty()){
                validationMessage += "\n";
            }
            validationMessage += Settings.labels.InvalidContact;
        }

        if(!validationMessage.isEmpty()){
            LayoutManager.alertMessage(this, validationMessage);
        }else {

            progressDialog.setMessage(Settings.labels.AddingPhoneContact);
            progressDialog.show();

            if(isAuthenticator){
                Data.SmsValidationContext = Data.ValidationContext.addAuth;
            }

            String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"CountryCode\":\"%s\", \"PhoneNumber\":\"%s\", \"LanguageID\":%s}",
                    loginUser.SSK, loginUser.AuthID, "+351", phoneContact, sm.getLangCodePosition() + 1);

            WebApiClient.post(String.format("/%s/%s", WebApiClient.API.crm, WebApiMethods.ADDCUSTOMERPHONECONTACT), jsonRequest, true, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    progressDialog.dismiss();
                    LayoutManager.alertMessage(EditAccountActivity.this, Settings.labels.TryAgain);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {

                    String message = WebApiMessages.DecryptMessage(response);

                    JSONObject jsonMessage = null;
                    try {
                        jsonMessage = new JSONObject(message);
                    } catch (JSONException ex) {
                        Toast.makeText(EditAccountActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                    }

                    ChangeEntityValidationStateResponse responseData = null;

                    if (jsonMessage.has(Variables.ResponseData)) {
                        try {

                            if (jsonMessage.has("ReportList") && !jsonMessage.isNull("ReportList")) {

                                String receivedMessage = "";

                                Type ReportListType = new TypeToken<ArrayList<ReportList>>() {}.getType();

                                List<ReportList> reportList = new Gson().fromJson(jsonMessage.getJSONArray(Variables.ReportList).toString(), ReportListType);

                                if(reportList.size() > 0) {
                                    StringBuilder sb = new StringBuilder();

                                    for (int i = 0; i < reportList.size(); i++) {
                                        sb.append(reportList.get(i).Description);
                                        if (i + 1 < reportList.size()) {
                                            sb.append("\n");
                                        }
                                    }

                                    if (sb.length() > 0) {
                                        receivedMessage = sb.toString();
                                    } else {
                                        Toast.makeText(EditAccountActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                                    }

                                    progressDialog.dismiss();
                                    LayoutManager.alertMessage(EditAccountActivity.this, Settings.labels.CreateAccount, receivedMessage);
                                }
                            }

                            responseData = new Gson().fromJson(jsonMessage.getJSONObject(Variables.ResponseData).toString(), ChangeEntityValidationStateResponse.class);

                            if (responseData.IsAdded) {
                                LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                                PhoneContacts addedContact = new PhoneContacts();
                                addedContact.Number = phoneContact;
                                addedContact.ID = responseData.PhoneContactID;

                                if(isAuthenticator){
                                    addedContact.Login = true;
                                }

                                user.PhoneContacts.add(addedContact);

                                sm.setUser(new Gson().toJson(user));

                                changeEntityValidationState(isAuthenticator, responseData.PhoneContactID, phoneContact, false);
                            } else {
                                //TODO add error message
                            }


                        } catch (JSONException ex) {
                            //TODO add error message
                        }
                    }
                }
            });
        }
    }

    private void addAuthPhoneContact(final int phoneID){
        UserEntity user = AccountGeneral.getUser(this);
        final EditText phoneContactField = findViewById(R.id.accountPhone);

        final String phoneContact = phoneContactField.getText().toString();

        boolean validPhoneNumber = new InputValidatorManager().isValidPhone(phoneContact);

        String validationMessage = "";

        if(phoneContactField.isEnabled() && !validPhoneNumber){
            if(!validationMessage.isEmpty()){
                validationMessage += "\n";
            }
            validationMessage += Settings.labels.InvalidContact;
        }

        if(!validationMessage.isEmpty()){
            LayoutManager.alertMessage(this, validationMessage);
        }else {

            progressDialog.setMessage(Settings.labels.AddingPhoneContact);
            progressDialog.show();


            String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"PhoneID\":\"%s\"}",
                    user.getSsk(), user.getUserId(), phoneID);

            WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount,  WebApiClient.METHODS.AddCustomerLogin), jsonRequest, true, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    progressDialog.dismiss();
                    LayoutManager.alertMessage(EditAccountActivity.this, Settings.labels.TryAgain);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {

                    String message = WebApiMessages.DecryptMessage(response);

                    JSONObject jsonMessage = null;
                    try {
                        jsonMessage = new JSONObject(message);
                    } catch (JSONException ex) {
                        Toast.makeText(EditAccountActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                    }

                    ChangeEntityValidationStateResponse responseData = null;

                    if (jsonMessage.has(Variables.ResponseData)) {
                        try {

                            if (jsonMessage.has("ReportList") && !jsonMessage.isNull("ReportList")) {

                                String receivedMessage = "";

                                Type ReportListType = new TypeToken<ArrayList<ReportList>>() {}.getType();

                                List<ReportList> reportList = new Gson().fromJson(jsonMessage.getJSONArray(Variables.ReportList).toString(), ReportListType);

                                if(reportList.size() > 0) {
                                    StringBuilder sb = new StringBuilder();

                                    for (int i = 0; i < reportList.size(); i++) {
                                        sb.append(reportList.get(i).Description);
                                        if (i + 1 < reportList.size()) {
                                            sb.append("\n");
                                        }
                                    }

                                    if (sb.length() > 0) {
                                        receivedMessage = sb.toString();
                                    } else {
                                        Toast.makeText(EditAccountActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                                    }

                                    progressDialog.dismiss();
                                    LayoutManager.alertMessage(EditAccountActivity.this, Settings.labels.CreateAccount, receivedMessage);
                                }
                            }

                            responseData = new Gson().fromJson(jsonMessage.getJSONObject(Variables.ResponseData).toString(), ChangeEntityValidationStateResponse.class);

                            if (responseData.IsAdded) {
                                LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                                for(int i = 0; i < user.PhoneContacts.size() ; i++){
                                    if(user.PhoneContacts.get(i).ID == phoneID){
                                            if(user.PhoneContacts.get(i).ValidationStates != null &&
                                                    user.PhoneContacts.get(i).ValidationStates.size() > 0){

                                            if(user.PhoneContacts.get(i).ValidationStates.get(0).Active){
                                                user.PhoneContacts.get(i).ValidationStates.get(0).TypeID = 2;
                                                user.PhoneContacts.get(i).Login = true;
                                                phoneContactField.setEnabled(false);
                                                ImageView starPhoneAuth = findViewById(R.id.accountPhoneValidationImage);
                                                starPhoneAuth.setVisibility(View.VISIBLE);
                                                starPhoneAuth.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Toast.makeText(EditAccountActivity.this, Settings.labels.AuthenticationContact, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                Button editSubmitButton = findViewById(R.id.editAccountSubmit);
                                                Button editCancelButton = findViewById(R.id.editAccountCancel);
                                                editSubmitButton.setVisibility(View.GONE);
                                                editCancelButton.setVisibility(View.GONE);

                                                EditText accountPhoneField = findViewById(R.id.accountPhone);
                                                accountPhoneField.setEnabled(false);
                                                accountPhoneField.setBackground(getDrawable(R.drawable.border_bottom));
                                            }
                                        }
                                    }
                                }

                                sm.setUser(new Gson().toJson(user));

                            } else {
                                //TODO add error message
                            }


                        } catch (JSONException ex) {
                            //TODO add error message
                        }
                    }
                }
            });
        }
    }

    private void changeEntityValidationState(final boolean isAuthenticator, final int id, final String mobileNumber, final boolean email){
        UserEntity user = AccountGeneral.getUser(this);

        LoginUserResponse loginUser = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

        if(isAuthenticator){
            Data.SmsValidationContext = Data.ValidationContext.addAuth;
            sm.isAuth();
            sm.setEmailId(id);
        }

        String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"EntityID\":\"%s\", \"LanguageID\":%s}",
                loginUser.SSK, loginUser.AuthID, id, sm.getLangCodePosition() + 1);

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.crm, WebApiMethods.CHANGEENTITYVALIDATIONSTATE), jsonRequest, true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
                LayoutManager.alertMessage(EditAccountActivity.this, Settings.labels.TryAgain);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {

                AppSignatureHelper appSignatureHelper = new AppSignatureHelper(EditAccountActivity.this);
                appSignatureHelper.getAppSignatures();

                progressDialog.dismiss();

                final String message = WebApiMessages.DecryptMessage(response);

                JSONObject jsonMessage = null;
                String receivedMessage = "";

                try {
                    jsonMessage = new JSONObject(message);
                } catch (JSONException ex) {
                    Toast.makeText(EditAccountActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                }

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

                        if(sb.length() > 0) {
                            receivedMessage = sb.toString();
                        }else{
                            Toast.makeText(EditAccountActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditAccountActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                }

                if(email){
                    if(!receivedMessage.isEmpty()) {

                        LayoutManager.alertMessage(EditAccountActivity.this, receivedMessage);
                    }else{
                        Toast.makeText(EditAccountActivity.this, Settings.labels.AppInMaintenanceMessage, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Data.SmsValidationContext = Data.ValidationContext.changeContact;

                    Intent intent = new Intent(EditAccountActivity.this, ValidateSMSTokenActivity.class);
                    intent.putExtra(Variables.AlertMessage, receivedMessage);
                    intent.putExtra(Variables.MobileNumber, mobileNumber);
                    intent.putExtra(Variables.MobileId, id);
                    intent.putExtra(Variables.IsAuth, isAuthenticator);
                    startActivity(intent);
                }

                /*SmsRetrieverClient client = SmsRetriever.getClient(getBaseContext());

                Task<Void> task = client.startSmsRetriever();

                final String finalReceivedMessage = receivedMessage;
                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Data.SmsValidationContext = Data.ValidationContext.changeContact;

                        Intent intent = new Intent(EditAccountActivity.this, ValidateSMSTokenActivity.class);
                        intent.putExtra(Variables.AlertMessage, finalReceivedMessage);

                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //String test = "ok";
                        //Toast.makeText(AccountActivity.this, getResources().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                    }
                });*/

                }
        });

    }
}