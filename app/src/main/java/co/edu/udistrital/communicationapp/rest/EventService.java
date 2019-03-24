package co.edu.udistrital.communicationapp.rest;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.R;
import co.edu.udistrital.communicationapp.adapter.EventListAdapter;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.model.Event;
import co.edu.udistrital.communicationapp.navigation.NavigateTo;
import co.edu.udistrital.communicationapp.util.JSONUtil;
import co.edu.udistrital.communicationapp.util.ToastMessage;
import co.edu.udistrital.communicationapp.values.FieldName;

public class EventService {

    private static final String EVENT_SAVE = "/event/save";
    private static final String EVENT_LIST = "/event/list";
    private static final String EVENT_ACTIVE_UPDATE = "/event/stateUpdate";

    private RecyclerView recyclerView;

    public EventService() {
    }

    public EventService(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void saveEvent(final Event eventObj) {
        AsyncTask<Event, Void, String> task = new AsyncTask<Event, Void, String>() {
            @Override
            protected String doInBackground(Event... event) {
                if (eventObj == null)
                    return "invalid-object";
                String jsonSend = JSONUtil.parseToJson(eventObj);
                String json = ApplicationService.executePost(EVENT_SAVE, jsonSend);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean succesResponse = jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                    if (succesResponse)
                        return "success";
                    else
                        return "error";
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                try {
                    if (response.equalsIgnoreCase("success")) {
                        NavigateTo.eventListActivity();
                        ToastMessage.addSuccess(R.string.core_success_response);
                    } else
                        ToastMessage.addError(R.string.core_error_response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute(eventObj);
    }

    public void loadEventListByUser(final String userId) {
        if (userId == null || userId.trim().isEmpty())
            return;
        AsyncTask<String, Void, List<Event>> task = new AsyncTask<String, Void, List<Event>>() {
            @Override
            protected List<Event> doInBackground(String... strings) {
                if (userId == null || userId.trim().isEmpty())
                    return Collections.emptyList();
                Map<String, String> map = new HashMap<>();
                map.put("userId", userId);
                String json = ApplicationService.executePost(EVENT_LIST, map);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    return (List<Event>) JSONUtil.parseToList(jsonObject.getString(FieldName.LIST_RESPONSE), new TypeToken<List<Event>>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            }

            @Override
            protected void onPostExecute(List<Event> eventResponseList) {
                super.onPostExecute(eventResponseList);
                try {
                    Context context = CommunicationApplication.getAppContext();
                    if (eventResponseList != null && !eventResponseList.isEmpty()) {
                        LinkedList<Event> eventList = new LinkedList<>();
                        eventList.addAll(eventResponseList);
                        // Get a handle to the RecyclerView
                        // Create an adapter and supply the data to be displayed
                        EventListAdapter adapter = new EventListAdapter(context, eventList);
                        //Connect the adapter with the RecyclerView
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute(userId);
    }

    public boolean changeEventActive(String eventId, boolean active) {
        if (eventId == null || eventId.trim().isEmpty())
            return false;
        Event event = new Event();
        event.setId(eventId);
        event.setActive(active);

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                String json = ApplicationService.executePost(EVENT_ACTIVE_UPDATE, JSONUtil.parseToJson(event));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    return jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        task.execute();
        return true;
    }
}
