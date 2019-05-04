package co.edu.udistrital.communicationapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.R;
import co.edu.udistrital.communicationapp.application.ApplicationProperties;
import co.edu.udistrital.communicationapp.model.Conversation;
import co.edu.udistrital.communicationapp.model.Message;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int SENT_MESSAGE_VIEW = 1;
    private static final int RECEIVED_MESSAGE_VIEW = 2;

    private final LinkedList<Message> messageList;
    private final String currentUserId;


    private LayoutInflater inflater;
    private ApplicationProperties properties;
    private Context context;
    private Conversation conversation;


    public MessageListAdapter(Context context, Conversation conversation) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.conversation = conversation;
        this.currentUserId = this.conversation.userList.get(0).id;
        this.messageList = conversation.messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENT_MESSAGE_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view, conversation);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view, conversation);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = this.messageList.get(position);
        switch (holder.getItemViewType()) {
            case SENT_MESSAGE_VIEW:
                ((SentMessageHolder) holder).bind(message);
                break;
            case RECEIVED_MESSAGE_VIEW:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            default:
                break;
        }
    }

    public int getItemViewType(int position) {
        Message m = this.messageList.get(position);
        return m.senderUserId.equals(this.currentUserId) ? SENT_MESSAGE_VIEW : RECEIVED_MESSAGE_VIEW;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
