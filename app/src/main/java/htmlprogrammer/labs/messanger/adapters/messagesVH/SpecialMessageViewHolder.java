package htmlprogrammer.labs.messanger.adapters.messagesVH;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;

public class SpecialMessageViewHolder extends MessageViewHolder {
    private TextView message;

    public SpecialMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        //find elements
        message = itemView.findViewById(R.id.message);
    }

    public void updateUI(Message message, FragmentManager manager){
        setMessage(message.getMessage());
    }

    void setMessage(String msg){
        message.setText(msg);
    }
}
