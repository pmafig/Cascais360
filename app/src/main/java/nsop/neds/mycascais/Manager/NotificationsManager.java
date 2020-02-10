package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import nsop.neds.mycascais.DetailActivity;
import nsop.neds.mycascais.Entities.Json.Detail;
import nsop.neds.mycascais.Entities.Json.Event;
import nsop.neds.mycascais.Entities.Json.Notifications;
import nsop.neds.mycascais.Entities.Json.Place;
import nsop.neds.mycascais.Entities.Json.Route;
import nsop.neds.mycascais.Entities.Json.Search;
import nsop.neds.mycascais.Entities.Json.Weather;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.Settings.Settings;

public class NotificationsManager extends AsyncTask<String, Void, Search> {

    Context context;
    LinearLayout content;

    public NotificationsManager(Context context, LinearLayout content){
        this.context = context;
        this.content = content;
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
    }

    private void addNotification(String name, final int ID){
        View block = View.inflate(context, R.layout.block_notification_selected_item, null);

        final TextView nameField = block.findViewById(R.id.notification_name);
        final TextView removeButton = block.findViewById(R.id.notification_remove_button);

        nameField.setText(name);

        nameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Variables.Id, ID);
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

        this.content.addView(block);
    }


    private Search setNotifications(String data){

        try {
            JSONObject response = CommonManager.getResponseData(data);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");

                String temp = responseData.getString("NodeList");

                Notifications search = new Gson().fromJson(temp, Notifications.class);

                return search.Data;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
