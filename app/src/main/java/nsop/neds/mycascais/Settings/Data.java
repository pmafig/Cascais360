package nsop.neds.mycascais.Settings;

import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import nsop.neds.mycascais.Entities.DashboardEntity;
import nsop.neds.mycascais.Entities.Json.Category;
import nsop.neds.mycascais.Entities.Json.Country;
import nsop.neds.mycascais.Entities.Json.EventsCategories;
import nsop.neds.mycascais.Entities.Json.InfoEventBlock;
import nsop.neds.mycascais.Entities.Json.MapMarker;
import nsop.neds.mycascais.Entities.Json.PlacesCategories;
import nsop.neds.mycascais.Entities.Json.RoutesCategories;
import nsop.neds.mycascais.Entities.Json.Search;
import nsop.neds.mycascais.Entities.Json.TownCouncils;

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
    public static List<Country> CountryList;

    public enum ValidationContext{
        none,
        changeContact,
        newAccount,
        recoverAccount,
        addAuth
    }

    public static List<InfoEventBlock> CurrentCategoryList;

    public static TextView CurrentCategorySortOption;

    public static List<EventsCategories> NotificationsEventsCategories;
    public static List<PlacesCategories> NotificationsPlacesCategories;
    public static List<RoutesCategories> NotificationsRoutesCategories;

    public static List<TownCouncils> Towns;

    public static String CurrentAccountName;

    public static Boolean RecoverByEmail;

    public static Boolean RecoverBySms;

    public static List<Category> NotificationsCategoryList;
}
