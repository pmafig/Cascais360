package nsop.neds.mycascais.Manager.Broadcast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.ArrayMap;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nsop.neds.mycascais.DetailActivity;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.SplashActivity;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        SessionManager sm = new SessionManager(getApplicationContext());
        sm.setFirebaseToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> params = message.getData();
        JSONObject object = new JSONObject(params);

        String NOTIFICATION_CHANNEL_ID = "360_channel";

        //long pattern[] = {0, 1000, 500, 1000};

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Your Notifications",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            //notificationChannel.setVibrationPattern(pattern);
            //notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            channel.canBypassDnd();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        //String click_action = message.getNotification().getClickAction();
        //Intent intent = new Intent(click_action);

        Bitmap bitmap = null;
        String imageUrl = "";
        String nid = "";
        String title = "";
        String body = "";

        PendingIntent pendingIntent = null;

        try {
            Map<String, String> data = message.getData();

            for(String key : data.keySet()){
                if(key.equals("attachment")){
                    bitmap = getBitmapfromUrl(data.get(key));
                }else if(key.equals("nid")){
                    nid = data.get(key);
                }else if(key.equals("body")){
                    body = data.get(key);
                }else if(key.equals("title")){
                    title = data.get(key);
                }
            }

            //Intent intent = new Intent();
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Variables.Id, nid);

            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            //startActivity(intent);

            /*intent.putExtra(Variables.Id, nid);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);*/
        } catch (Exception   e) {  }

        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setSummaryText(body);
        notiStyle.bigPicture(bitmap);

        notificationBuilder.setAutoCancel(true)
        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
        .setContentTitle(title)
        .setContentText(body)
        .setDefaults(Notification.DEFAULT_ALL)
        .setWhen(System.currentTimeMillis())
        .setLargeIcon(bitmap)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setAutoCancel(true)
        .setStyle(notiStyle)
        .setContentIntent(pendingIntent);

        mNotificationManager.notify(1000, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
