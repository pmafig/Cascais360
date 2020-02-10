package nsop.neds.mycascais;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.UserEntity;
import nsop.neds.mycascais.Manager.CommonManager;
import nsop.neds.mycascais.Manager.ControlsManager.InputValidatorManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMethods;

public class ChangePasswordActivity extends AppCompatActivity {

    LinearLayout menuFragment;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private SessionManager sm;

    private EditText olderPassword;
    private EditText newPassword;
    private EditText rePassword;

    private ImageView icon1;
    private ImageView icon2;
    private ImageView icon3;
    private ImageView icon4;
    private ImageView icon5;
    private ImageView icon6;

    private boolean valid1;
    private boolean valid2;
    private boolean valid3;
    private boolean valid4;
    private boolean valid5;
    private boolean valid6;

    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        TextView title = findViewById(R.id.account_changepassword_title);

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

        title.setTextColor(Color.parseColor(Settings.colors.YearColor));

        title.setText(Settings.labels.PasswordRecoveryNewPassword);
        rule1.setText(Settings.labels.PasswordRuleLength);
        rule2.setText(Settings.labels.PasswordRuleUppercase);
        rule3.setText(Settings.labels.PasswordRuleLowercase);
        rule4.setText(Settings.labels.PasswordRuleNumber);
        rule5.setText(Settings.labels.PasswordRuleSpecial);
        rule6.setText(Settings.labels.PasswordMismatchMessage);

        olderPassword = findViewById(R.id.accountOlderPassword);
        newPassword = findViewById(R.id.accountPassword);
        rePassword = findViewById(R.id.accountRePassword);

        olderPassword.setHint(Settings.labels.OldPassword);
        newPassword.setHint(Settings.labels.Password);
        rePassword.setHint(Settings.labels.Password);

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

        changePasswordButton = findViewById(R.id.changePasswordAccount);
        changePasswordButton.setBackgroundColor(Color.parseColor(Settings.colors.Gray2));
        changePasswordButton.setEnabled(false);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordClick();
            }
        });

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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void changePasswordClick(){
        String older = olderPassword.getText().toString();
        String newone = newPassword.getText().toString();
        String re = rePassword.getText().toString();

        changeAccountPassword(older, newone, re);
    }

    private void changeAccountPassword(String accountOldPassword, String accountNewPassword, String accountRePassword){

        UserEntity user = AccountGeneral.getUser(this);

        String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"OldPassword\":\"%s\", \"Password\":\"%s\", \"ConfirmPassword\":\"%s\"}",
                user.getSsk(), user.getUserId(),  new MessageEncryption().Encrypt(accountOldPassword, WebApiClient.SITE_KEY), new MessageEncryption().Encrypt(accountNewPassword, WebApiClient.SITE_KEY), new MessageEncryption().Encrypt(accountRePassword, WebApiClient.SITE_KEY));

        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Alterando password...");
        progressDialog.show();

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiMethods.CHANGELOGINPASSWORD), jsonRequest, true,  new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
                System.out.println(responseString);
                try {
                    AlertDialog.Builder alertMessage = new AlertDialog.Builder(getBaseContext(), R.style.AlertMessageDialog);
                    alertMessage.setMessage("Lamentamos, ocorreu um erro com o seu pedido.");
                    alertMessage.show();
                } catch (Exception e) {
                    AccountGeneral.logout(ChangePasswordActivity.this);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                progressDialog.dismiss();
                String json = new MessageEncryption().Decrypt(WebApiClient.SITE_KEY, responseString.replace('"', ' ').trim());
                System.out.println(json);

                if (ReportManager.invalidSession(json)) {
                    AccountGeneral.logout(ChangePasswordActivity.this);
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //refreshToken(accountFunc.changePassword, getBaseContext());
                } else {
                    if (ReportManager.getIsSuccess(json)) {
                        if(AccountGeneral.logout(ChangePasswordActivity.this)){
                            startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                        }
                    } else {
                        AlertDialog.Builder alertMessage = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.AlertMessageDialog);
                        String message = ReportManager.getErrorReportList(json);

                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChangePasswordActivity.this);
                        builder.setTitle(Settings.labels.AlertMessage);

                        if (!message.isEmpty()) {
                            alertMessage.setMessage(message.trim());
                        } else {
                            alertMessage.setMessage(Settings.labels.ChangePasswordError);
                        }

                        alertMessage.show();
                    }
                }
            }
        });
    }

    private void enableButton(){
        if(valid1 && valid2 && valid3 && valid4 && valid6){
            changePasswordButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
            changePasswordButton.setEnabled(true);
        }else{
            changePasswordButton.setBackgroundColor(Color.parseColor(Settings.colors.Gray2));
            changePasswordButton.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}