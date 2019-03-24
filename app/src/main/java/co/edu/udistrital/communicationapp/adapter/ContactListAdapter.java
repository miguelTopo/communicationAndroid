package co.edu.udistrital.communicationapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.R;
import co.edu.udistrital.communicationapp.TalkActivity;
import co.edu.udistrital.communicationapp.application.ApplicationProperties;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.model.UserContact;
import co.edu.udistrital.communicationapp.util.PropertyKey;
import co.edu.udistrital.communicationapp.values.FieldName;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    private final LinkedList<UserContact> contactList;
    private LayoutInflater inflater;
    private ApplicationProperties properties;

    public ContactListAdapter(Context context, LinkedList<UserContact> userList) {
        inflater = LayoutInflater.from(context);
        this.contactList = userList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.contact_recycleview, parent, false);
        return new ContactViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        UserContact userContact = contactList.get(position);
        holder.contactName.setText(userContact.customName == null || userContact.customName.trim().isEmpty() ? userContact.user.name : userContact.customName.trim());
        holder.hourLastMessage.setText("10:00");
        holder.lastMessage.setText("Mesg desde adapter");
        String photo = userContact.user.photo == null || userContact.user.photo.trim().isEmpty() ? getPropertyByKey(PropertyKey.CORE_DEFAULT_USERPHOTO) : userContact.user.photo;
        Picasso.get().load(photo).into(holder.contactPhoto);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    private String getPropertyByKey(String key) {
        if (properties == null)
            properties = new ApplicationProperties();
        return properties.getPropertyByKey(key);
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final CircleImageView contactPhoto;
        public final TextView contactName;
        public final TextView hourLastMessage;
        public final TextView lastMessage;
        final ContactListAdapter adapter;

        public ContactViewHolder(View itemView, ContactListAdapter adapter) {
            super(itemView);
            contactPhoto = itemView.findViewById(R.id.contact_photo);
            contactName = itemView.findViewById(R.id.contact_name);
            hourLastMessage = itemView.findViewById(R.id.contact_last_message_hour);
            lastMessage = itemView.findViewById(R.id.contact_last_message);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Get the position of the item that was clicked.
            int position = getLayoutPosition();
            UserContact contact = contactList.get(position);
            Intent intent = new Intent(CommunicationApplication.getAppContext(), TalkActivity.class);
            intent.putExtra(FieldName.CONTACT_SELECTED, contact.user.id) ;
            CommunicationApplication.getAppContext().startActivity(intent);
        }

    }

}
