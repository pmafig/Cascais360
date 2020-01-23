package nsop.neds.cascais360;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.cascais360.Authenticator.AccountGeneral;
import nsop.neds.cascais360.Entities.UserEntity;
import nsop.neds.cascais360.Manager.DetailManager;
import nsop.neds.cascais360.Manager.MenuManager;
import nsop.neds.cascais360.Manager.SessionManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Manager.WeatherManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.MULTIPLY);

        final Intent intent = getIntent();

        int id = intent.getIntExtra(Variables.Id, 0);

        CallEvent(id);

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());

        LinearLayout menuFragment = findViewById(R.id.menu);
        Toolbar toolbar = findViewById(R.id.toolbar);

        LinearLayout backButton = toolbar.findViewById(R.id.menu_back_frame);
        backButton.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        new MenuManager(this, toolbar, menuFragment, " ");
    }


    private void CallEvent(int nid){
        SessionManager sm = new SessionManager(this);
        if(sm.isLoggedOn()){
            UserEntity user = AccountGeneral.getUser(this);
            new DetailManager((TextView)findViewById(R.id.toolbar_title), nid,this,(LinearLayout) findViewById(R.id.detail_frame), (RelativeLayout) findViewById(R.id.loadingPanel))
                    .execute(WebApiCalls.getDetail(nid, user.getSsk(), user.getUserId()));
        }else{
            new DetailManager((TextView)findViewById(R.id.toolbar_title), nid, this, (LinearLayout) findViewById(R.id.detail_frame), (RelativeLayout) findViewById(R.id.loadingPanel))
                    .execute(WebApiCalls.getDetail(nid, "", ""));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
