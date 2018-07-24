package co.edu.udistrital.communicationapp.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import co.edu.udistrital.communicationapp.application.CommunicationApplication;

public class AndroidInfo {

    private Context context;

    public AndroidInfo() {
        this.context = CommunicationApplication.getAppContext();
    }

    /**
     * Permite obtener la cadena de texto para el numero de celular  del dispositivo actual
     */
    public String getCurrentUserMobilePhone() {
        try {
            TelephonyManager manager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getLine1Number();
        } catch (SecurityException se) {
            return "";
        }
    }

}
