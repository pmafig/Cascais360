package nsop.neds.mycascais.Manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.Html;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import nsop.neds.mycascais.Entities.Json.Dates;
import nsop.neds.mycascais.Settings.Settings;

public class CalendarManager {

    Context context;

    public CalendarManager(Context context){
        this.context = context;
    }

    public void addevent(String title, String description, String location, Dates dates){
        long startMillis = 0;
        long endMillis = 0;

        Calendar firstDate = Calendar.getInstance();
        Calendar lastDate = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            firstDate.setTime(sdf.parse(dates.value.replace('T', ' ')));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            lastDate.setTime(sdf.parse(dates.end_value.replace('T', ' ')));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR, firstDate.get(Calendar.HOUR));
        currentDate.set(Calendar.MINUTE, firstDate.get(Calendar.MINUTE));
        currentDate.set(Calendar.SECOND, firstDate.get(Calendar.SECOND));
        currentDate.set(Calendar.MILLISECOND, firstDate.get(Calendar.MILLISECOND));

        startMillis = firstDate.before(currentDate) ? currentDate.getTimeInMillis() : firstDate.getTimeInMillis();
        endMillis = lastDate.getTimeInMillis();

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, Html.fromHtml(description).toString())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location);

        /*if(allday) {
            intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        }else{
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis);
        }*/

        //indica se o evento ocupa a disponibilidade do utilizador
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        intent.putExtra(CalendarContract.Reminders.MINUTES, 90);
        //intent.putExtra(CalendarContract.Reminders.EVENT_ID, eventID);
        intent.putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        context.startActivity(intent);
    }

    public void addReminderInCalendar() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat calformat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        /*try {
            String date = Settings.current_event.DisplayDate();

            cal.set(calformat.parse(date));


        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
        ContentResolver cr = context.getContentResolver();
        TimeZone timeZone = TimeZone.getDefault();

        /** Inserting an event in calendar. */
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.TITLE, Settings.current_event.Title());
        values.put(CalendarContract.Events.DESCRIPTION, Html.fromHtml(Settings.current_event.Description()).toString());
        values.put(CalendarContract.Events.ALL_DAY, 0);

        values.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis() + 1 * 60 * 1000);
        // ends 60 minutes from now
        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis() + 2 * 60 * 1000);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        Uri event = cr.insert(EVENTS_URI, values);

        // Display event id.
        Toast.makeText(context, "O evento " + Settings.current_event.Title() + " foi adicionado ao calendário.", Toast.LENGTH_SHORT).show();

        /** Adding reminder for event added. */
       /* Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 10);
        cr.insert(REMINDERS_URI, values);*/
    }

    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") :
                        Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }
}
