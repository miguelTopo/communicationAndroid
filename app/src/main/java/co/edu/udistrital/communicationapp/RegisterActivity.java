package co.edu.udistrital.communicationapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import co.edu.udistrital.communicationapp.application.ApplicationProperties;
import co.edu.udistrital.communicationapp.model.User;
import co.edu.udistrital.communicationapp.rest.UserService;
import co.edu.udistrital.communicationapp.util.ActionCode;
import co.edu.udistrital.communicationapp.util.AndroidInfo;
import co.edu.udistrital.communicationapp.util.AndroidPermission;
import co.edu.udistrital.communicationapp.util.FileUtils;
import co.edu.udistrital.communicationapp.util.PermissionCode;
import co.edu.udistrital.communicationapp.util.PropertyKey;
import co.edu.udistrital.communicationapp.util.ToastMessage;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private TextInputLayout layoutName;
    private TextInputEditText registerName;
    private TextInputLayout layoutMobileNumber;
    private TextInputEditText registerMobileNumber;
    private TextView registerUserLabel;
    private MaterialButton registerBtn;
    private AndroidInfo androidInfo;
    private FloatingActionButton registerPhotoUpdateBtn;
    private CircleImageView registerPhotoUser;
    private ProgressBar progressBar;
    private Uri photoUri;

    private UserService userService;
    private ApplicationProperties applicationProperties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViewComponents();
    }

    private void initComponents() {
        this.registerName = findViewById(R.id.register_name);
        this.registerMobileNumber = findViewById(R.id.register_mobile_number);
        this.registerBtn = findViewById(R.id.register_btn);
        this.layoutMobileNumber = findViewById(R.id.register_mobile_layout);
        this.layoutName = findViewById(R.id.register_name_layout);
        this.registerUserLabel = findViewById(R.id.register_user_label);
        this.registerPhotoUpdateBtn = findViewById(R.id.register_photo_update_btn);
        this.registerPhotoUser = findViewById(R.id.register_form_photo);
        this.progressBar = findViewById(R.id.register_progress_bar);
    }

    private void registerNameListener() {
        this.registerName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return validRegisterName();
            }
        });

        this.registerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validRegisterName();
                    registerMobileNumber.requestFocus();
                }
                return false;
            }
        });
    }

    private void registerMobileNumberListener() {
        this.registerMobileNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return validRegisterMobileNumber();
            }
        });

        this.registerMobileNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(registerBtn.getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    private void initViewComponents() {
        initComponents();
        registerNameListener();
        registerMobileNumberListener();
        this.registerPhotoUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUploadUserPhoto();
            }
        });
        this.registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                userRegister();
            }
        });
    }

    private void goUploadUserPhoto() {
        Intent selectPhotoIntent = new Intent();
        selectPhotoIntent.setType("image/*");
        selectPhotoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(selectPhotoIntent, getString(R.string.core_image_select)), ActionCode.SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActionCode.SELECT_IMAGE) {
            photoUri = data.getData();
            this.registerPhotoUser.setImageURI(photoUri);
        }
    }

    private boolean getRealExternalStoragePermission() {
        if (this.photoUri == null)
            return true;
        if (AndroidPermission.readExteranlStorage(this))
            return true;
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
                showExplanation(getString(R.string.main_read_phone_state_permission_title), getString(R.string.main_read_phone_state_permission_message), Manifest.permission.READ_EXTERNAL_STORAGE, PermissionCode.READ_EXTERNAL_STORAGE);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionCode.READ_EXTERNAL_STORAGE);
            return false;
        }
    }

    private boolean getContactListOrRequestPermission() {
        if (AndroidPermission.readContacts(this))
            return true;
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS))
                showExplanation(getString(R.string.main_read_phone_state_permission_title), getString(R.string.main_read_phone_state_permission_message), Manifest.permission.READ_CONTACTS, PermissionCode.READ_CONTACTS);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PermissionCode.READ_CONTACTS);
            return false;
        }
    }

    private void progressBarShow() {
        this.progressBar.setVisibility(View.VISIBLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void progressBarHide() {
        this.progressBar.setVisibility(View.GONE);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionCode.READ_CONTACTS:
                userRegister();
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


    private void userRegister() {
        try {
            if (!vallidUserRegister())
                return;

            progressBarShow();
            boolean permission = getContactListOrRequestPermission();
            if (!permission)
                return;
            permission = getRealExternalStoragePermission();
            if (!permission)
                return;
            User user = new User();
            user.name = this.registerName.getText().toString();
            user.mobilePhone = this.registerMobileNumber.getText().toString();
            user.countryCode = getApplicationProperties().getPropertyByKey(PropertyKey.SETTINGS_ISO_COUNTRY_PHONE_CODE);
            user.userContactList = getAndroidInfo().getContactList();
            File file = null;
            if (this.photoUri != null)
                file = FileUtils.getFileFromURI(this, this.photoUri, "");
            getUserService().userRegisterAndContactRedirect(user, file);
        } catch (Exception e) {
            progressBarHide();
            ToastMessage.addError(R.string.core_error_response);
        }
    }


    private boolean vallidUserRegister() {
        return validRegisterName() && validRegisterMobileNumber();
    }

    private boolean validRegisterMobileNumber() {
        if (this.registerMobileNumber.getText() == null || this.registerMobileNumber.getText().toString().trim().isEmpty()) {
            this.layoutMobileNumber.setError(getString(R.string.core_text_no_empty));
            return false;
        } else if (this.registerMobileNumber.getText().toString().trim().length() < 10) {
            this.layoutMobileNumber.setError(getString(R.string.core_incomplete_mobile_number));
            return false;
        } else
            this.layoutMobileNumber.setError(null);
        return true;
    }

    private boolean validRegisterName() {
        if (this.registerName.getText() == null || this.registerName.getText().toString().trim().isEmpty()) {
            this.registerUserLabel.setText("");
            this.layoutName.setError(getString(R.string.core_text_no_empty));
            return false;
        } else {
            this.registerUserLabel.setText(this.registerName.getText().toString().trim());
            this.layoutName.setError(null);
        }
        return true;
    }

    private AndroidInfo getAndroidInfo() {
        if (this.androidInfo == null)
            this.androidInfo = new AndroidInfo();
        return this.androidInfo;
    }

    private UserService getUserService() {
        if (this.userService == null)
            this.userService = new UserService();
        return this.userService;
    }

    private ApplicationProperties getApplicationProperties() {
        if (this.applicationProperties == null)
            this.applicationProperties = new ApplicationProperties();
        return this.applicationProperties;
    }
}
