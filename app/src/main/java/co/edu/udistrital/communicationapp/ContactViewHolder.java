package co.edu.udistrital.communicationapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public NetworkImageView photoUrl;

    public TextView name;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        photoUrl = itemView.findViewById(R.id.contact_photo);
        name = itemView.findViewById(R.id.contact_name);
    }
}
