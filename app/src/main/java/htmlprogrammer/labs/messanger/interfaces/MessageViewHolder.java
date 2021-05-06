package htmlprogrammer.labs.messanger.interfaces;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import htmlprogrammer.labs.messanger.App;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.MessageAPI;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.chat.SendMessagesStore;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
    protected ConstraintLayout group;
    protected TextView date;
    protected ImageView check;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        group = itemView.findViewById(R.id.group);
        date = itemView.findViewById(R.id.date);
        check = itemView.findViewById(R.id.check);
    }

    protected void setOwn(boolean isOwn){
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) group.getLayoutParams();
        params.horizontalBias = isOwn ? 1 : 0;
        group.setLayoutParams(params);

        Context ctx = App.getContext();

        Drawable bg = ctx.getResources().getDrawable(R.drawable.bg_chat_message);
        bg = DrawableCompat.wrap(bg);
        DrawableCompat.setTint(bg, ctx.getResources().getColor(isOwn ? R.color.bgMessage : R.color.bgWhite));

        group.setBackground(bg);
    }

    protected void setSending(Message msg){
        check.setImageDrawable(App.getContext().getDrawable(msg.isSending() ? R.drawable.sending : R.drawable.check));

        if(msg.isSending()) {
            group.setOnClickListener(v -> {
                SendMessagesStore.getInstance().deleteMessage(msg);
                MessageAPI.cancelSend(msg.getId());
            });
        }
    }

    public void setDate(String dateStr){
        date.setVisibility(View.VISIBLE);
        date.setText(dateStr);
    }

    public void hideDate(){
        date.setVisibility(View.GONE);
    }

    abstract public void updateUI(Message message, FragmentManager manager);
}
