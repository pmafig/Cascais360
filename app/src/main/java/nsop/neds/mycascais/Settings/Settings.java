package nsop.neds.mycascais.Settings;

import android.widget.TextView;

import java.util.List;

import nsop.neds.mycascais.Entities.EventDetailEntity;
import nsop.neds.mycascais.Entities.Json.Colors;
import nsop.neds.mycascais.Entities.Json.Labels;
import nsop.neds.mycascais.Entities.Json.Menu;

public class Settings {

    public static String LangCode;

    public static Colors colors;
    public static Labels labels;

    public static List<Menu> menus;

    public static String aboutApp;

    public static int timeout = 2000;

    public static int dotsMargin = 5;

    public static int spotLightBottomMargin = 25;

    public static String resourcesChecksum;


    //public static int selected_month;
    //public static String selected_day;
    public static TextView tv_selected_day;

    public static EventDetailEntity current_event;

    public static int screen_heigth;

    public static String SmsHash = "h+6G4vdG51b";

    public static String html = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "<title>WebView9</title>\n" +
            "<meta forua=\"true\" http-equiv=\"Cache-Control\" content=\"max-age=0\"/>\n" +
            "<style type=\"text/css\">\n" +
            "   @font-face {\n" +
            "      font-family: 'Font';\n" +
            "      src:url(\"file:///android_asset/montserrat_regular.otf\")\n" +
            "   }\n" +
            "   body {\n" +
            "      font-family: 'Font';\n" +
            //"      font-size: medium;\n" +
            "      text-align: justify;\n" +
            "      margin: 0;\n" +
            //"      color:#ffffff\n" +
            "   }\n" +
            "</style>\n" +
            "</head>\n" +
            //"<body style=\"background-color:#212121\">\n" +
            "<body>\n" +
            "%s</body>\n" +
            "</html>";

}
