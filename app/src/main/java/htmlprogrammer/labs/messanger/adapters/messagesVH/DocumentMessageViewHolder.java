package htmlprogrammer.labs.messanger.adapters.messagesVH;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import htmlprogrammer.labs.messanger.App;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.MessageAPI;
import htmlprogrammer.labs.messanger.helpers.Converter;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;
import okio.BufferedSink;
import okio.Okio;

public class DocumentMessageViewHolder extends MessageViewHolder {
    private TextView name;
    private TextView time;
    private TextView documentName;
    private TextView size;
    private FrameLayout action;
    private ImageView actionImg;
    private ImageView check2;

    private String url;
    private String fileName;
    private Activity activity;

    public DocumentMessageViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView);

        //find elements
        documentName = itemView.findViewById(R.id.documentName);
        action = itemView.findViewById(R.id.action);
        actionImg = itemView.findViewById(R.id.actionImg);
        size = itemView.findViewById(R.id.passed);
        name = itemView.findViewById(R.id.name);
        time = itemView.findViewById(R.id.time);
        check2 = itemView.findViewById(R.id.check2);

        this.activity = activity;
    }

    public void updateUI(Message message, FragmentManager manager){
        setOwn(message.getAuthor().getId().equals(MeStore.getInstance().getUser().getId()));
        setName(message.getAuthor().getFullName());
        setDocumentName(message.getMessage());
        setSize(message.getSize());
        setShown(message.isReaded());
        setTime(message.getTimeString());

        setURL(message.getUrl());
        setFileName(message.getMessage());

        addHandlers();
    }

    private void addHandlers(){
        action.setOnClickListener(v -> {
            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url.replace("\\", "/"));
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(fileName);
            request.setDescription("Downloading " + fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            manager.enqueue(request);
        });
    }

    private void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private void setURL(String url){
        this.url = url;
    }

    private void setDocumentName(String name){
        documentName.setText(name);
    }

    private void setSize(long bytes){
        size.setText(Converter.toSizeStr(bytes));
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
}
