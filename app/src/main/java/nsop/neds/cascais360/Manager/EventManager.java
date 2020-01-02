package nsop.neds.cascais360.Manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.cascais360.Authenticator.AccountGeneral;
import nsop.neds.cascais360.Entities.EventDetailEntity;
import nsop.neds.cascais360.Entities.PointEntity;
import nsop.neds.cascais360.LoginActivity;
import nsop.neds.cascais360.MainActivity;
import nsop.neds.cascais360.R;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiClient;
import nsop.neds.cascais360.WebApi.WebApiMessages;
import nsop.neds.cascais360.WebApi.WebApiMethods;


public class EventManager extends AsyncTask<String, Void, EventDetailEntity> {

    final int nid;
    final RelativeLayout loading;
    final LinearLayout mainContent;
    final Context context;
    final ScrollView wrapper;
    final SessionManager sm;

    private EventDetailEntity eventDetail;

    private Boolean invalidSession = false;

    public EventManager(int nid, Context context, LinearLayout mainContent, RelativeLayout loading, ScrollView wrapper){
        this.nid = nid;
        this.loading = loading;
        this.wrapper = wrapper;
        this.context = context;
        this.mainContent = mainContent;
        eventDetail = new EventDetailEntity();
        eventDetail.SetNid(nid);
        sm = new SessionManager(context);
    }

    @Override
    protected EventDetailEntity doInBackground(String... strings) {
        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");

                if (sm.asUserLoggedOn()) {

                    try {
                        invalidSession = responseData.getBoolean("InvalidSession");
                    } catch (Exception e) {
                    }

                    if (invalidSession) {
                        //Intent intent = new Intent(context, RefreshTokenActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        int nid = eventDetail.Nid();
                        //intent.putExtra("nid", nid);
                        //context.startActivity(intent);
                        return null;
                    } else {
                        try {
                            eventDetail.SetLike(Boolean.parseBoolean(responseData.getString("Like")));
                            eventDetail.SetNotification(Boolean.parseBoolean(responseData.getString("Subscribed")));
                        }catch (JSONException e){
                            if(AccountGeneral.logout(context)){
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra(Variables.LogoutSession, true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }else{
                                //Intent intent = new Intent(context, NoServiceActivity.class);
                                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //context.startActivity(intent);
                            }
                        }

                        responseData = responseData.getJSONObject("ContentDetail");
                    }
                }

                JSONObject jsonObject = responseData.getJSONObject("Data");

                JSONArray object;

                try {
                    object = jsonObject.getJSONArray("Events");
                    eventDetail.SetEvent();
                } catch (JSONException je) {
                    try {
                        object = jsonObject.getJSONArray("Places");
                        eventDetail.SetPlace();
                    } catch (JSONException jp) {
                        try {
                            object = jsonObject.getJSONArray("Routes");
                            eventDetail.SetRoute();
                        } catch (JSONException jr) {
                            object = null;
                        }
                    }
                }

                JSONObject obj = object.getJSONObject(0);

                JSONArray categories = obj.getJSONArray("Category");

                if (eventDetail.IsEvent() || eventDetail.IsRoute()) {
                    eventDetail.SetCategoryTheme(obj.getString("CategoryTheme"));
                } else {
                    eventDetail.SetCategoryTheme("Agenda");
                }

                eventDetail.SetTitle(obj.getString("Title"));

                eventDetail.SetDescription(obj.getString("Description"));


                eventDetail.SetCategory(categories.getJSONObject(0).getString("Description"));

                eventDetail.SetImage(obj.getJSONArray("Images").getString(0));

                if (!eventDetail.IsRoute()) {
                    eventDetail.SetShareLink(obj.getString("WebURL"));
                    eventDetail.SetOnlineTicket(obj.getString("OnlineTicket"));
                }

                if (eventDetail.IsEvent() || eventDetail.IsPlace()) {
                    eventDetail.SetPrice((obj.getJSONObject("Price")).getString("Text"));
                    eventDetail.SetPriceValue(Double.parseDouble((obj.getJSONObject("Price")).getString("Value")));

                    try {
                        eventDetail.SetDisplayDate(obj.getJSONArray("NextDates").get(0).toString());

                        JSONArray dates = obj.getJSONArray("NextDates");
                        for (int d = 0; d < dates.length(); d++) {
                            eventDetail.Dates().add(dates.get(d).toString());
                        }
                    }catch (JSONException e){
                        //NON NextDates
                    }

                    try {
                        eventDetail.SetDisplayLocation(((JSONObject) obj.getJSONArray("Points").get(0)).getString("Title"));
                    }catch (JSONException e){
                        eventDetail.SetDisplayLocation("");
                    }

                    try {
                        JSONArray points = obj.getJSONArray("Points");
                        for (int _p = 0; _p < points.length(); _p++){
                            JSONObject p = (JSONObject)points.get(_p);
                            eventDetail.Points().add(new PointEntity(p.getInt("ID"), p.getString("Title"),(p.getJSONObject("TownCouncil")).getString("Description")
                                    ,p.getString("Address"), (p.getJSONObject("Coordinates")).getDouble("Lat"), (p.getJSONObject("Coordinates")).getDouble("Lng")));
                        }

                    }catch (JSONException e){
                        eventDetail.SetDisplayLocation("");
                    }
                }

                if (eventDetail.IsEvent()) {
                    JSONArray tabs = obj.getJSONArray("Tabs");

                    for (int t = 0; t < tabs.length(); t++) {
                        String title = ((JSONObject) tabs.get(t)).getString("Title");
                        String description = ((JSONObject) tabs.get(t)).getString("Description");

                        eventDetail.Tabs().add(title);
                        eventDetail.Tabs().add(description);
                    }
                }

                if (eventDetail.IsRoute()) {
                    JSONArray dif = obj.getJSONArray("Difficulty");
                    eventDetail.GetRoute().SetDifficulty(((JSONObject) dif.get(0)).getString("Description"));
                    eventDetail.GetRoute().SetDuration(obj.getString("Duration"));
                    eventDetail.GetRoute().SetDistance(obj.getString("Distance"));

                    eventDetail.GetRoute().SetPoints(obj.getJSONArray("PointsMap").toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(new Intent(context, MainActivity.class));
        }

        Settings.current_event = eventDetail;

        return eventDetail;
    }

    private List<View> tabs;

    @Override
    protected void onPostExecute(final EventDetailEntity eventDetail) {
        super.onPostExecute(eventDetail);

        /*tabs = new ArrayList<>();

        try {
            final ImageView like = mainContent.findViewById(R.id.event_ac_heart);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!sm.asUserLoggedOn()){
                        context.startActivity(new Intent(context, LoginActivity.class).putExtra("nid", nid));
                    }else {
                       setLike(eventDetail.Nid());
                    }
                }
            });

            final ImageView notification = mainContent.findViewById(R.id.event_ac_bell);
            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!sm.asUserLoggedOn()){
                        context.startActivity(new Intent(context, LoginActivity.class).putExtra("nid", nid));
                    }else {
                        setNotification(eventDetail.Nid());
                    }
                }
            });

            final ImageView share = mainContent.findViewById(R.id.event_ac_share);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "\n\n");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, eventDetail.ShareLink());
                    context.startActivity(Intent.createChooser(sharingIntent,  "Title"));
                }
            });


            TextView category = mainContent.findViewById(R.id.event_category);

            LinearLayout routeBriefing = mainContent.findViewById(R.id.event_route_briefing);
            LinearLayout routeButtons = mainContent.findViewById(R.id.event_route_buttons);
            LinearLayout descriptionWrapper = mainContent.findViewById(R.id.event_description_wrapper);

            LinearLayout dateWrapper = mainContent.findViewById(R.id.event_date_wrapper);
            LinearLayout locationWrapper = mainContent.findViewById(R.id.event_location_wrapper);
            LinearLayout priceWrapper = mainContent.findViewById(R.id.event_price_wrapper);

            if(CommonManager.IsEmptyOrNull(eventDetail.Category())){
                category.setVisibility(View.INVISIBLE);
            }else {
                if(eventDetail.CategoryTheme() != null && eventDetail.CategoryTheme() != "") {
                    category.setText(eventDetail.CategoryTheme().toUpperCase() + " | + " + eventDetail.Category().toUpperCase());
                    category.setTextColor(Color.parseColor(Settings.color));
                }
            }

            TextView title = mainContent.findViewById(R.id.event_title);
            title.setText(eventDetail.Title());

            ImageView img = mainContent.findViewById(R.id.event_image);
            img.setImageBitmap(eventDetail.Image());


            if(eventDetail.Price() != null && !eventDetail.Price().isEmpty() ) {
                TextView price = mainContent.findViewById(R.id.price);
                ImageView euroIcon = mainContent.findViewById(R.id.euro_icon);
                price.setText((Html.fromHtml(eventDetail.Price())).toString());
                euroIcon.setColorFilter(Color.parseColor(Settings.color));
            }

            if(eventDetail.DisplayDate() != null && !eventDetail.DisplayDate().isEmpty()) {
                TextView date = mainContent.findViewById(R.id.event_date);
                ImageView icon = mainContent.findViewById(R.id.date_icon);
                icon.setColorFilter(Color.parseColor(Settings.color));
                date.setText(eventDetail.DisplayDate().trim());
            }

            if(eventDetail.DisplayLocation() != null && !eventDetail.DisplayLocation().isEmpty()) {
                locationWrapper.setVisibility(View.VISIBLE);
                TextView local = mainContent.findViewById(R.id.event_geo);
                ImageView icon = mainContent.findViewById(R.id.location_icon);
                icon.setColorFilter(Color.parseColor(Settings.color));
                local.setText(Html.fromHtml(eventDetail.DisplayLocation().trim()));
            }else{
                locationWrapper.setVisibility(View.GONE);
            }

            if(sm.asUserLoggedOn()){
                ImageView likeImage = mainContent.findViewById(R.id.event_ac_heart);
                ImageView notificationImage = mainContent.findViewById(R.id.event_ac_bell);

                if(eventDetail.Like()) {
                    likeImage.setColorFilter(Color.parseColor(Settings.color), PorterDuff.Mode.SRC_ATOP);
                }

                if(eventDetail.Notification()) {
                    notificationImage.setColorFilter(Color.parseColor(Settings.color), PorterDuff.Mode.SRC_ATOP);
                }
            }

            if(eventDetail.IsEvent()){
                ImageView calendarIcon = mainContent.findViewById(R.id.event_ac_calendar);
                calendarIcon.setVisibility(View.VISIBLE);
            }



            if(eventDetail.IsEvent() || eventDetail.IsPlace()){

                TextView dateLabel = mainContent.findViewById(R.id.date_label);
                TextView moreDatesLabel = mainContent.findViewById(R.id.label_more_dates);
                TextView localityLabel = mainContent.findViewById(R.id.label_locality);
                TextView moreMoreInfoLabel = mainContent.findViewById(R.id.label_more_info);
                TextView priceLabel = mainContent.findViewById(R.id.label_price);

                dateLabel.setTextColor(Color.parseColor(Settings.color));
                moreDatesLabel.setTextColor(Color.parseColor(Settings.color));
                localityLabel.setTextColor(Color.parseColor(Settings.color));
                moreMoreInfoLabel.setTextColor(Color.parseColor(Settings.color));
                priceLabel.setTextColor(Color.parseColor(Settings.color));

                if(eventDetail.DisplayDate() == null || eventDetail.DisplayDate().isEmpty()) {
                    dateWrapper.setVisibility(View.GONE);
                }

                if(eventDetail.Dates().size() > 1) {
                    moreDatesLabel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.event_more_dates);

                            LinearLayout wrapper = (LinearLayout) dialog.findViewById(R.id.more_dates);

                            for(int  d = 0; d < eventDetail.Dates().size(); d++) {
                                View _d = View.inflate(context, R.layout.event_more_date, null);
                                TextView _t = _d.findViewById(R.id.more_date_info);
                                _t.setText(eventDetail.Dates().get(d));
                                wrapper.addView(_d);
                            }


                            dialog.show();
                        }
                    });
                }else{
                    moreDatesLabel.setVisibility(View.GONE);
                }

                if(eventDetail.Points().size() > 0) {
                    moreMoreInfoLabel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.event_more_info);

                            ((TextView) dialog.findViewById(R.id.more_info_title)).setTextColor(Color.parseColor(Settings.color));
                            ((TextView) dialog.findViewById(R.id.more_info_label_name)).setTextColor(Color.parseColor(Settings.color));
                            ((TextView) dialog.findViewById(R.id.more_info_label_town)).setTextColor(Color.parseColor(Settings.color));
                            ((TextView) dialog.findViewById(R.id.more_info_label_geo_location)).setTextColor(Color.parseColor(Settings.color));

                            TextView name = (TextView) dialog.findViewById(R.id.more_info_name);
                            name.setText(eventDetail.Points().get(0).Title());

                            TextView town = (TextView) dialog.findViewById(R.id.more_info_town);
                            town.setText(eventDetail.Points().get(0).Town());

                            TextView location = (TextView) dialog.findViewById(R.id.more_info_geo_location);
                            location.setText(String.format("%s, %s", eventDetail.Points().get(0).Latitude(), eventDetail.Points().get(0).Longitude()));

                            dialog.show();
                        }
                    });
                }else{
                    moreMoreInfoLabel.setVisibility(View.GONE);
                }

                if(eventDetail.Price() == null || eventDetail.Price().isEmpty()) {
                    priceWrapper.setVisibility(View.GONE);
                }else{
                    priceWrapper.setVisibility(View.VISIBLE);
                }
            }else{
                dateWrapper.setVisibility(View.GONE);
                priceWrapper.setVisibility(View.GONE);
            }

            if(eventDetail.IsRoute()){
                routeBriefing.setVisibility(View.VISIBLE);
                routeButtons.setVisibility(View.VISIBLE);

                TextView distance = mainContent.findViewById(R.id.event_route_distance);
                distance.setText(eventDetail.GetRoute().Distance());
                distance.setTextColor(Color.parseColor(Settings.color));

                ImageView distanceIcon = mainContent.findViewById(R.id.event_distance_icon);
                distanceIcon.setColorFilter(Color.parseColor(Settings.color));

                TextView difficulty = mainContent.findViewById(R.id.event_route_difficulty);
                difficulty.setText(eventDetail.GetRoute().Difficulty());
                difficulty.setTextColor(Color.parseColor(Settings.color));

                ImageView difficultyIcon = mainContent.findViewById(R.id.event_difficulty_icon);
                difficultyIcon.setColorFilter(Color.parseColor(Settings.color));

                TextView duration = mainContent.findViewById(R.id.event_route_duration);
                duration.setText(eventDetail.GetRoute().Duration());
                duration.setTextColor(Color.parseColor(Settings.color));

                ImageView durationIcon = mainContent.findViewById(R.id.event_duration_icon);
                durationIcon.setColorFilter(Color.parseColor(Settings.color));

                Button seeMap = mainContent.findViewById(R.id.btn_route_seemap);

                ImageView euroIcon = mainContent.findViewById(R.id.event_euro_icon);
                euroIcon.setColorFilter(Color.parseColor(Settings.color));

                TextView price_Label = mainContent.findViewById(R.id.event_price_label);
                price_Label.setTextColor(Color.parseColor(Settings.color));

                String _p = eventDetail.GetRoute().Points();

                if(!_p.startsWith("[]")) { //Empty Json array

                    Drawable border = context.getDrawable(R.drawable.hit_border);
                    border.setTint(Color.parseColor(Settings.color));
                    seeMap.setBackground(border);

                    seeMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent mapIntent = new Intent(context, MapsActivity.class);
                            mapIntent.putExtra("PointsMap", eventDetail.GetRoute().Points());
                            mapIntent.putExtra("Title", eventDetail.Title());
                            //mapIntent.putExtra("JsonArrayPointsMap", )
                            context.startActivity(mapIntent);
                        }
                    });

                    Button mapRoute = mainContent.findViewById(R.id.btn_route_route);
                    mapRoute.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Settings.color)));
                    mapRoute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            StringBuilder sb = new StringBuilder();

                            try {
                                JSONArray points = new JSONArray(eventDetail.GetRoute().Points());
                                for (int i = 0; i < points.length(); i++) {
                                    JSONObject mapPoint = (JSONObject) points.get(i);
                                    JSONObject point = (JSONObject) mapPoint.getJSONArray("Point").get(0);
                                    JSONObject coord = point.getJSONObject("Coordinates");
                                    sb.append(String.format("%s%s,%s", i == 0 ? "q=" : "&to=", coord.getString("Lat"), coord.getString("Lng")));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Uri uri = Uri.parse("google.navigation:mode=w&" + sb.toString());
                            //Uri uri = Uri.parse("http://maps.google.com/maps?daddr=20.344,34.34&daddr=20.5666,45.345&daddr=20.5666,45.445");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(mapIntent);
                            }
                        }
                    });
                }else{
                    LinearLayout routebuttons = mainContent.findViewById(R.id.event_route_buttons);
                    routebuttons.setVisibility(View.GONE);
                }
            }else{
                routeBriefing.setVisibility(View.GONE);
                routeButtons.setVisibility(View.GONE);
                descriptionWrapper.setVisibility(View.GONE);
            }

            if(eventDetail.Description() != null && !eventDetail.Description().isEmpty() && eventDetail.Description() != "null") {
                TextView descriptionTitle = mainContent.findViewById(R.id.event_description_title);
                descriptionTitle.setTextColor(Color.parseColor(Settings.color));

                descriptionWrapper.setVisibility(View.VISIBLE);
                TextView description = mainContent.findViewById(R.id.event_description_info);
                description.setText((Html.fromHtml(eventDetail.Description())).toString());
            }else{
                descriptionWrapper.setVisibility(View.GONE);
            }

            Button onlineTicket = mainContent.findViewById(R.id.event_ticket);

            /*Drawable _d = new DrawableContainer();
            _d.setTint(Color.parseColor(Settings.color));

            onlineTicket.setBackground(_d);

            if(eventDetail.PriceValue() > 0){
                onlineTicket.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Settings.color)));
                onlineTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent onlineTicket = new Intent(context, WebWrapperActivity.class);
                        onlineTicket.putExtra("link", eventDetail.OnlineTicket());
                        context.startActivity(onlineTicket);

                        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(eventDetail.OnlineTicket()));
                        context.startActivity(browser);
                    }
                });
            }else{
                onlineTicket.setVisibility(View.GONE);
            }

            for(int i = 0; i < eventDetail.Tabs().size(); i++) {
                final View block = View.inflate(context, R.layout.event_tab, null);

                TextView tabTitle = block.findViewById(R.id.tab_title);
                tabTitle.setText(eventDetail.Tabs().get(i++).toUpperCase());

                final WebView tabContent = block.findViewById(R.id.tab_content);
                tabContent.loadData(eventDetail.Tabs().get(i), "text/html", "utf-8");

                tabTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tabs.add(block.findViewById(R.id.tab_title));
                        tabs.add(block.findViewById(R.id.tab_content));

                        if(block.findViewById(R.id.tab_content).getVisibility() == View.VISIBLE) {
                            collapse(block.findViewById(R.id.tab_content));
                            (block.findViewById(R.id.tab_title)).setBackground(ContextCompat.getDrawable(context, R.drawable.tab_event_border_inactive));
                        }else{
                            collapseAll();
                            expand(block.findViewById(R.id.tab_content));
                            (block.findViewById(R.id.tab_title)).setBackground(ContextCompat.getDrawable(context, R.drawable.tab_event_border_active));
                        }
                    }
                });

                LinearLayout wrapper = mainContent.findViewById(R.id.tab_wrapper);
                wrapper.addView(block);

                collapse(block.findViewById(R.id.tab_content));
            }

            collapseAll();

            wrapper.setVisibility(View.VISIBLE);

            loading.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            if(!invalidSession){
                context.startActivity(new Intent(context, NoServiceActivity.class));
            }
        }*/
    }

    public void collapseAll(){
        for(int i = 0; i < tabs.size(); i++){
            //(tabs.get(i++)).setBackground(ContextCompat.getDrawable(context, R.drawable.tab_event_border_inactive));
            collapse(tabs.get(i));
        }
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
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
                /*final ImageView like = mainContent.findViewById(R.id.event_ac_heart);

                try {
                    JSONObject response = new JSONObject(WebApiMessages.DecryptMessage(responseString));

                    if(response != null) {
                        JSONObject responseData = response.getJSONObject("ResponseData");

                        if (responseData.getBoolean("IsSet")) {
                            if (like.getColorFilter() != null) {
                                like.setColorFilter(null);
                            } else {
                                like.setColorFilter(Color.parseColor(Settings.color), PorterDuff.Mode.SRC_ATOP);
                            }
                        } else {
                            like.setColorFilter(null);
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(context, "Lamentamos, não foi possível executar o seu pedido.", Toast.LENGTH_LONG).show();
                }*/
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
                /*final ImageView notification = mainContent.findViewById(R.id.event_ac_bell);

                try {
                    JSONObject response = new JSONObject(WebApiMessages.DecryptMessage(responseString));

                    if(response != null) {
                        JSONObject responseData = response.getJSONObject("ResponseData");

                        if (responseData.getBoolean("IsSet")) {
                            if (notification.getColorFilter() != null) {
                                notification.setColorFilter(null);
                            } else {
                                notification.setColorFilter(Color.parseColor(Settings.color), PorterDuff.Mode.SRC_ATOP);
                            }
                        } else {
                            notification.setColorFilter(null);
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(context, "Lamentamos, não foi possível executar o seu pedido.", Toast.LENGTH_LONG).show();
                }*/
            }
        });
    }
}
