package htmlprogrammer.labs.messanger.interfaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import htmlprogrammer.labs.messanger.App;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.MessageAPI;
import htmlprogrammer.labs.messanger.constants.MessageTypes;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.chat.SelectedMessagesStore;
import htmlprogrammer.labs.messanger.store.chat.SendMessagesStore;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
    protected ConstraintLayout group;
    protected TextView date;
    protected ImageView check;

    private Timer timer;
    private Message message;
    private boolean isPressed = false;
    private boolean isSelected = false;

    private SelectedMessagesStore selectedMessagesStore = SelectedMessagesStore.getInstance();

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        //find elements
        group = itemView.findViewById(R.id.group);
        date = itemView.findViewById(R.id.date);
        check = itemView.findViewById(R.id.check);
    }

    protected void setOwn(boolean isOwn){
        //update position(left or right)
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) group.getLayoutParams();
        params.horizontalBias = isOwn ? 1 : 0;
        group.setLayoutParams(params);

        Context ctx = App.getContext();

        //update bg
        Drawable bg = ctx.getResources().getDrawable(R.drawable.bg_chat_message);
        bg = DrawableCompat.wrap(bg);
        DrawableCompat.setTint(bg, ctx.getResources().getColor(isOwn ? R.color.bgMessage : R.color.bgWhite));

        group.setBackground(bg);
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void setSending(Message msg){
        //set selection listeners
        this.message = msg;
        group.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                isPressed = true;
                timer = new Timer();

                //not select sending or special messages
                if(msg.isSending() || msg.getType().equals(MessageTypes.SPECIAL))
                    return false;

                if(isSelected) {
                    //un select
                    selectedMessagesStore.removeMessage(msg);
                }
                //start selecting
                else if(!selectedMessagesStore.isSelectMode())
                    timer.schedule(new SelectTask(), 500);
                else
                    timer.schedule(new SelectTask(), 0);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                isPressed = false;
            }

            return false;
        });


        check.setImageDrawable(App.getContext().getDrawable(msg.isSending() ? R.drawable.sending : R.drawable.check));

        if(msg.isSending()) {
            group.setOnClickListener(v -> {
                //remove sending from store and cancel http call
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

    public void select(){
        isSelected = true;

        //update bg
        Drawable bg = App.getContext().getResources().getDrawable(R.drawable.bg_chat_message);
        bg = DrawableCompat.wrap(bg);
        DrawableCompat.setTint(bg, App.getContext().getResources().getColor(R.color.bgBlue));

        group.setBackground(bg);
    }

    abstract public void updateUI(Message message, FragmentManager manager);

    class SelectTask extends TimerTask{
        @Override
        public void run() {
            if(isPressed)
                selectedMessagesStore.addMessage(message);
        }
    }
}
