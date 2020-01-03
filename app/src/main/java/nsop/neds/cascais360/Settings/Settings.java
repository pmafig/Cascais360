package nsop.neds.cascais360.Settings;

import android.widget.TextView;

import nsop.neds.cascais360.Entities.EventDetailEntity;
import nsop.neds.cascais360.Entities.Json.Colors;
import nsop.neds.cascais360.Entities.Json.Labels;

public class Settings {

    public static Colors colors;
    public static Labels labels;

    public static int timeout = 2000;

    public static int dotsMargin = 5;

    public static String resourcesChecksum;


    public static int selected_month;
    public static String selected_day;
    public static TextView tv_selected_day;

    public static EventDetailEntity current_event;

    public static int screen_heigth;

}
