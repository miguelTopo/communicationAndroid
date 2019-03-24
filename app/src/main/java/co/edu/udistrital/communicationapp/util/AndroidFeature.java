package co.edu.udistrital.communicationapp.util;

import android.content.pm.PackageManager;

import co.edu.udistrital.communicationapp.application.CommunicationApplication;

public class AndroidFeature {


    private static boolean hasFeature(String feature){
        return CommunicationApplication.getAppContext().getPackageManager().hasSystemFeature(feature);
    }

    public static  boolean camera(){
        return hasFeature(PackageManager.FEATURE_CAMERA);
    }
}
