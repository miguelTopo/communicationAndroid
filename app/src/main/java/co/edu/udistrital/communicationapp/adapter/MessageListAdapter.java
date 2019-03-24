package co.edu.udistrital.communicationapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.R;
import co.edu.udistrital.communicationapp.model.Conversation;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int SENT_MESSAGE_VIEW = 1;
    private static final int RECEIVED_MESSAGE_VIEW = 2;

    private LayoutInflater inflater;
    private Context context;
    private Conversation conversation;

    public MessageListAdapter(Context context, Conversation conversation) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.conversation = conversation;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENT_MESSAGE_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case SENT_MESSAGE_VIEW:
                ((SentMessageHolder) holder).bind();
                break;
            case RECEIVED_MESSAGE_VIEW:
                ((ReceivedMessageHolder) holder).bind();
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        //aqui validar  el tipo de mensaje partiendo de la definición de si es un mensaje de llegada o de salida
        return SENT_MESSAGE_VIEW;
    }

    @Override
    public int getItemCount() {
        return  0 /*this.conversation.messageLinkedList.size()*/;
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timeText;
        TextView nameText;
        CircleImageView imageMessageProfile;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_mesage_name);
            imageMessageProfile = itemView.findViewById(R.id.image_message_profile);
        }

        void bind() {
            //En este método se pueden enviar datos que se van a renderizar
            messageText.setText("Hola este mesaje es quemado desde receivedHolder");
            timeText.setText("15:23");
            nameText.setText("Nombre de persona");
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timeText;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind() {
            //En este método se pueden enviar datos que se van a renderizar
            messageText.setText("Hola este mesaje es quemado desde sendHolder");
            timeText.setText("12:30");
        }
    }

}
