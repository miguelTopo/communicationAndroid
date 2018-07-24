package co.edu.udistrital.communicationapp.application;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

public class CommunicationApplication extends Application {

    private static CommunicationApplication instance;
    private static Context context;

    public static CommunicationApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return context;
    }

    public void setAppContext(Context mAppContext) {
        this.context = mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.setAppContext(getApplicationContext());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
