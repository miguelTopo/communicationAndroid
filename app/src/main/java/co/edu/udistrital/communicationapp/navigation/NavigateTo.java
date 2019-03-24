package co.edu.udistrital.communicationapp.navigation;

import android.content.Intent;

import co.edu.udistrital.communicationapp.ContactActivity;
import co.edu.udistrital.communicationapp.EventActivity;
import co.edu.udistrital.communicationapp.EventListActivity;
import co.edu.udistrital.communicationapp.LanPreferenceActivity;
import co.edu.udistrital.communicationapp.MainActivity;
import co.edu.udistrital.communicationapp.RegisterActivity;
import co.edu.udistrital.communicationapp.TalkActivity;
import co.edu.udistrital.communicationapp.TalkTextActivity;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;

public class NavigateTo {


    public static final void contactActivity() {
        goTo(ContactActivity.class);
    }

    public static final void mainActivity() {
        goTo(MainActivity.class);
    }

    public static final void registerActivity() {
        goTo(RegisterActivity.class);
    }

    public static final void lanPreferenceActivity() {
        goTo(LanPreferenceActivity.class);
    }

    public static final void talkTextActivity() {
        goTo(TalkTextActivity.class);
    }

    public static final void talkActivity() {
        goTo(TalkActivity.class);
    }

    public static final void eventActivity() {
        goTo(EventActivity.class);
    }

    public static final void eventListActivity() {
        goTo(EventListActivity.class);
    }

    private static final void goTo(Class activityRedirect) {
        Intent intent = new Intent(CommunicationApplication.getAppContext(), activityRedirect);
        CommunicationApplication.getAppContext().startActivity(intent);
    }
}
