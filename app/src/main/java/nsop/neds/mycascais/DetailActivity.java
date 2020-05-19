package nsop.neds.mycascais;

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

import com.google.gson.Gson;

import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Entities.UserEntity;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Manager.DetailManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.MULTIPLY);

        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null && bundle.containsKey(Variables.Id)){
            String id = bundle.getString(Variables.Id);
            if(id!= null && !id.isEmpty()){
                CallEvent(Integer.valueOf(id));
            }else if(intent.hasExtra(Variables.Id)) {
                CallEvent(intent.getIntExtra(Variables.Id, 0));
            }
        }else{
            CallEvent(intent.getIntExtra(Variables.Id, 0));
        }

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

        LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

        if(sm.isLoggedOn()){
            new DetailManager((TextView)findViewById(R.id.toolbar_title), nid,this,(LinearLayout) findViewById(R.id.detail_frame), (RelativeLayout) findViewById(R.id.loadingPanel))
                    .execute(WebApiCalls.getDetail(nid, user.SSK, user.AuthID));
        }else{
            new DetailManager((TextView)findViewById(R.id.toolbar_title), nid, this, (LinearLayout) findViewById(R.id.detail_frame), (RelativeLayout) findViewById(R.id.loadingPanel))
                    .execute(WebApiCalls.getDetail(nid));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
