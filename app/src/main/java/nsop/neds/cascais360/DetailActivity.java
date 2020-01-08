package nsop.neds.cascais360;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import nsop.neds.cascais360.Authenticator.AccountGeneral;
import nsop.neds.cascais360.Entities.UserEntity;
import nsop.neds.cascais360.Manager.DetailManager;
import nsop.neds.cascais360.Manager.SessionManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.WebApi.WebApiCalls;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Intent intent = getIntent();

        CallEvent(intent.getIntExtra(Variables.Id, 0));
    }


    private void CallEvent(int nid){
        SessionManager sm = new SessionManager(this);
        if(sm.asUserLoggedOn()){
            UserEntity user = AccountGeneral.getUser(this);
            new DetailManager(nid,
                    this,
                    (LinearLayout) findViewById(R.id.detail_frame),
                    (RelativeLayout) findViewById(R.id.loadingPanel)
            ).execute(WebApiCalls.getDetail(nid, user.getSsk(), user.getUserId()));
        }else{
            new DetailManager(nid,
                    this,
                    (LinearLayout) findViewById(R.id.detail_frame),
                    (RelativeLayout) findViewById(R.id.loadingPanel)
            ).execute(WebApiCalls.getDetail(nid, "", ""));
        }
    }
}
