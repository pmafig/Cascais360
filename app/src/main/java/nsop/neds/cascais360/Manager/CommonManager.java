package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiMessages;

public class CommonManager {

    public static final Boolean logOnConsole = true;

    public static String readStream(InputStream bis) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = 0;
        String output = "";

        try {
            result = bis.read();

            while(result != -1) {
                buf.write((byte) result);
                result = bis.read();
            }
            output = buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    public static JSONObject getResponseData(String u){
        try {
            DebugConsoleLog(u);

            URL url = new URL(u);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Settings.timeout);

            try {
                BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = WebApiMessages.DecryptMessage(CommonManager.readStream(in));

                DebugConsoleLog(response);

                return new JSONObject(response);
            } finally {
                urlConnection.disconnect();
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void DebugConsoleLog(String log){
        if(logOnConsole){
            System.out.println(log);
        }
    }

    public static Boolean IsEmptyOrNull(String n){
        return n == null || n.isEmpty();
    }

    public static void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }

    public static Bitmap loadImageBitmap(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream    = context.openFileInput(imageName);
            bitmap      = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 3, Something went wrong!");
            e.printStackTrace();
        }
        return bitmap;
    }

    public void ShowErrorPage(Context context, Exception ex){
        //context.startActivity(new Intent(context, NoServiceActivity.class));
    }
}
