package co.edu.udistrital.communicationapp.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String TAG = DateUtil.class.getSimpleName();

    public static Date getCurrentDate() {
        return getCurrentCalendar().getTime();
    }

    public static Calendar getCurrentCalendar() {
        return Calendar.getInstance();
    }

    public static int getCurrrentYear() {
        return getCurrentCalendar().get(Calendar.YEAR);
    }

    public static int getCurrrentMont() {
        return getCurrentCalendar().get(Calendar.MONTH);
    }

    public static String getDayOfWeekLongName(int dayOfWeek) {
        return getDayOfWeekName(dayOfWeek);
    }

    public static String getDayOfWeekLongName(Calendar date) {
        if (date == null)
            return null;
        return getDayOfWeekName(date.get(Calendar.DAY_OF_WEEK));
    }

    public static String getDayOfWeekShortName(int dayOfWeek) {
        return getDayOfWeekNameShort(dayOfWeek);
    }

    public static String getDayOfWeekShortName(Calendar date) {
        if (date == null)
            return null;
        return getDayOfWeekNameShort(date.get(Calendar.DAY_OF_WEEK));
    }

    private static String getDayOfWeekNameShort(int dayOfWeek) {
        Calendar calendar = getCurrentCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        SimpleDateFormat sdf = new SimpleDateFormat("E");
        return sdf.format(calendar.getTime());
    }

    private static String getDayOfWeekName(int dayOfWeek) {
        Calendar calendar = getCurrentCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return sdf.format(calendar.getTime());
    }

    public static String getStringDate(Calendar calendar, String format) {
        calendar = calendar == null ? Calendar.getInstance() : calendar;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(calendar.getTime());
    }


    public static String getStringDate(int year, int monthOfYear, int dayOfMonth, String format) {
        Calendar calendar = DateUtil.getCurrentCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return getStringDate(calendar, format);
    }

    public static String getStringHour24H(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        return getStringHour24H(calendar);
    }

    public static String getStringHour24H(Calendar calendar) {
        if (calendar == null)
            return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(calendar.getTime());
    }

    public static int getHourFromString(String stringDate, String format) {
        Calendar calendar = getCalendarFromString(stringDate, format);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinuteFromString(String stringDate, String format) {
        Calendar calendar = getCalendarFromString(stringDate, format);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getDayOfMonthFromString(String stringDate, String format) {
        Calendar calendar = getCalendarFromString(stringDate, format);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthFromString(String stringDate, String format) {
        Calendar calendar = getCalendarFromString(stringDate, format);
        return calendar.get(Calendar.MONTH);
    }

    public static int getYearFromString(String stringDate, String format) {
        Calendar calendar = getCalendarFromString(stringDate, format);
        return calendar.get(Calendar.YEAR);
    }

    public static Calendar getCalendarFromString(String stringDate, String format) {
        try {
            Calendar calendar = getCurrentCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            calendar.setTime(sdf.parse(stringDate));
            return calendar;
        } catch (ParseException pe) {
            Log.e(TAG, "DateUtil - Exception getCalendarFromString");
            return null;

        }
    }
}
