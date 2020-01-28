package nsop.neds.mycascais;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.mycascais.Manager.ListManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;

public class ListActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.MULTIPLY);

        final Intent intent = getIntent();

        CallList(intent.getStringExtra(Variables.Type));

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());



    }

    private void CallList(String type){

        LinearLayout menuFragment = findViewById(R.id.menu);
        Toolbar toolbar = findViewById(R.id.toolbar);

        LinearLayout backButton = toolbar.findViewById(R.id.menu_back_frame);
        backButton.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switch (type){
            case Variables.Agenda:
                new MenuManager(this, toolbar, menuFragment, Settings.labels.Agenda);
                new ListManager(this,
                        (LinearLayout) findViewById(R.id.main_content),
                        (RelativeLayout) findViewById(R.id.loadingPanel)
                ).execute(WebApiCalls.getAgenda());
                break;
            case Variables.Visit:
                new MenuManager(this, toolbar, menuFragment, Settings.labels.Places);
                new ListManager(this,
                        (LinearLayout) findViewById(R.id.main_content),

                        (RelativeLayout) findViewById(R.id.loadingPanel)
                ).execute(WebApiCalls.getVisit());
                break;
            case Variables.Routes:
                new MenuManager(this, toolbar, menuFragment, Settings.labels.Routes);
                new ListManager(this,
                        (LinearLayout) findViewById(R.id.main_content),
                        (RelativeLayout) findViewById(R.id.loadingPanel)
                ).execute(WebApiCalls.getRoute());
                break;
        }
    }
}
