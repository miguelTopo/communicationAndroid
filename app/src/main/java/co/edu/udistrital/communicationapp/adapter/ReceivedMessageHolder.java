package co.edu.udistrital.communicationapp.adapter;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.R;
import co.edu.udistrital.communicationapp.application.ApplicationProperties;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.enums.MessageType;
import co.edu.udistrital.communicationapp.model.Conversation;
import co.edu.udistrital.communicationapp.model.Message;
import co.edu.udistrital.communicationapp.model.User;
import co.edu.udistrital.communicationapp.util.DateUtil;
import co.edu.udistrital.communicationapp.util.PropertyKey;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {


    private static final String TAG = SentMessageHolder.class.getSimpleName();

    private ApplicationProperties properties;

    public final CircleImageView imageReceivedPhoto;
    public final TextView messageReceivedBody;
    public final ImageView videoDefaultReceivedImage;
    public final VideoView videoReceived;
    public final TextView timeReceived;
    public final TextView textReceivedName;
    public final ImageButton playReceivedButton;
    public final ImageButton pauseReceivedButton;
    public final LinearLayout audioReceivedWrap;
    public final SeekBar receivedSeekbar;

    private MediaPlayer mediaPlayer;
    private boolean startPlaying = true;
    private Conversation conversation;
    private String fileName;
    private Handler mHandler;


    public ReceivedMessageHolder(@NonNull View itemView, Conversation conversation) {
        super(itemView);
        this.conversation = conversation;
        imageReceivedPhoto = itemView.findViewById(R.id.image_received_photo);
        textReceivedName = itemView.findViewById(R.id.text_received_name);
        messageReceivedBody = itemView.findViewById(R.id.message_received_body);
        videoDefaultReceivedImage = itemView.findViewById(R.id.video_default_received_image);
        videoDefaultReceivedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
            }
        });

        playReceivedButton = itemView.findViewById(R.id.play_received_button);
        playReceivedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = conversation.messageList.get(getAdapterPosition()).file;
                onPlay(startPlaying);
                setVisibleAndGonePlayAndPause();
            }
        });

        pauseReceivedButton = itemView.findViewById(R.id.pause_received_button);
        pauseReceivedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(startPlaying);
                setVisibleAndGonePlayAndPause();
            }
        });
        timeReceived = itemView.findViewById(R.id.time_received);
        videoReceived = itemView.findViewById(R.id.video_received);
        audioReceivedWrap = itemView.findViewById(R.id.audio_received_wrap);
        receivedSeekbar = itemView.findViewById(R.id.received_seekbar);
    }

    private void setVisibleAndGonePlayAndPause() {
        if (startPlaying) {
            playReceivedButton.setVisibility(View.GONE);
            pauseReceivedButton.setVisibility(View.VISIBLE);
        } else {
            pauseReceivedButton.setVisibility(View.GONE);
            playReceivedButton.setVisibility(View.VISIBLE);
        }
        startPlaying = !startPlaying;
    }

    void bind(Message m) {
        //En este método se pueden enviar datos que se van a renderizar
        messageReceivedBody.setText(m.messageBody);
        timeReceived.setText(DateUtil.getStringHour24H(m.creationDate));
        User user = conversation.userList.stream().filter(u -> u.id.equals(m.senderUserId)).findFirst().orElse(new User());
        textReceivedName.setText(user.name);
        Picasso.get().load(user.photo == null || user.photo.trim().isEmpty() ? getPropertyByKey(PropertyKey.CORE_DEFAULT_USERPHOTO) : user.photo).into(this.imageReceivedPhoto);
        showComponentList(m.messageType);
    }

    private void onPlay(boolean start) {
        if (start)
            startPlaying();
        else
            pausePlaying();
    }

    private void initSeekBar() {
        receivedSeekbar.setMax(mediaPlayer.getDuration() / 1000);
        mHandler = new Handler();
        Runnable tst = new Runnable() {
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    receivedSeekbar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        tst.run();
    }

    private void initMediaPlayer() {
        Uri uri = Uri.parse(fileName);
        mediaPlayer = MediaPlayer.create(CommunicationApplication.getAppContext(), uri);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startPlaying = false;
                setVisibleAndGonePlayAndPause();
                receivedSeekbar.setProgress(0);
                mediaPlayer.stop();
                mediaPlayer = null;
            }
        });
    }

    private void startPlaying() {
        if (mediaPlayer == null) {
            initMediaPlayer();
            initSeekBar();
            mediaPlayer.start();

        } else {
            mediaPlayer.start();
        }
    }

    private void pausePlaying() {
        mediaPlayer.pause();
    }

    private void playVideo() {
        System.out.print(getAdapterPosition());
        System.out.print(getLayoutPosition());
        videoDefaultReceivedImage.setVisibility(View.GONE);
        videoReceived.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse("https://miedificio.co/cdn/dev/resources/web/co/10/communication/2019/3/2019-03-f6b6e45f.mp4");
        videoReceived.setVideoURI(uri);
        videoReceived.requestFocus();
        videoReceived.start();
    }

    private void showComponentList(MessageType type) {
        if (type.equals(MessageType.TEXT)) {
            messageReceivedBody.setVisibility(View.VISIBLE);
        } else if (type.equals(MessageType.AUDIO)) {
            audioReceivedWrap.setVisibility(View.VISIBLE);
        } else if (type.equals(MessageType.VIDEO)) {
            videoDefaultReceivedImage.setVisibility(View.VISIBLE);
        } else if (type.equals(MessageType.BRAILLE)) {

        }
    }

    private String getPropertyByKey(String key) {
        if (properties == null)
            properties = new ApplicationProperties();
        return properties.getPropertyByKey(key);
    }
}
