package co.edu.udistrital.communicationapp.util;

import android.widget.Toast;

import co.edu.udistrital.communicationapp.application.CommunicationApplication;

public class ToastMessage {

    public static final void addWarn(int RIdStringId) {
        Toast.makeText(CommunicationApplication.getAppContext(), CommunicationApplication.getAppContext().getString(RIdStringId), Toast.LENGTH_SHORT).show();
    }

    public static final void addInfo(int RIdStringId) {
        Toast.makeText(CommunicationApplication.getAppContext(), CommunicationApplication.getAppContext().getString(RIdStringId), Toast.LENGTH_SHORT).show();
    }

    public static final void addSuccess(int RIdStringId) {
        Toast.makeText(CommunicationApplication.getAppContext(), CommunicationApplication.getAppContext().getString(RIdStringId), Toast.LENGTH_SHORT).show();
    }

    public static final void addError(int RIdStringId) {
        Toast.makeText(CommunicationApplication.getAppContext(), CommunicationApplication.getAppContext().getString(RIdStringId), Toast.LENGTH_SHORT).show();
    }

    public static final void addLongWarn(int RIdStringId) {
        Toast.makeText(CommunicationApplication.getAppContext(), CommunicationApplication.getAppContext().getString(RIdStringId), Toast.LENGTH_SHORT).show();
    }

    public static final void addLongInfo(int RIdStringId) {
        Toast.makeText(CommunicationApplication.getAppContext(), CommunicationApplication.getAppContext().getString(RIdStringId), Toast.LENGTH_SHORT).show();
    }

    public static final void addLongSuccess(int RIdStringId) {
        Toast.makeText(CommunicationApplication.getAppContext(), CommunicationApplication.getAppContext().getString(RIdStringId), Toast.LENGTH_SHORT).show();
    }

    public static final void addLongError(int RIdStringId) {
        Toast.makeText(CommunicationApplication.getAppContext(), CommunicationApplication.getAppContext().getString(RIdStringId), Toast.LENGTH_SHORT).show();
    }
}
