package co.edu.udistrital.communicationapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.edu.udistrital.communicationapp.rest.ContactService;

public class ContactFragment extends Fragment {

    private ContactService contactService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        setUpToolbar(view);
        return loadContactUserList(view);
    }

    private View loadContactUserList(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.contact_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        ContactRecyclerViewAdapter adapter = new ContactRecyclerViewAdapter(getContactService().findByMobileNumber());
        recyclerView.setAdapter(adapter);
        return view;

    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null)
            activity.setSupportActionBar(toolbar);
    }

    private ContactService getContactService() {
        if (this.contactService == null)
            this.contactService = new ContactService();
        return this.contactService;
    }

}
