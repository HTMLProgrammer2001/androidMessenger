package htmlprogrammer.labs.messanger.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.DialogsActivity;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.Message;

public class DialogAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private ArrayList<Dialog> data = new ArrayList<>();
    private LayoutInflater inflater;
    private DialogsActivity ctx;

    private static int EMPTY_TYPE = 1;
    private static int LIST_TYPE = 2;

    public DialogAdapter(Context ctx){
        this.ctx = (DialogsActivity) ctx;
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
            //update ui
            Dialog dialog = data.get(i);
            dialogViewHolder.showName(dialog.getName());
            dialogViewHolder.showAvatar(dialog.getName(), dialog.getAvatar());
            dialogViewHolder.showUnread(dialog.getUnread());
            dialogViewHolder.setOnline(dialog.isOnline());

            Message message = dialog.getMessage();

            if(message != null){
                //show last message info
                dialogViewHolder.showTime(message.getDateTimeString());
                dialogViewHolder.showMessage(message.getMessage());
            }
            else{
                //show default data if message not exists
                dialogViewHolder.showTime("");
                dialogViewHolder.showMessage(ctx.getString(R.string.messagesDeleted));
            }

            if(!dialog.getStatus().equals(""))
                dialogViewHolder.showMessage(dialog.getStatus());

            dialogViewHolder.setOnClick(v -> ctx.openDialog(dialog.getNick()));
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

    public void setData(TreeSet<Dialog> data){
        Object[] arr = data.toArray();
        ArrayList<Dialog> dialogs = new ArrayList<>();

        for(Object dialog : arr){
            dialogs.add((Dialog) dialog);
        }

        this.data = dialogs;
        this.notifyDataSetChanged();
    }
}
