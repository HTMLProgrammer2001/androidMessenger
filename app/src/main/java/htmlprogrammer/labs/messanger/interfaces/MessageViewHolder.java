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
import htmlprogrammer.labs.messanger.helpers.VibrateHelper;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.chat.SelectedMessagesStore;
import htmlprogrammer.labs.messanger.store.chat.SendMessagesStore;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
    protected ConstraintLayout group;
    protected TextView date;
    protected ImageView check;

    private Timer timer;
    private Message message;
    private boolean isSelected = false;
    private boolean isSelectable = true;

    private SelectedMessagesStore selectedMessagesStore = SelectedMessagesStore.getInstance();

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        //find elements
        group = itemView.findViewById(R.id.group);
        date = itemView.findViewById(R.id.date);
        check = itemView.findViewById(R.id.check);

        timer = new Timer();
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
                //not select sending or special messages
                if(msg.isSending() || msg.getType().equals(MessageTypes.SPECIAL) || !isSelectable)
                    return false;

                if(isSelected) {
                    //un select
                    selectedMessagesStore.removeMessage(msg);
                }
                //start selecting
                else if(!selectedMessagesStore.isSelectMode()) {
                    timer = new Timer();
                    timer.schedule(new SelectTask(), 2000);
                }
                else
                    selectedMessagesStore.addMessage(message);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                timer.cancel();
            }

            return true;
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

    public void setSelectable(boolean isSelectable){
        this.isSelectable = isSelectable;
    }

    abstract public void updateUI(Message message, FragmentManager manager);

    class SelectTask extends TimerTask{
        @Override
        public void run() {
            VibrateHelper.getInstance().vibrateChoose();
            selectedMessagesStore.addMessage(message);
        }
    }
}
