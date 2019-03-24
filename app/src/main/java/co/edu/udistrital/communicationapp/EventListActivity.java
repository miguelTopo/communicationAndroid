package co.edu.udistrital.communicationapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.navigation.NavigateTo;
import co.edu.udistrital.communicationapp.rest.EventService;
import co.edu.udistrital.communicationapp.values.PreferenceKey;

public class EventListActivity extends AppCompatActivity {

    private EventService eventService;
    private RecyclerView eventRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        initComponets();
        loadEventListByUser();
    }

    private void initComponets() {
        Toolbar toolbar = findViewById(R.id.reminder_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.eventRecyclerView = findViewById(R.id.event_recyclerview);
    }

    private void loadEventListByUser() {
        getEventService().loadEventListByUser(AppPreferences.getString(PreferenceKey.APP_USER_ID));
    }

    private EventService getEventService() {
        if (this.eventService == null)
            this.eventService = new EventService(eventRecyclerView);
        return this.eventService;
    }

    /*Eventos para manejo de menú*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_menu,  menu);
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
                //loadFragment(new EventAddFragment());
                NavigateTo.eventActivity();
                break;
            default:
                // Do nothing
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
