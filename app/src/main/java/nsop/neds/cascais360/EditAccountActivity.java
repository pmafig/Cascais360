package nsop.neds.cascais360;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.se.omapi.Session;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import nsop.neds.cascais360.Authenticator.AccountGeneral;
import nsop.neds.cascais360.Encrypt.MessageEncryption;
import nsop.neds.cascais360.Entities.UserEntity;
import nsop.neds.cascais360.Manager.Broadcast.AppSignatureHelper;
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
import nsop.neds.cascais360.WebApi.WebApiMethods;

public class EditAccountActivity extends AppCompatActivity {

    LinearLayout menuFragment;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        sm = new SessionManager(this);

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

        EditText name = findViewById(R.id.accountName);
        name.setHint(sm.getFullName());
        name.setFocusable(false);

        EditText email = findViewById(R.id.accountEmail);
        email.setHint(sm.getEmail());
        email.setFocusable(false);

        EditText address = findViewById(R.id.accountAddress);
        address.setHint(sm.getAddress());
        address.setFocusable(false);

        EditText phone = findViewById(R.id.accountPhone);
        phone.setHint(sm.getMobileNumber());

        View view = this.findViewById(R.id.accountPhone);

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

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
                changeAppContact();
            }
        });
    }

    private void editAccount(){
        EditText accountPhoneField = findViewById(R.id.accountPhone);
        accountPhoneField.setEnabled(true);
        accountPhoneField.setHint(sm.getMobileNumber());
        accountPhoneField.setBackground(getDrawable(R.drawable.mycascais_edittext));
        accountPhoneField.requestFocus();

        findViewById(R.id.editAccountSubmitFrame).setVisibility(View.VISIBLE);
        findViewById(R.id.editAccount).setVisibility(View.GONE);


    }

    private void cancelAccount(){
        EditText accountPhoneField = findViewById(R.id.accountPhone);
        accountPhoneField.setEnabled(false);
        accountPhoneField.setHint(sm.getMobileNumber());
        accountPhoneField.setBackground(getDrawable(R.drawable.border_bottom));

        findViewById(R.id.editAccount).setVisibility(View.VISIBLE);
        findViewById(R.id.editAccountSubmitFrame).setVisibility(View.GONE);
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

    private void changeAppContact(){
        UserEntity user = AccountGeneral.getUser(this);

        EditText phoneContactField = findViewById(R.id.accountPhone);
        final String phoneContact = phoneContactField.getText().toString();

        String mobileNumber = new SessionManager(this).getMobileNumber();

        boolean validPhoneNumber = new InputValidatorManager().isValidPhone(phoneContact);

        AlertDialog.Builder alertMessage = new AlertDialog.Builder(this, R.style.AlertMessageDialog);

        if(mobileNumber == phoneContact){
            alertMessage.setMessage("O número de telemóvel inserido é igual ao registado.");
            alertMessage.show();
        }else if(!validPhoneNumber){
            alertMessage.setMessage("O número de telemóvel inserido inválido.");
            alertMessage.show();
        }else {

            final ProgressDialog progressDialog = new ProgressDialog(this);

            progressDialog.setMessage("Alterando telemóvel...");
            progressDialog.show();

            String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"CountryCode\":\"%s\", \"PhoneNumber\":\"%s\"}", user.getSsk(), user.getUserId(), "+351", phoneContact);

            WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiMethods.UPDATEMOBILEPHONECONTACT), jsonRequest, true, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    System.out.println(responseString);
                    progressDialog.dismiss();
                    try {
                        AlertDialog.Builder alertMessage = new AlertDialog.Builder(getBaseContext(), R.style.AlertMessageDialog);
                        alertMessage.setMessage("Lamentamos, ocorreu um erro com o seu pedido.");
                        alertMessage.show();
                    } catch (Exception e) {
                        //clearAccount();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {

                    AppSignatureHelper appSignatureHelper = new AppSignatureHelper(EditAccountActivity.this);
                    appSignatureHelper.getAppSignatures();

                    progressDialog.dismiss();

                    String json = WebApiMessages.DecryptMessage(response);

                    SmsRetrieverClient client = SmsRetriever.getClient(getBaseContext());

                    Task<Void> task = client.startSmsRetriever();

                    new SessionManager(getBaseContext()).setMobileNumber(phoneContact);

                    task.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //String test = "ok";
                            //getSupportFragmentManager().beginTransaction().replace(R.id.container, RecoverPhoneCodeFragment.newInstance()).commitNow();
                            //finishAndRemoveTask();
                            //Toast.makeText(AccountActivity.this, String.format(getResources().getString(R.string.info_message_sms_codevalidation), phoneContact), Toast.LENGTH_SHORT).show();
                        }
                    });

                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //String test = "ok";
                            //Toast.makeText(AccountActivity.this, getResources().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}