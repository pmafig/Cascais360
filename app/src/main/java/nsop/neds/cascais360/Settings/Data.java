package nsop.neds.cascais360.Settings;

import java.util.HashMap;
import java.util.List;

import nsop.neds.cascais360.Entities.DashboardEntity;
import nsop.neds.cascais360.Entities.FrameEntity;
import nsop.neds.cascais360.Entities.PinPointEntity;

public class Data {

    public static DashboardEntity Dashboard;

    public static HashMap<String, HashMap<String, List<FrameEntity>>> CalendarEvents;

    public static HashMap<Integer, PinPointEntity> PinpointEvents;

    public static String selected_day;
    public static String selected_month;
    public static String selected_year;

    public static Boolean noMoreCalendarEvents;

    public static int current_day;
    public static int current_month;
    public static int current_year;
}
