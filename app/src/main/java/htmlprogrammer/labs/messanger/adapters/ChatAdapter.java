package htmlprogrammer.labs.messanger.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.models.Message;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private ArrayList<Message> data = new ArrayList<>();
    private LayoutInflater inflater;

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
    public void onBindViewHolder(@NonNull MessageViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return Math.max(data.size(), 1);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
