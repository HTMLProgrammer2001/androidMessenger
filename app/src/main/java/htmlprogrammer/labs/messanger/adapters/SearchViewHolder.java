package htmlprogrammer.labs.messanger.adapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.fragments.UserAvatar;

class SearchViewHolder extends RecyclerView.ViewHolder{
    private TextView name;
    private TextView message;
    private TextView time;
    private TextView unread;
    private FrameLayout avatar;
    private ConstraintLayout root;
    private View online;
    private int id;

    private AppCompatActivity ctx;

    SearchViewHolder(AppCompatActivity ctx, View view){
        super(view);
        this.ctx = ctx;

        try {
            this.root = view.findViewById(R.id.root);
            this.name = view.findViewById(R.id.name);
            this.message = view.findViewById(R.id.message);
            this.time = view.findViewById(R.id.time);
            this.unread = view.findViewById(R.id.unread);
            this.avatar = view.findViewById(R.id.dialogAvatar);
            this.online = view.findViewById(R.id.online);
            this.id = View.generateViewId();

            this.avatar.setId(id);
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
                .replace(id, avatarFragment, null)
                .commit();
    }

    void setOnline(boolean isOnline){
        online.setVisibility(isOnline ? View.VISIBLE : View.INVISIBLE);
    }

    void setSelected(boolean isSelected){
        this.root.setBackgroundColor(ctx.getResources().getColor(isSelected ? R.color.bgBlue : R.color.bgWhite));
    }

    void setOnClick(View.OnClickListener listener){
        this.root.setOnClickListener(listener);
    }
}
