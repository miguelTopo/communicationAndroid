package co.edu.udistrital.communicationapp.rest;


import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.adapter.ContactListAdapter;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.model.User;
import co.edu.udistrital.communicationapp.model.UserContact;
import co.edu.udistrital.communicationapp.util.JSONUtil;
import co.edu.udistrital.communicationapp.values.FieldName;

public class UserContactService {

    private static final String CONTACT_LIST = "/userContact/list";
    private RecyclerView recyclerView;

    public UserContactService(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void loadContactList(final User user) {
        AsyncTask<User, Void, List<UserContact>> task = new AsyncTask<User, Void, List<UserContact>>() {
            @Override
            protected List<UserContact> doInBackground(User... users) {
                if (user == null || user.mobilePhone == null || user.mobilePhone.trim().isEmpty() || user.mobilePhone.trim().length() < 10)
                    return Collections.emptyList();
                String json = ApplicationService.executePost(CONTACT_LIST, JSONUtil.parseToJson(user));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    return (List<UserContact>) JSONUtil.parseToList(jsonObject.getString(FieldName.LIST_RESPONSE), new TypeToken<List<UserContact>>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Collections.emptyList();
            }

            @Override
            protected void onPostExecute(List<UserContact> contactResponse) {
                super.onPostExecute(contactResponse);
                try {
                    Context context = CommunicationApplication.getAppContext();
                    if (contactResponse != null && !contactResponse.isEmpty()) {
                        LinkedList<UserContact> contactList = new LinkedList<>();
                        contactList.addAll(contactResponse);
                        // Get a handle to the RecyclerView
                        // Create an adapter and supply the data to be displayed

                        ContactListAdapter adapter = new ContactListAdapter(context, contactList);
                        //Connect the adapter with the RecyclerView
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        //textView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        //textView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        };
        task.execute(user);
    }

}
