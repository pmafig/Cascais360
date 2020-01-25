package nsop.neds.cascais360.Settings;

import java.util.HashMap;
import java.util.List;

import nsop.neds.cascais360.Entities.DashboardEntity;
import nsop.neds.cascais360.Entities.Json.MapMarker;
import nsop.neds.cascais360.Entities.Json.Search;

public class Data {

    public static DashboardEntity Dashboard;

    public static HashMap<String, Search> CalendarEvents;

    public static String selected_day;
    public static String selected_month;
    public static String selected_year;

    public static Boolean noMoreCalendarEvents;

    public static int current_day;
    public static int current_month;
    public static int current_year;

    public static List<MapMarker> LocalMarkers;

    public static ValidationContext SmsValidationContext;

    public enum ValidationContext{
        none,
        changeContact,
        newAccount

    }


}
