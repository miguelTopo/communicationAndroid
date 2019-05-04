package co.edu.udistrital.communicationapp;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.rest.TalkAudioService;
import co.edu.udistrital.communicationapp.util.AndroidUtil;
import co.edu.udistrital.communicationapp.util.FileUtils;
import co.edu.udistrital.communicationapp.util.PermissionCode;
import co.edu.udistrital.communicationapp.values.FieldName;
import co.edu.udistrital.communicationapp.values.PreferenceKey;

public class TalkAudioActivity extends AppCompatActivity {

    private static final String TAG = TalkAudioActivity.class.getSimpleName();

    private MaterialButton audioRecordBtn;
    private MaterialButton audioSendBtn;
    private MaterialButton audioCancelBtn;
    private MaterialButton audioPlayBtn;
    private MediaRecorder mediaRecorder = null;
    private MediaPlayer mediaPlayer = null;
    private LinearLayout actionLayout;

    boolean startRecording = true;
    boolean startPlaying = true;

    private String fileName;
    private String contactId;
    //Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permission = {Manifest.permission.RECORD_AUDIO};

    private TalkAudioService talkAudioService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_audio);
        initComponents();
        getFileName();
        contactId = getIntent().getStringExtra(FieldName.CONTACT_SELECTED);
        ActivityCompat.requestPermissions(this, permission, PermissionCode.REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void getFileName() {
        File directory = FileUtils.getPicturePrivateAlbum();
        fileName = directory.getAbsolutePath() + "/" + FileUtils.getFileName(".mp3");
    }

    private void initComponents() {
        audioRecordBtn = findViewById(R.id.talk_audio_record);
        audioSendBtn = findViewById(R.id.talk_audio_send);
        audioCancelBtn = findViewById(R.id.talk_audio_cancel);
        actionLayout = findViewById(R.id.talk_audio_action_layout);
        audioPlayBtn = findViewById(R.id.talk_audio_play);

        audioRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAudioRecord(startRecording);
                if (startRecording) {
                    audioRecordBtn.setText("Stop Recording");
                    audioRecordBtn.setIcon(AndroidUtil.getDrawable(R.drawable.ic_stop_white_24dp));
                } else {
                    audioRecordBtn.setText("Start Recording");
                    audioRecordBtn.setIcon(AndroidUtil.getDrawable(R.drawable.ic_mic_white_24dp));
                }
                startRecording = !startRecording;
            }
        });

        audioSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAudioSend();
            }
        });

        audioCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAudioCancel();
            }
        });

        audioPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlay(startPlaying);
                if (startPlaying) {
                    audioPlayBtn.setText("Detener audio");
                    audioPlayBtn.setIcon(AndroidUtil.getDrawable(R.drawable.ic_stop_white_24dp));
                } else {
                    audioPlayBtn.setText("Escuchar audio");
                    audioPlayBtn.setIcon(AndroidUtil.getDrawable(R.drawable.ic_play_arrow_white_24dp));
                }
                startPlaying = !startPlaying;
            }
        });
    }

    private void onPlay(boolean start) {
        if (start)
            startPlaying();
        else
            stopPlaying();
    }

    private void startPlaying() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void onAudioCancel() {
        hideButtonControl(false);
    }

    private void onAudioSend() {
        String userId = AppPreferences.getString(PreferenceKey.APP_USER_ID);
        getTalkAudioService().messageAudioSend(userId, this.contactId, fileName);
    }


    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        mediaRecorder.start();
    }

    private void hideButtonControl(boolean hideRecord) {
        if (hideRecord) {
            audioRecordBtn.setVisibility(View.GONE);
            actionLayout.setVisibility(View.VISIBLE);
        } else {
            audioRecordBtn.setVisibility(View.VISIBLE);
            actionLayout.setVisibility(View.GONE);
        }
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        hideButtonControl(true);
    }

    /**
     * Redirecciona a grabar audio o detener grabación
     */
    private void onAudioRecord(boolean start) {
        if (start)
            startRecording();
        else
            stopRecording();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private TalkAudioService getTalkAudioService() {
        if (this.talkAudioService == null)
            this.talkAudioService = new TalkAudioService();
        return this.talkAudioService;
    }
}
