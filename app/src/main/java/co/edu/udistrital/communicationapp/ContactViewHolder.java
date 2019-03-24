package co.edu.udistrital.communicationapp;

import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public NetworkImageView photoUrl;

    public TextView name;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        photoUrl = itemView.findViewById(R.id.contact_photo);
        name = itemView.findViewById(R.id.contact_name);
    }
}
