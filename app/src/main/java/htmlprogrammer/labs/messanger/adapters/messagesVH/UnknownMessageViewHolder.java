package htmlprogrammer.labs.messanger.adapters.messagesVH;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;

public class UnknownMessageViewHolder extends MessageViewHolder {
    private TextView name;
    private TextView time;
    private ImageView check2;

    public UnknownMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        //find elements
        name = itemView.findViewById(R.id.name);
        time = itemView.findViewById(R.id.time);
        check2 = itemView.findViewById(R.id.check2);
    }

    public void updateUI(Message message, FragmentManager manager){
        setOwn(message.getAuthor().getId().equals(MeStore.getInstance().getUser().getId()));
        setName(message.getAuthor().getFullName());
        setShown(message.isReaded());
        setTime(message.getTimeString());
        setSending(message);
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
