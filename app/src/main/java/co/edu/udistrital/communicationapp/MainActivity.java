package co.edu.udistrital.communicationapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import co.edu.udistrital.communicationapp.asynctask.UserInDB;
import co.edu.udistrital.communicationapp.rest.UserService;
import co.edu.udistrital.communicationapp.util.AndroidInfo;
import co.edu.udistrital.communicationapp.util.AndroidPermission;
import co.edu.udistrital.communicationapp.util.PermissionCode;


public class MainActivity extends AppCompatActivity implements NavigationHost {

    private AndroidInfo androidInfo;

    private UserService userService;
    private String userMobilePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        //setContentView(R.layout.activity_main);
        //redirectMainActivity();
    }

    private void redirectMainActivity() {
        userMobilePhone = null;
        getMobileNumberOrRequestPermission();
        if (userMobilePhone == null || userMobilePhone.trim().isEmpty())
            return;
        new UserInDB().execute(userMobilePhone );
    }

    private void getMobileNumberOrRequestPermission() {
        if (AndroidPermission.checkPermission(this, Manifest.permission.READ_PHONE_STATE))
            userMobilePhone = getAndroidInfo().getCurrentUserMobilePhone();
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE))
                showExplanation(getString(R.string.main_read_phone_state_permission_title), getString(R.string.main_read_phone_state_permission_message), Manifest.permission.READ_PHONE_STATE, PermissionCode.READ_PHONE_STATE);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionCode.READ_PHONE_STATE);
        }
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
