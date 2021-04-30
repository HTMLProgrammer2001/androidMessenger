package htmlprogrammer.labs.messanger.interfaces;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import htmlprogrammer.labs.messanger.models.Message;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract public void updateUI(Message message, FragmentManager manager);
}
