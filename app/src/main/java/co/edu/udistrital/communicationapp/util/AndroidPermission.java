package co.edu.udistrital.communicationapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;

public class AndroidPermission {

    private Context context;

    public AndroidPermission() {
        this.context = CommunicationApplication.getAppContext();
    }

    private static boolean checkPermission(Activity activity, String manifestPermission) {
        return ContextCompat.checkSelfPermission(activity, manifestPermission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean readContacts(Activity activity) {
        return checkPermission(activity, Manifest.permission.READ_CONTACTS);
    }

    public static boolean writeExteranlStorage(Activity activity) {
        return checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean readExteranlStorage(Activity activity) {
        return checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}
