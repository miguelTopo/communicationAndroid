package co.edu.udistrital.communicationapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import co.edu.udistrital.communicationapp.application.CommunicationApplication;

public class AndroidPermission {

    private Context context;

    public AndroidPermission() {
        this.context = CommunicationApplication.getAppContext();
    }

    public static boolean checkPermission(Activity activity, String manifestPermission) {
        return ContextCompat.checkSelfPermission(activity, manifestPermission) == PackageManager.PERMISSION_GRANTED;
    }
}
