package htmlprogrammer.labs.messanger.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.fragments.common.UserAvatar;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.Message;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.DialogViewHolder> {
    private List<Dialog> data = new ArrayList<>();
    private LayoutInflater inflater;
    private AppCompatActivity ctx;
    private boolean isLoading = false;

    private static int EMPTY_TYPE = 1;
    private static int LIST_TYPE = 2;
    private static int LOADING_TYPE = 3;

    public DialogAdapter(Context ctx){
        this.ctx = (AppCompatActivity) ctx;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getItemViewType(int position) {
        if(isLoading)
            return LOADING_TYPE;

        return data.size() == 0 ? EMPTY_TYPE : LIST_TYPE;
    }

    @NonNull
    @Override
    public DialogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if(i == data.size() && isLoading){
            view = inflater.inflate(R.layout.loading_adapter, viewGroup, false);
        }
        else if(data.size() == 0) {
            view = inflater.inflate(R.layout.empty_adapter, viewGroup, false);
        }
        else {
            view = inflater.inflate(R.layout.dialog_item, viewGroup, false);
        }

        return new DialogViewHolder(ctx, view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder dialogViewHolder, int i) {
        if(data.size() > 0){
            Dialog dialog = data.get(i);
            dialogViewHolder.showName(dialog.getName());
            dialogViewHolder.showAvatar(dialog.getName(), dialog.getAvatar());
            dialogViewHolder.showUnread(dialog.getUnread());

            Message message = dialog.getMessage();

            if(message != null){
                dialogViewHolder.showTime(message.getTime());
                dialogViewHolder.showMessage(message.getMessage());
            }
            else{
                dialogViewHolder.showTime("");
                dialogViewHolder.showMessage("Messages were deleted");
            }
        }
    }

    @Override
    public int getItemCount() {
        return Math.max(data.size() + (isLoading ? 1 : 0), 1);
    }

    public void setLoading(boolean isLoading){
        this.isLoading = isLoading;
        this.notifyDataSetChanged();
    }

    public void setData(ArrayList<Dialog> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    class DialogViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView message;
        private TextView time;
        private TextView unread;

        private AppCompatActivity ctx;

        DialogViewHolder(AppCompatActivity ctx, View view){
            super(view);

            this.ctx = ctx;

            try {
                this.name = view.findViewById(R.id.name);
                this.message = view.findViewById(R.id.message);
                this.time = view.findViewById(R.id.time);
                this.unread = view.findViewById(R.id.unread);
            }
            catch (Exception e){}
        }

        void showName(String name){
            this.name.setText(name);
        }

        void showMessage(String message){
            this.message.setText(message);
        }

        void showTime(String time){
            this.time.setText(time);
        }

        void showUnread(int unread){
            if(unread == 0){
                this.unread.setVisibility(View.GONE);
            }
            else{
                this.unread.setText(Integer.toString(unread));
                this.unread.setVisibility(View.VISIBLE);
            }
        }

        void showAvatar(String name, String avatar){
            UserAvatar avatarFragment = UserAvatar.getInstance(name, avatar);

            this.ctx.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.avatar, avatarFragment, null)
                    .commit();
        }
    }
}
