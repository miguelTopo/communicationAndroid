package co.edu.udistrital.communicationapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.model.Message;
import co.edu.udistrital.communicationapp.model.User;
import co.edu.udistrital.communicationapp.rest.MessageService;
import co.edu.udistrital.communicationapp.util.AndroidUtil;
import co.edu.udistrital.communicationapp.util.PermissionCode;
import co.edu.udistrital.communicationapp.values.FieldName;
import co.edu.udistrital.communicationapp.values.PreferenceKey;

public class TalkActivity extends AppCompatActivity {

    private BottomNavigationView talkMenu;
    private MessageService messageService;
    private String contactId;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        initComponents();
        loadConversation();
    }

    private void loadConversation() {
        Intent intent = getIntent();
        this.contactId = intent.getStringExtra(FieldName.CONTACT_SELECTED);
        System.out.println("Llegamos a conversación con contacto =" + this.contactId);
        getMessageService().loadConversation(AppPreferences.getString(PreferenceKey.APP_USER_ID), this.contactId);
    }

    private void navigateToTalkTextActivity() {
        Intent intent = new Intent(this, TalkTextActivity.class);
        intent.putExtra(FieldName.CONTACT_SELECTED, this.contactId);
        startActivityForResult(intent, PermissionCode.TEXT_MESSAGE_REQUEST);
    }

    private void navigateToTalkAudioActivity() {
        Intent intent = new Intent(this, TalkAudioActivity.class);
        intent.putExtra(FieldName.CONTACT_SELECTED, this.contactId);
        startActivityForResult(intent, PermissionCode.TEXT_MESSAGE_REQUEST);
    }

    private void navigateToVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(takeVideoIntent, PermissionCode.REQUEST_VIDEO_CAPTURE);
    }

    private boolean navigateOptionConversation(int option) {
        switch (option) {
            case R.id.action_conversation_text:
                navigateToTalkTextActivity();
                break;
            case R.id.action_conversation_audio:
                navigateToTalkAudioActivity();
                break;
            case R.id.action_conversation_video:
                navigateToVideo();
                break;
            default:
                break;
        }
        return true;
    }

    private void initComponents() {

        Toolbar defaultToolbar = findViewById(R.id.talk_toolbar);
        setSupportActionBar(defaultToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.message_recyclerview);

        talkMenu = findViewById(R.id.talk_menu);
        talkMenu.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        return navigateOptionConversation(menuItem.getItemId());
                    }
                }
        );
    }

    private void sendVideoMessage(Uri videoUri) {
        if (videoUri == null)
            return;
        String filePath = AndroidUtil.getVideoRealPathFromUri(videoUri);
        File video = new File(filePath);
        if (video.exists()) {
            Message message = new Message();
            message.receiverUser = new User(this.contactId);
            message.senderUser = new User(AppPreferences.getString(PreferenceKey.APP_USER_ID));
            getMessageService().sendVideoMessage(video, new Message());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PermissionCode.REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            sendVideoMessage(intent.getData());
        }
    }

    private MessageService getMessageService() {
        try {
            if (this.messageService == null)
                this.messageService = new MessageService(recyclerView);
            return this.messageService;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
