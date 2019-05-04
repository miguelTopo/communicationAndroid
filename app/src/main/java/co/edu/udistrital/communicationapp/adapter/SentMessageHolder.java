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

public class SentMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = SentMessageHolder.class.getSimpleName();

    private ApplicationProperties properties;

    public final CircleImageView imageSentPhoto;
    public final TextView messageSentBody;
    public final ImageView videoDefaultSentImage;
    public final VideoView videoSent;
    public final TextView timeSent;
    public final ImageButton playSentButton;
    public final ImageButton pauseSentButton;
    public final LinearLayout audioSentWrap;
    public final SeekBar sentSeekbar;

    private MediaPlayer mediaPlayer = null;
    private boolean startPlaying = true;
    private Conversation conversation;
    private String fileName;
    private Handler mHandler;


    public SentMessageHolder(@NonNull View itemView, Conversation conversation) {
        super(itemView);
        this.conversation = conversation;
        imageSentPhoto = itemView.findViewById(R.id.image_sent_photo);
        messageSentBody = itemView.findViewById(R.id.message_sent_body);
        videoDefaultSentImage = itemView.findViewById(R.id.video_default_sent_image);
        videoDefaultSentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
            }
        });

        playSentButton = itemView.findViewById(R.id.play_sent_button);
        playSentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = conversation.messageList.get(getAdapterPosition()).file;
                onPlay(startPlaying);
                setVisibleAndGonePlayAndPause();

            }
        });

        pauseSentButton = itemView.findViewById(R.id.pause_sent_button);
        pauseSentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(startPlaying);
                setVisibleAndGonePlayAndPause();
            }
        });
        timeSent = itemView.findViewById(R.id.time_sent);
        videoSent = itemView.findViewById(R.id.video_sent);
        audioSentWrap = itemView.findViewById(R.id.audio_sent_wrap);
        sentSeekbar = itemView.findViewById(R.id.sent_seekbar);
    }

    private void setVisibleAndGonePlayAndPause() {
        if (startPlaying) {
            playSentButton.setVisibility(View.GONE);
            pauseSentButton.setVisibility(View.VISIBLE);
        } else {
            pauseSentButton.setVisibility(View.GONE);
            playSentButton.setVisibility(View.VISIBLE);
        }
        startPlaying = !startPlaying;
    }

    public void bind(Message m) {
        messageSentBody.setText(m.messageBody);
        timeSent.setText(DateUtil.getStringHour24H(m.creationDate));
        String userPhoto = conversation.userList.stream().filter(u -> u.id.equals(m.senderUserId)).findFirst().orElse(new User()).photo;
        Picasso.get().load(userPhoto == null || userPhoto.trim().isEmpty() ? getPropertyByKey(PropertyKey.CORE_DEFAULT_USERPHOTO) : userPhoto).into(this.imageSentPhoto);
        showComponentList(m.messageType);
    }

    private void onPlay(boolean start) {
        if (start)
            startPlaying();
        else
            pausePlaying();
    }

    private void initSeekBar() {
        sentSeekbar.setMax(mediaPlayer.getDuration() / 1000);
        mHandler = new Handler();
        Runnable tst = new Runnable() {
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    sentSeekbar.setProgress(mCurrentPosition);
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
                sentSeekbar.setProgress(0);
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
        videoDefaultSentImage.setVisibility(View.GONE);
        videoSent.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse("https://miedificio.co/cdn/dev/resources/web/co/10/communication/2019/3/2019-03-f6b6e45f.mp4");
        videoSent.setVideoURI(uri);
        videoSent.requestFocus();
        videoSent.start();
    }

    private void showComponentList(MessageType type) {
        if (type.equals(MessageType.TEXT)) {
            messageSentBody.setVisibility(View.VISIBLE);
        } else if (type.equals(MessageType.AUDIO)) {
            audioSentWrap.setVisibility(View.VISIBLE);
        } else if (type.equals(MessageType.VIDEO)) {
            videoDefaultSentImage.setVisibility(View.VISIBLE);
        } else if (type.equals(MessageType.BRAILLE)) {

        }
    }

    private String getPropertyByKey(String key) {
        if (properties == null)
            properties = new ApplicationProperties();
        return properties.getPropertyByKey(key);
    }

}
