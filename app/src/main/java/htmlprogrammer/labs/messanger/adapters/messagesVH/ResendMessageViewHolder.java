package htmlprogrammer.labs.messanger.adapters.messagesVH;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.TreeSet;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.ChatAdapter;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;

public class ResendMessageViewHolder extends MessageViewHolder {
    private TextView name;
    private TextView time;
    private ImageView check2;
    private RecyclerView list;
    private ChatAdapter adapter;
    private Activity activity;

    public ResendMessageViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView);

        //find elements
        name = itemView.findViewById(R.id.name);
        time = itemView.findViewById(R.id.time);
        check2 = itemView.findViewById(R.id.check2);
        list = itemView.findViewById(R.id.list);
        this.activity = activity;
    }

    public void updateUI(Message message, FragmentManager manager){
        setOwn(message.getAuthor().getId().equals(MeStore.getInstance().getUser().getId()));
        setName(message.getAuthor().getFullName());
        setShown(message.isReaded());
        setTime(message.getTimeString());
        setSending(message);

        adapter = new ChatAdapter(manager, this.activity, false);
        adapter.setData(new TreeSet<>(message.getResend()));
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(activity));
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
