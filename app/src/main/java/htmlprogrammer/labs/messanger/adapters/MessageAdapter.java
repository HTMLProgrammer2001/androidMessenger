package htmlprogrammer.labs.messanger.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.models.Message;

public class MessageAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private ArrayList<Message> data = new ArrayList<>();
    private LayoutInflater inflater;
    private AppCompatActivity ctx;

    private static int EMPTY_TYPE = 1;
    private static int LIST_TYPE = 2;

    public MessageAdapter(Context ctx){
        this.ctx = (AppCompatActivity) ctx;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getItemViewType(int position) {
        return data.size() == 0 ? EMPTY_TYPE : LIST_TYPE;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if(data.size() == 0) {
            view = inflater.inflate(R.layout.empty_adapter, viewGroup, false);
        }
        else {
            view = inflater.inflate(R.layout.dialog_item, viewGroup, false);
        }

        return new SearchViewHolder(ctx, view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder dialogViewHolder, int i) {
        if(data.size() > 0 && data.size() != i){
            Message message = data.get(i);
            dialogViewHolder.showName("Name");
            dialogViewHolder.showAvatar("Name", null);

            dialogViewHolder.showTime(message.getTimeString());
            dialogViewHolder.showMessage(message.getMessage());
        }
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

        for(Object msg : arr){
            messages.add((Message) msg);
        }

        this.data = messages;
        this.notifyDataSetChanged();
    }
}
