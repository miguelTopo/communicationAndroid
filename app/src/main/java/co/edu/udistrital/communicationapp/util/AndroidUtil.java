package co.edu.udistrital.communicationapp.util;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import androidx.core.content.ContextCompat;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;

public class AndroidUtil {

    public static final String TAG = AndroidUtil.class.getSimpleName();

    public static Drawable getDrawable(int intRResource) {
        return ContextCompat.getDrawable(CommunicationApplication.getAppContext(), intRResource);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static String getVideoRealPathFromUri(Uri uri) {
        return getRealPathFromUri(uri, MediaStore.Video.Media.DATA);
    }

    public static String getImageRealPathFromUri(Uri uri) {
        return getRealPathFromUri(uri, MediaStore.Images.Media.DATA);
    }

    private static String getRealPathFromUri(Uri uri, String dataType) {
        Cursor cursor = null;
        try {
            String[] proj = {dataType};
            cursor = CommunicationApplication.getAppContext().getContentResolver().query(uri, proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(dataType);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static Date getDateFromDatePicker(DatePicker datePicker) {
        return getCalendarFromDatePicker(datePicker).getTime();
    }

    public static Calendar getCalendarFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = DateUtil.getCurrentCalendar();
        calendar.set(year, month, day);
        return calendar;
    }

}
