package nsop.neds.mycascais.Manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiMessages;

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


    public static String WebViewFormatRegular(String content){

        String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "<title>WebView9</title>\n" +
                "<meta forua=\"true\" http-equiv=\"Cache-Control\" content=\"max-age=0\"/>\n" +
                "<style type=\"text/css\">\n" +
                "   @font-face {\n" +
                "      font-family: Font;\n" +
                "      src:url(\"https://fonts.gstatic.com/s/montserrat/v14/JTUQjIg1_i6t8kCHKm45_QpRxC7mw9c.woff2\")\n" +
                "   }\n" +
                "   body {\n" +
                "      font-family: Font;\n" +
                //"      font-size: medium;\n" +
                "      text-align: justify;\n" +
                "      margin: 0;\n" +
                "      color:#333\n" +
                "   }\n" +
                "   a {\n" +
                "      font-family: Font;\n" +
                "      color:"+ Settings.colors.YearColor +"\n" +
                "   }\n" +
                "</style>\n" +
                "</head>\n" +
                //"<body style=\"background-color:#212121\">\n" +
                "<body>\n" +
                content +
                "</body>\n" +
                "</html>";

        try {
            return android.util.Base64.encodeToString(html.getBytes("UTF-8"), android.util.Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String WebViewFormatLight(String content){

        float sp = 15;
        //int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, scaledDensity);

        //int sp = Math.round(px * context.getResources().getDisplayMetrics().scaledDensity);

        String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "<title>WebView9</title>\n" +
                "<meta forua=\"true\" http-equiv=\"Cache-Control\" content=\"max-age=0\"/>\n" +
                "<style type=\"text/css\">\n" +
                "   @font-face {\n" +
                "      font-family: Font;\n" +
                "      src:url(\"https://fonts.gstatic.com/s/montserrat/v14/JTURjIg1_i6t8kCHKm45_cJD3gTD_u50.woff2\")\n" +
                "   }\n" +
                "   body {\n" +
                "      font-family: Font;\n" +
                "      font-size:"+ sp  +"px;\n" +
                "      text-align: justify;\n" +
                "      margin: 0;\n" +
                "      color:#000000\n" +
                "   }\n" +
               /* "   p {\n" +
                "      font-family: 'Arial';\n" +
                "   }\n" +*/
                "   a {\n" +
                "      font-family: Font;\n" +
                "      color:"+ Settings.colors.YearColor +"\n" +
                "   }\n" +
                "</style>\n" +
                "</head>\n" +
                //"<body style=\"background-color:#212121\">\n" +
                "<body>\n" +
                content +
                "</body>\n" +
                "</html>";

        try {
            return android.util.Base64.encodeToString(html.getBytes("UTF-8"), android.util.Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";

        //return String.format("<style>body{ margin:0; padding:0;} p{font-family:\"montserrat_light\";}</style><body>%s</body>", content);
    }

    public static String MimeType(){
        return "text/html; charset=utf-8";
    }

    public static String Encoding(){

        return "base64";

        //return "UTF-8";
    }

    public static String ReturnFirstPrice(String price){
        try {
            return price.substring(price.indexOf("<p>"), price.indexOf("</p>")) + "</p>";
        }catch (Exception e){
            return price;
        }
    }

    public static void launchApp(Context context, String packageName, String variable) {
        Intent intent = new Intent();
        intent.setPackage(packageName);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        if(resolveInfos.size() > 0) {
            ResolveInfo launchable = resolveInfos.get(0);
            ActivityInfo activity = launchable.activityInfo;
            ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);

            Intent i = new Intent(Intent.ACTION_MAIN);
            i.putExtra(packageName + ".vault", variable);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(name);

            context.startActivity(i);
        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
