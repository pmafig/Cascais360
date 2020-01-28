package nsop.neds.mycascais;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Entities.Json.Resources;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

public class SettingsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private static final int CALENDAR_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        final LinearLayout menuFragment = findViewById(R.id.menu);

        final Context context = this;
        final SessionManager sm = new SessionManager(this);

        final TextView idioma = findViewById(R.id.app_settings_language);
        final TextView permissions = findViewById(R.id.app_settings_permissions);
        final Switch location = findViewById(R.id.app_settings_location_switch);
        final Switch calendar = findViewById(R.id.app_settings_calendar_switch);
        final TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        permissions.setText(Settings.labels.Permissions);
        //idioma.setText(Settings.labels.);
        location.setText(Settings.labels.Location);
        calendar.setText(Settings.labels.Calendar);

        LinearLayout backButton = toolbar.findViewById(R.id.menu_back_frame);
        backButton.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Spinner spinner = findViewById(R.id.lang_code);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lang_code_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setSelection(sm.getLangCodePosition());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String lastLangCode = Settings.LangCode;

                switch (position) {
                    case 0:
                        Settings.LangCode = "pt";
                        sm.setLangCode("pt");
                        break;
                    case 1:
                        Settings.LangCode = "en";
                        sm.setLangCode("en");
                        break;
                }

                if (!lastLangCode.startsWith(Settings.LangCode)) {
                    sm.setLangCodePosition(position);

                    WebApiClient.get(String.format("/%s/%s", WebApiClient.API.cms, WebApiClient.METHODS.content), String.format("{\"ContentType\":\"resources\", \"LangCode\":\"%s\"}", Settings.LangCode), new TextHttpResponseHandler(){
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            String message = WebApiMessages.DecryptMessage(responseString);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            try {
                                JSONObject response = new JSONObject(WebApiMessages.DecryptMessage(responseString));

                                JSONObject responseData = response.getJSONObject("ResponseData");

                                String crc = responseData.getString("CRC");

                                JSONObject jsonObject = responseData.getJSONObject("Data");

                                Resources resources = new Gson().fromJson(jsonObject.toString(), Resources.class);

                                resources.CRC = crc;

                                sm.setResources(resources);

                                permissions.setText(resources.Labels.Permissions);
                                location.setText(resources.Labels.Location);
                                calendar.setText(resources.Labels.Calendar);
                                toolbarTitle.setText(resources.Labels.Settings);

                                Settings.menus = resources.Menu;
                                Settings.labels = resources.Labels;

                                new MenuManager(getBaseContext(), toolbar, menuFragment, Settings.labels.Settings);

                            }catch (Exception ex){}
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ImageView locationIcon = findViewById(R.id.app_settings_location_icon);
        locationIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));
        ImageView calendarIcon = findViewById(R.id.app_settings_calendar_icon);
        calendarIcon.setColorFilter(Color.parseColor(Settings.colors.YearColor));




        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location.isChecked()){
                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
                }else{
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {



                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    startActivity(new Intent());
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    location.setChecked(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("A app será fechada").setPositiveButton("Sim", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).show();


                }
            }
        });



        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendar.isChecked()){
                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, CALENDAR_REQUEST_CODE);
                }else{
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    startActivity(new Intent());
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    calendar.setChecked(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("A app será fechada").setPositiveButton("Sim", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).show();

                }
            }
        });

        location.setChecked(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        calendar.setChecked(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED);

        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.parseColor(Settings.colors.Gray2),
                Color.parseColor(Settings.colors.YearColor),
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            location.setThumbTintList(new ColorStateList(states, thumbColors));
            location.setTrackTintList(new ColorStateList(states, thumbColors));

            calendar.setThumbTintList(new ColorStateList(states, thumbColors));
            calendar.setTrackTintList(new ColorStateList(states, thumbColors));
        }

        new MenuManager(this, toolbar, menuFragment, Settings.labels.Settings);
        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {

            Switch location = findViewById(R.id.app_settings_location_switch);

            location.setChecked(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);

            /*if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission was denied. Display an error message.
            }*/
        }else if (requestCode == CALENDAR_REQUEST_CODE) {

            Switch calendar = findViewById(R.id.app_settings_calendar_switch);

            calendar.setChecked(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED);
        }
    }

}
