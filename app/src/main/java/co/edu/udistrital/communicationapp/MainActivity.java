package co.edu.udistrital.communicationapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.rest.UserService;
import co.edu.udistrital.communicationapp.util.AndroidInfo;
import co.edu.udistrital.communicationapp.util.PermissionCode;
import co.edu.udistrital.communicationapp.values.PreferenceKey;


public class MainActivity extends AppCompatActivity implements NavigationHost {

    private AndroidInfo androidInfo;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        //setContentView(R.layout.activity_main);
        redirectMainActivity();
    }

    private void redirectMainActivity() {
        getUserService().redirectContactOrRegister(AppPreferences.getString(PreferenceKey.MOBILE_PHONE_CONFIGURATION));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionCode.READ_PHONE_STATE:
                redirectMainActivity();
                break;
        }
    }


    private void showExplanation(String title, String message, final String permission, final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message)
                .setPositiveButton(R.string.core_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment);
        if (addToBackstack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    private UserService getUserService() {
        if (this.userService == null)
            this.userService = new UserService();
        return this.userService;
    }

    private AndroidInfo getAndroidInfo() {
        if (this.androidInfo == null)
            this.androidInfo = new AndroidInfo();
        return this.androidInfo;
    }
}
