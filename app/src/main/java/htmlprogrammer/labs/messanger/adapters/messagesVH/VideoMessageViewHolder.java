package htmlprogrammer.labs.messanger.adapters.messagesVH;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import htmlprogrammer.labs.messanger.App;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.VideoPlayerActivity;
import htmlprogrammer.labs.messanger.helpers.Converter;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;

public class VideoMessageViewHolder extends MessageViewHolder {
    private TextView name;
    private TextView time;
    private TextView videoName;
    private TextView size;
    private ImageView play;
    private ImageView check2;

    private String url;

    public VideoMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        //find elements
        videoName = itemView.findViewById(R.id.videoName);
        name = itemView.findViewById(R.id.name);
        time = itemView.findViewById(R.id.time);
        check2 = itemView.findViewById(R.id.check2);
        size = itemView.findViewById(R.id.passed);
        play = itemView.findViewById(R.id.play);
    }

    public void updateUI(Message message, FragmentManager manager){
        setOwn(message.getAuthor().getId().equals(MeStore.getInstance().getUser().getId()));
        setURL(message.getUrl());
        setName(message.getAuthor().getFullName());
        setVideoName(message.getMessage());
        setShown(message.isReaded());
        setTime(message.getTimeString());
        setSize(message.getSize());
        setSending(message);

        play.setOnClickListener(v -> open());
        videoName.setOnClickListener(v -> open());
    }

    private void open(){
        //show video player layout
        Intent intent = new Intent(App.getContext(), VideoPlayerActivity.class);
        intent.putExtra("url", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

    private void setSize(long size){
        this.size.setText(Converter.toSizeStr(size));
    }

    private void setURL(String url) {
        this.url = url;
    }

    private void setName(String name){
        this.name.setText(name);
    }

    private void setTime(String time){
        this.time.setText(time);
    }

    private void setShown(boolean isShown){
        check2.setVisibility(isShown ? View.VISIBLE : View.GONE);
    }

    private void setVideoName(String newName){
        videoName.setText(newName);
    }
}
