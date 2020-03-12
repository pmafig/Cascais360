package nsop.neds.mycascais.Manager;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Entities.Json.Detail;
import nsop.neds.mycascais.Entities.Json.Event;
import nsop.neds.mycascais.Entities.Json.Point;
import nsop.neds.mycascais.Entities.Json.User;
import nsop.neds.mycascais.Entities.WebApi.Response;
import nsop.neds.mycascais.LoginActivity;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.NoDataActivity;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.RefreshTokenActivity;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;
import nsop.neds.mycascais.WebApi.WebApiMethods;

public class DetailManager extends AsyncTask<String, Void, Detail> {

    private static final int PERMISSIONS_REQUEST_WRITE_CALENDAR = 2;

    TextView title;
    RelativeLayout loading;
    LinearLayout mainContent;
    Context context;
    int nid;

    public DetailManager(TextView titleView, int nid, Context context, LinearLayout mainContent, RelativeLayout loading){
        this.nid = nid;
        this.context = context;
        this.mainContent = mainContent;
        this.loading = loading;
        this.title = titleView;
    }

    @Override
    protected Detail doInBackground(String... strings) {
        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);


            SessionManager sm = new SessionManager(context);

            if(response != null) {

                JSONObject responseData = response.getJSONObject(Variables.ResponseData);

                JSONObject jsonObject = null;

                if(sm.isLoggedOn()) {
                    User user = new Gson().fromJson(responseData.toString(), User.class);

                    if(user.InvalidSession){
                        Intent intent = new Intent(context, NoDataActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Variables.Id, this.nid);
                        context.startActivity(intent);
                    }else {
                        final JSONObject jsonDetail = responseData.getJSONObject(Variables.ContentDetail);
                        jsonObject = jsonDetail.getJSONObject(Variables.Data);
                        String _s = jsonObject.toString();
                        Detail detail = new Gson().fromJson(_s, Detail.class);
                        detail.Like = user.Like;
                        detail.Subscribed = user.Subscribed;
                        return detail;
                    }
                }else{
                    jsonObject = responseData.getJSONObject(Variables.Data);
                    String _s = jsonObject.toString();
                    Detail detail = new Gson().fromJson(_s, Detail.class);
                    return detail;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Detail detail) {
        super.onPostExecute(detail);

        try {

            if(detail != null) {

                final SessionManager sm = new SessionManager(context);

                int nid = 0;
                String url = "";

                final LinearLayout like = mainContent.findViewById(R.id.event_ac_heart);
                final LinearLayout notification = mainContent.findViewById(R.id.event_ac_bell);
                final LinearLayout share = mainContent.findViewById(R.id.event_ac_share);
                final LinearLayout calendar = mainContent.findViewById(R.id.event_ac_calendar);

                if (detail.Events != null && detail.Events.size() > 0) {
                    nid = detail.Events.get(0).ID;
                    url = detail.Events.get(0).WebURL;
                    calendar.setVisibility(View.VISIBLE);
                    this.title.setText(detail.Events.get(0).CategoryTheme);
                    LayoutManager.setEvent(context, mainContent, detail.Events.get(0), detail.Like, detail.Subscribed);
                }
                if (detail.Places != null && detail.Places.size() > 0) {
                    nid = detail.Places.get(0).ID;
                    url = detail.Places.get(0).WebURL;
                    this.title.setText(detail.Places.get(0).CategoryTheme);
                    LayoutManager.setPlace(context, mainContent, detail.Places.get(0));
                }
                if (detail.Routes != null && detail.Routes.size() > 0) {
                    nid = detail.Routes.get(0).ID;
                    url = detail.Routes.get(0).WebURL;
                    this.title.setText(detail.Routes.get(0).CategoryTheme);
                    LayoutManager.setRoute(context, mainContent, detail.Routes.get(0));
                }


                final int finalNid = nid;
                final String finalUrl = url;

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!sm.asUserLoggedOn()) {
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.putExtra(Variables.Id, finalNid);
                            context.startActivity(intent);
                        } else {
                            setLike(finalNid);
                        }
                    }
                });

                notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!sm.asUserLoggedOn()) {
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.putExtra(Variables.Id, finalNid);
                            context.startActivity(intent);
                        } else {
                            setNotification(finalNid);
                        }
                    }
                });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "\n\n");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, finalUrl);
                        context.startActivity(Intent.createChooser(sharingIntent, "Title"));
                    }
                });

                calendar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //addReminderInCalendar();
                        Event event = detail.Events.get(0);

                        final Calendar beginTime = Calendar.getInstance();
                        beginTime.set(2019, 11, 4, 15, 30);

                        final Calendar endTime = Calendar.getInstance();
                        beginTime.set(2019, 11, 4, 16, 30);

                        new CalendarManager(context).addevent(event.Title, event.Description, event.Points.get(0).Title);
                    }
                });


                mainContent.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }else{
                context.startActivity(new Intent(context, NoDataActivity.class));
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            //context.startActivity(new Intent(context, NoServiceActivity.class));
        }

        loading.setVisibility(View.GONE);
    }


    public void setLike(int nid){
        AccountManager mAccountManager = AccountManager.get(this.context);
        Account[] availableAccounts  = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        String ssk = mAccountManager.getUserData(availableAccounts[0], "SSK");
        String userId = mAccountManager.getUserData(availableAccounts[0], "UserId");

        String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"NID\":\"%s\"}", ssk, userId, this.nid);

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.cms, WebApiMethods.SETLIKESTATUS), jsonRequest, true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "Lamentamos, não foi possível executar o seu pedido.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                final ImageView like = mainContent.findViewById(R.id.event_ac_heart_icon);

                try {
                    JSONObject response = new JSONObject(WebApiMessages.DecryptMessage(responseString));

                    if(response != null) {
                        JSONObject responseData = response.getJSONObject("ResponseData");

                        if (responseData.getBoolean("IsSet")) {
                            if (like.getColorFilter() != null) {
                                like.setColorFilter(null);
                            } else {
                                like.setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.SRC_ATOP);
                            }
                        } else {
                            like.setColorFilter(null);
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(context, "Lamentamos, não foi possível executar o seu pedido.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void setNotification(int nid){
        AccountManager mAccountManager = AccountManager.get(this.context);
        Account[] availableAccounts  = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        String ssk = mAccountManager.getUserData(availableAccounts[0], "SSK");
        String userId = mAccountManager.getUserData(availableAccounts[0], "UserId");

        String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"NID\":\"%s\"}", ssk, userId, this.nid);

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.cms, WebApiMethods.SETSUBSCRIPTION), jsonRequest, true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "Lamentamos, não foi possível executar o seu pedido.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                final ImageView notification = mainContent.findViewById(R.id.event_ac_bell_icon);

                try {
                    JSONObject response = new JSONObject(WebApiMessages.DecryptMessage(responseString));

                    if(response != null) {
                        JSONObject responseData = response.getJSONObject("ResponseData");

                        if (responseData.getBoolean("IsSet")) {
                            if (notification.getColorFilter() != null) {
                                notification.setColorFilter(null);
                            } else {
                                notification.setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.SRC_ATOP);
                            }
                        } else {
                            notification.setColorFilter(null);
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(context, "Lamentamos, não foi possível executar o seu pedido.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
