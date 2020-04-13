package nsop.neds.mycascais.Manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.DetailActivity;
import nsop.neds.mycascais.Entities.Json.Category;
import nsop.neds.mycascais.Entities.Json.Country;
import nsop.neds.mycascais.Entities.Json.Detail;
import nsop.neds.mycascais.Entities.Json.Event;
import nsop.neds.mycascais.Entities.Json.EventsCategories;
import nsop.neds.mycascais.Entities.Json.Labels;
import nsop.neds.mycascais.Entities.Json.Notifications;
import nsop.neds.mycascais.Entities.Json.NotificationsCategory;
import nsop.neds.mycascais.Entities.Json.Place;
import nsop.neds.mycascais.Entities.Json.PlacesCategories;
import nsop.neds.mycascais.Entities.Json.Route;
import nsop.neds.mycascais.Entities.Json.RoutesCategories;
import nsop.neds.mycascais.Entities.Json.Search;
import nsop.neds.mycascais.Entities.Json.TownCouncils;
import nsop.neds.mycascais.Entities.Json.Weather;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;
import nsop.neds.mycascais.WebApi.WebApiMethods;

public class NotificationsManager extends AsyncTask<String, Void, Search> {

    Context context;
    LinearLayout contents;
    LinearLayout subscriptions;

    public NotificationsManager(Context context, LinearLayout content, LinearLayout subscriptions){
        this.context = context;
        this.contents = content;
        this.subscriptions = subscriptions;
    }

    @Override
    protected Search doInBackground(String... strings) {
        try {
            return setNotifications(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Search content) {
        super.onPostExecute(content);

        if(content != null) {
            if(content.Events != null && content.Events.size() > 0) {
                for (Event e : content.Events) {
                    addNotification(e.Title, e.ID);
                }
            }
            if(content.Places != null && content.Places.size() > 0) {
                for (Place p : content.Places) {
                    addNotification(p.Title, p.ID);
                }
            }
            if(content.Routes != null && content.Routes.size() > 0) {
                for (Route r : content.Routes) {
                    addNotification(r.Title, r.ID);
                }
            }
        }


        for (int i = 0; i < Data.NotificationsCategoryList.size() ; i++){
            NotificationsCategory n = Data.NotificationsCategoryList.get(i);

            for (int e = 0; e < Data.NotificationsEventsCategories.size(); e++){
                EventsCategories event = Data.NotificationsEventsCategories.get(e);

                if(n.CID == event.ID){
                    for (int t = 0; t < Data.Towns.size(); t++){
                        TownCouncils town = Data.Towns.get(t);
                        if(n.TCID == town.ID){
                            addSubscription(String.format("%s -> %s -> %s", Settings.labels.Events, event.Description, town.Description));
                            break;
                        }
                    }
                }
            }



            for (int e = 0; e < Data.NotificationsPlacesCategories.size(); e++){
                PlacesCategories place = Data.NotificationsPlacesCategories.get(e);

                if(n.CID == place.ID){
                    for (int t = 0; t < Data.Towns.size(); t++){
                        TownCouncils town = Data.Towns.get(t);
                        if(n.TCID == town.ID){
                            addSubscription(String.format("%s -> %s -> %s", Settings.labels.Places, place.Description, town.Description));
                            break;
                        }
                    }
                }
            }

            for (int e = 0; e < Data.NotificationsRoutesCategories.size(); e++){
                RoutesCategories route = Data.NotificationsRoutesCategories.get(e);

                if(n.CID == route.ID){
                    for (int t = 0; t < Data.Towns.size(); t++){
                        TownCouncils town = Data.Towns.get(t);
                        if(n.TCID == town.ID){
                            addSubscription(String.format("%s -> %s -> %s", Settings.labels.Routes, route.Description, town.Description));
                            break;
                        }
                    }
                }
            }


        }
    }

    private void addSubscription(String line){
        View block = View.inflate(context, R.layout.block_subscription_item, null);

        final TextView nameField = block.findViewById(R.id.notification_name);
        nameField.setText(line);

        this.subscriptions.addView(block);
    }

    private void addNotification(String name, final int id){
        View block = View.inflate(context, R.layout.block_notification_selected_item, null);

        final TextView nameField = block.findViewById(R.id.notification_name);
        final TextView removeButton = block.findViewById(R.id.notification_remove_button);

        nameField.setText(name);

        nameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Variables.Id, id);
                context.startActivity(intent);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                nameField.setVisibility(View.GONE);
                                removeButton.setVisibility(View.GONE);
                                setNotification(id);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                builder.show().dismiss();
                                break;
                        }
                    }
                };


                builder.setMessage(Settings.labels.UnsubscribedConfirmation).setPositiveButton(Settings.labels.Confirm, dialogClickListener)
                        .setNegativeButton(Settings.labels.Cancel, dialogClickListener).show();

            }
        });

        this.contents.addView(block);
    }


    private Search setNotifications(String data){

        try {
            JSONObject response = CommonManager.getResponseData(data);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");

                String temp = responseData.getString("NodeList");

                String tempCategory = responseData.getString("CategoryList");

                Notifications search = new Gson().fromJson(temp, Notifications.class);

                Type CategoryTypeList = new TypeToken<ArrayList<NotificationsCategory>>() { }.getType();

                Data.NotificationsCategoryList = new Gson().fromJson(tempCategory, CategoryTypeList);

                if(Data.NotificationsCategoryList == null){
                    Data.NotificationsCategoryList = new ArrayList<>();
                }

                return search.Data;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setNotification(int nid){
        AccountManager mAccountManager = AccountManager.get(this.context);
        Account[] availableAccounts  = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        String ssk = mAccountManager.getUserData(availableAccounts[0], "SSK");
        String userId = mAccountManager.getUserData(availableAccounts[0], "UserId");

        String jsonRequest = String.format("{\"ssk\":\"%s\", \"userid\":\"%s\", \"NID\":\"%s\"}", ssk, userId, nid);

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.cms, WebApiMethods.SETSUBSCRIPTION), jsonRequest, true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, Settings.labels.TryAgain, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(context, Settings.labels.SubscriptionRemoved, Toast.LENGTH_LONG).show();
            }
        });
    }
}
