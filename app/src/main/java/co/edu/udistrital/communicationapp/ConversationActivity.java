package co.edu.udistrital.communicationapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.rest.MessageService;
import co.edu.udistrital.communicationapp.values.FieldName;
import co.edu.udistrital.communicationapp.values.PreferenceKey;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageService messageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        initComponents();
        loadMessageList();
    }

    private void initComponents() {
        recyclerView = findViewById(R.id.conversation_message_recyclerview);
    }

    private void loadMessageList() {
        Intent intent = getIntent();
        String contactId = intent.getStringExtra(FieldName.CONTACT_SELECTED);
        getMessageService().loadConversation(AppPreferences.getString(PreferenceKey.APP_USER_ID), contactId);
    }

    private MessageService getMessageService() {
        if (this.messageService == null)
            this.messageService = new MessageService();
        return this.messageService;
    }
}
