package htmlprogrammer.labs.messanger.adapters.messagesVH;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import htmlprogrammer.labs.messanger.App;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.helpers.Converter;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;

public class AudioMessageViewHolder extends MessageViewHolder {
    private TextView name;
    private TextView time;
    private ImageView check2;
    private FrameLayout action;
    private ImageView actionImg;
    private TextView audioName;
    private SeekBar duration;
    private TextView total;
    private TextView passed;

    private String url;
    private boolean isPlay = false;
    private boolean isPrepared = false;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler = new Handler();

    public AudioMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        //find elements
        name = itemView.findViewById(R.id.name);
        time = itemView.findViewById(R.id.time);
        check2 = itemView.findViewById(R.id.check2);
        action = itemView.findViewById(R.id.action);
        actionImg = itemView.findViewById(R.id.actionImg);
        audioName = itemView.findViewById(R.id.documentName);
        duration = itemView.findViewById(R.id.duration);
        total = itemView.findViewById(R.id.total);
        passed = itemView.findViewById(R.id.passed);
    }

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        runnable = new Runnable() {
            @Override
            public void run() {
                duration.setProgress(mediaPlayer.getCurrentPosition());
                passed.setText(Converter.toTimeStr(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 500);
            }
        };

        mediaPlayer.stop();
    }

    public void updateUI(Message message, FragmentManager manager) {
        setOwn(message.getAuthor().getId().equals(MeStore.getInstance().getUser().getId()));
        setName(message.getAuthor().getFullName());
        setShown(message.isReaded());
        setTime(message.getTimeString());
        setAudioName(message.getMessage());
        setURL(message.getUrl());
        setSending(message);

        initPlayer();
        addHandlers();
    }

    private void setAudioName(String name) {
        audioName.setText(name);
    }

    private void setURL(String url) {
        this.url = url.replace("\\", "/");
    }

    void setName(String name) {
        this.name.setText(name);
    }

    void setTime(String time) {
        this.time.setText(time);
    }

    private void setShown(boolean isShown) {
        check2.setVisibility(isShown ? View.VISIBLE : View.GONE);
    }

    private void addHandlers() {
        action.setOnClickListener(v -> onTogglePlay());
//        mediaPlayer.setOnCompletionListener(mp -> onEnd());
        duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mediaPlayer.setOnPreparedListener(mp -> {
            //update max seek bar value
            duration.setMax(mp.getDuration());

            //update durations
            int dur = mp.getDuration();
            total.setText(Converter.toTimeStr(dur));
            passed.setText(Converter.toTimeStr(0));
        });
    }

    private void onTogglePlay() {
        //change image
        actionImg.setImageDrawable(App.getContext().getDrawable(isPlay ? R.drawable.play : R.drawable.pause));

        if(isPlay) {
            //stop play
            mediaPlayer.stop();
            handler.removeCallbacks(runnable);
        }
        else {
            //preload audio
//            try {
//                if(!isPrepared){
//                    mediaPlayer.setDataSource(url);
//                    isPrepared = true;
//                    mediaPlayer.prepareAsync();
//                }
//            }
//            catch (Exception e){}

            //start play
//            mediaPlayer.start();
//            handler.postDelayed(runnable, 0);
        }

        isPlay = !isPlay;
    }

    private void onEnd(){
        onTogglePlay();
        duration.setProgress(0);
        mediaPlayer.seekTo(0);
    }
}
