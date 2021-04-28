package htmlprogrammer.labs.messanger.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.models.Message;

import java.util.ArrayList;
import java.util.TreeSet;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private ArrayList<Message> data = new ArrayList<>();
    private LayoutInflater inflater;

    private final int LIST_TYPE = 1;
    private final int EMPTY_TYPE = 2;

    public ChatAdapter(Context ctx){
        inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if(data.size() == 0)
            view = inflater.inflate(R.layout.empty_adapter, viewGroup, false);
        else
            view = inflater.inflate(R.layout.message_layout, viewGroup, false);

        return new MessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return data.size() == 0 ? EMPTY_TYPE : LIST_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder viewHolder, int i) {
        if(data.size() == 0)
            return;

        Message message = data.get(i);
        viewHolder.setMessage(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return Math.max(data.size(), 1);
    }

    public void setData(TreeSet<Message> data){
        Object[] arr = data.toArray();
        ArrayList<Message> messages = new ArrayList<>();

        for(Object dialog : arr){
            messages.add((Message) dialog);
        }

        this.data = messages;
        this.notifyDataSetChanged();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView message;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message);
        }

        void setMessage(String msg){
            message.setText(msg);
        }
    }
}
