package htmlprogrammer.labs.messanger.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.messagesVH.ImageMessageViewHolder;
import htmlprogrammer.labs.messanger.adapters.messagesVH.SpecialMessageViewHolder;
import htmlprogrammer.labs.messanger.adapters.messagesVH.TextMessageViewHolder;
import htmlprogrammer.labs.messanger.constants.MessageTypes;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;

public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private ArrayList<Message> data = new ArrayList<>();
    private FragmentManager manager;

    private final int EMPTY_TYPE = -1;

    public ChatAdapter(FragmentManager manager){
        this.manager = manager;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MessageViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if(data.size() == 0)
            //show no messages label
            holder = new TextMessageViewHolder(inflater.inflate(R.layout.empty_adapter, viewGroup, false));
        else{
            Message message = data.get(i);

            //show messages by type
            if(message.getType().equals(MessageTypes.IMAGE))
                holder = new ImageMessageViewHolder(inflater.inflate(R.layout.image_message_layout, viewGroup, false));
            else if(message.getType().equals(MessageTypes.SPECIAL))
                holder = new SpecialMessageViewHolder(inflater.inflate(R.layout.special_message_layout, viewGroup, false));
            else
                holder = new TextMessageViewHolder(inflater.inflate(R.layout.text_message_layout, viewGroup, false));
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder textMessageViewHolder, int i) {
        if(data.size() == 0)
            return;

        Message message = data.get(i);
        textMessageViewHolder.updateUI(message, manager);
        textMessageViewHolder.setIsRecyclable(false);
    }

    @Override
    public int getItemViewType(int position) {
        return data.size() == 0 ? EMPTY_TYPE : position;
    }

    @Override
    public int getItemCount() {
        return Math.max(data.size(), 1);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId().hashCode();
    }

    public void setData(TreeSet<Message> data){
        Object[] arr = data.toArray();
        ArrayList<Message> messages = new ArrayList<>();

        for(Object dialog : arr){
            messages.add((Message) dialog);
        }

        Collections.reverse(messages);
        this.data = messages;
        this.notifyDataSetChanged();
    }
}
