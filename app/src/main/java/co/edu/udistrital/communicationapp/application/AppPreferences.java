package co.edu.udistrital.communicationapp.application;

import android.content.SharedPreferences;

public class AppPreferences {

    private static final String PREFS_NAME = "ComunicationAppPreferences";

    public static boolean addString(String key, String value) {
        SharedPreferences settings = getSettings();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
        return true;
    }

    public static String getString(String key) {
        SharedPreferences settings = getSettings();
        return settings.getString(key, "");
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences settings = getSettings();
        return settings.getBoolean(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        SharedPreferences settings = getSettings();
        return settings.getInt(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue) {
        SharedPreferences settings = getSettings();
        return settings.getFloat(key, defaultValue);
    }

    public static long getFloat(String key, long defaultValue) {
        SharedPreferences settings = getSettings();
        return settings.getLong(key, defaultValue);
    }

    private static SharedPreferences getSettings() {
        return CommunicationApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);
    }
}