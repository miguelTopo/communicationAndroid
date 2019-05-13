package co.edu.udistrital.communicationapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.model.Message;
import co.edu.udistrital.communicationapp.model.User;
import co.edu.udistrital.communicationapp.navigation.NavigateTo;
import co.edu.udistrital.communicationapp.rest.MessageService;
import co.edu.udistrital.communicationapp.util.FileUtils;
import co.edu.udistrital.communicationapp.util.ToastMessage;
import co.edu.udistrital.communicationapp.values.FieldName;
import co.edu.udistrital.communicationapp.values.PreferenceKey;

public class TalkTextActivity extends AppCompatActivity {

    private static final int MESSAGE_TEXT = 1;

    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private TextInputEditText message;
    private TextToSpeech tts;

    private MessageService messageService;

    private String contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_text);
        Intent intent = getIntent();
        this.contactId = intent.getStringExtra(FieldName.CONTACT_SELECTED);
        initComponents();
    }

    private void initComponents() {
        Toolbar defaultToolbar = findViewById(R.id.talk_text_toolbar);
        setSupportActionBar(defaultToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        saveButton = findViewById(R.id.btn_talk_text_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        cancelButton = findViewById(R.id.btn_talk_text_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigateTo.talkActivity();
            }
        });
        message = findViewById(R.id.talk_text_message);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale locale = new Locale("es", "CO");
                    tts.setLanguage(locale);
                }
            }
        });


    }

    private boolean validateSendMessage() {
        if (this.message.getText().toString() == null || this.message.getText().toString().trim().isEmpty()) {
            ToastMessage.addWarn(R.string.message_text_empty);
            return false;
        }
        return true;
    }

    private void sendMessage() {
        if (!validateSendMessage())
            return;

        Message message = new Message();
        message.messageBody = this.message.getText().toString();
        message.receiverUser = new User(contactId);
        message.senderUser = new User(AppPreferences.getString(PreferenceKey.APP_USER_ID));
        synthesizeToFileAndSendMessage(message);

    }

    private void synthesizeToFileAndSendMessage(Message message) {
        File directory = FileUtils.getPicturePrivateAlbum();
        String filePath = directory.getAbsolutePath() + "/" + FileUtils.getFileName(".mp3");
        File f = new File(filePath);
        String uttId = TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID;

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                getMessageService().sendTextMessage(message, f);
            }

            @Override
            public void onError(String utteranceId) {
                System.out.print("Ocurrió un error");
            }
        });
        tts.synthesizeToFile(message.messageBody, null, f, uttId);
    }

    private MessageService getMessageService() {
        if (this.messageService == null)
            this.messageService = new MessageService();
        return this.messageService;
    }
}
