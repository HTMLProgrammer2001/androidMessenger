package htmlprogrammer.labs.messanger.adapters.messagesVH;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import htmlprogrammer.labs.messanger.App;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.dialogs.ImageDialog;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;

public class ImageMessageViewHolder extends MessageViewHolder {
    private TextView name;
    private ImageView img;
    private TextView time;
    private ImageView check2;

    private final static int MAX_WIDTH = ((WindowManager)App.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() - 150;
    private final static int MIN_WIDTH = 100;

    public ImageMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        //find elements
        img = itemView.findViewById(R.id.img);
        name = itemView.findViewById(R.id.name);
        time = itemView.findViewById(R.id.time);
        check2 = itemView.findViewById(R.id.check2);
    }

    public void updateUI(Message message, FragmentManager manager){
        setOwn(message.getAuthor().getId().equals(MeStore.getInstance().getUser().getId()));
        setImage(message.getUrl());
        setName(message.getAuthor().getFullName());
        setShown(message.isReaded());
        setTime(message.getTimeString());
        setSending(message);

        img.setOnClickListener(v -> ImageDialog.getInstance(message.getUrl()).show(manager, "Image"));
    }

    void setImage(String url) {
        Picasso.get().load(url).transform(new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = source.getWidth();
                int targetHeight = source.getHeight();

                double aspect = (double) targetWidth / (double) targetHeight;

                if(targetWidth > MAX_WIDTH || targetWidth < MIN_WIDTH) {
                    targetWidth = targetWidth > MAX_WIDTH ? MAX_WIDTH : MIN_WIDTH;
                    targetHeight = (int) (targetWidth * aspect);
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);

                    if(result != source)
                        source.recycle();

                    return result;
                }
                else
                    return source;
            }

            @Override
            public String key() {
                return "cropTransform";
            }
        }).into(img);
    }

    void setName(String name){
        this.name.setText(name);
    }

    void setTime(String time){
        this.time.setText(time);
    }

    void setShown(boolean isShown){
        check2.setVisibility(isShown ? View.VISIBLE : View.GONE);
    }
}
