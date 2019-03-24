package co.edu.udistrital.communicationapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.adapter.ContactListAdapter;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.model.User;
import co.edu.udistrital.communicationapp.navigation.NavigateTo;
import co.edu.udistrital.communicationapp.rest.UserContactService;
import co.edu.udistrital.communicationapp.util.AndroidInfo;
import co.edu.udistrital.communicationapp.values.FieldName;
import co.edu.udistrital.communicationapp.values.PreferenceKey;

public class ContactActivity extends AppCompatActivity {

    private UserContactService userContactService;
    private AndroidInfo androidInfo;
    private RecyclerView contactRecycleView;
    private ContactListAdapter adapter;
    //private TextView contactEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initComponents();
        initView();
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void initView() {
        String mobilePhone = AppPreferences.getString(PreferenceKey.MOBILE_PHONE_CONFIGURATION);
        if (mobilePhone == null || mobilePhone.trim().isEmpty())
            mobilePhone = getIntent().getStringExtra(FieldName.MOBILE_PHONE);
        User user = new User();
        user.userContactList = getAndroidInfo().getContactList();
        user.mobilePhone = mobilePhone;
        loadContactList(user);
    }

    private void initComponents() {
        Toolbar toolbar = findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);
        this.contactRecycleView = findViewById(R.id.contact_recycleview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_contact_preferences:
                NavigateTo.lanPreferenceActivity();
                break;
            case R.id.action_contact_calendar:
                NavigateTo.eventListActivity();
                break;
            default:
                // Do nothing
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void loadContactList(User user) {
        getUserContactService().loadContactList(user);
    }

    private UserContactService getUserContactService() {
        if (this.userContactService == null)
            this.userContactService = new UserContactService(contactRecycleView);
        return this.userContactService;
    }

    private AndroidInfo getAndroidInfo() {
        if (this.androidInfo == null)
            this.androidInfo = new AndroidInfo();
        return this.androidInfo;
    }
}
