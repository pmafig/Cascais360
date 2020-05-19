package nsop.neds.mycascais.Manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Entities.Json.Detail;
import nsop.neds.mycascais.Entities.Json.Event;
import nsop.neds.mycascais.Entities.Json.RefreshToken;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.LoginActivity;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.NoDataActivity;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;
import nsop.neds.mycascais.WebApi.WebApiMethods;

public class RefreshManager extends AsyncTask<String, Void, LoginUserResponse> {

    private static final int PERMISSIONS_REQUEST_WRITE_CALENDAR = 2;

    TextView title;
    RelativeLayout loading;
    LinearLayout mainContent;
    Context context;
    int nid;

    public RefreshManager(TextView titleView, int nid, Context context, LinearLayout mainContent, RelativeLayout loading){
        this.nid = nid;
        this.context = context;
        this.mainContent = mainContent;
        this.loading = loading;
        this.title = titleView;
    }

    @Override
    protected LoginUserResponse doInBackground(String... strings) {
        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);

            if(response != null) {

                JSONObject responseData = response.getJSONObject(Variables.ResponseData);

                String _s = responseData.toString();

                LoginUserResponse user = new Gson().fromJson(_s, LoginUserResponse.class);

                if (user.SSK != null){
                    SessionManager sm = new SessionManager(context);
                    sm.setUser(_s);
                }

                return user;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final LoginUserResponse refreshUser) {
        super.onPostExecute(refreshUser);

        try {
            if(refreshUser.SSK != null && refreshUser.AuthID != null){
                new DetailManager(title, nid,context,mainContent, loading)
                        .execute(WebApiCalls.getDetail(nid, refreshUser.SSK, refreshUser.AuthID));
            }else{
                new DetailManager(title, nid,context,mainContent, loading)
                        .execute(WebApiCalls.getDetail(nid));
            }

        } catch (Exception e) {
            throw e;
        }

        loading.setVisibility(View.GONE);
    }
}
