package htmlprogrammer.labs.messanger.adapters.messagesVH;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.helpers.Converter;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;

public class DocumentMessageViewHolder extends MessageViewHolder {
    private TextView name;
    private TextView time;
    private TextView documentName;
    private TextView size;
    private FrameLayout action;
    private ImageView check2;

    private String url;
    private String fileName;
    private Activity activity;

    private final static int PERM_CODE = 101;

    public DocumentMessageViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView);

        //find elements
        documentName = itemView.findViewById(R.id.documentName);
        action = itemView.findViewById(R.id.action);
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
        setSending(message);

        addHandlers();
    }

    private void addHandlers(){
        action.setOnClickListener(v -> {
            //request write permissions
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                int perm = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (perm == PackageManager.PERMISSION_GRANTED)
                    onPermGranted();
                else
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS}, PERM_CODE);
            }
            else
                onPermGranted();
        });
    }

    private void onPermGranted(){
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url.replace("\\", "/"));
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName);
        request.setDescription("Downloading " + fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        manager.enqueue(request);
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
