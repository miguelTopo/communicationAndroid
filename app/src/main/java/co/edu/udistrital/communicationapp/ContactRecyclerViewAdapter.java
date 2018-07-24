package co.edu.udistrital.communicationapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import co.edu.udistrital.communicationapp.model.UserContact;
import co.edu.udistrital.communicationapp.network.ImageRequester;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private final List<UserContact> userContactList;
    private ImageRequester imageRequester;

    public ContactRecyclerViewAdapter(List<UserContact> userContactList) {
        this.userContactList = userContactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact, parent, false);
        return new ContactViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if (userContactList != null && position < userContactList.size()) {
            UserContact uc = userContactList.get(position);
            holder.name.setText(uc.customName);
            imageRequester.setImageFromUrl(holder.photoUrl, uc.photoUrl);
        }
    }

    @Override
    public int getItemCount() {
        return userContactList.size();
    }

}
