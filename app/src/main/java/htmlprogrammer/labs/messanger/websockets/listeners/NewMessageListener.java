package htmlprogrammer.labs.messanger.websockets.listeners;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import org.json.JSONObject;

import java.util.Arrays;

import htmlprogrammer.labs.messanger.App;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import htmlprogrammer.labs.messanger.websockets.Websocket;
import io.socket.emitter.Emitter;

public class NewMessageListener implements Emitter.Listener {
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
    private ChatStore chatStore = ChatStore.getInstance();
    private ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();
    private MeStore meStore = MeStore.getInstance();

    @Override
    public void call(Object... args) {
        //parse data
        Message newMessage = Message.fromJSON((JSONObject) args[0]);
        Dialog dialog = newMessage.getDialog();

        //set message to dialog
        dialog.setMessage(newMessage);

        //add dialog to search bar
        searchDialogsStore.addDialog(dialog);

        if(chatStore.getDialog() != null && chatStore.getDialog().getId().equals(dialog.getId())) {
            chatMessagesStore.addMessage(newMessage);
            Websocket.viewMessages(Arrays.asList(newMessage.getId()));
        }

        //play ringtone
        if(newMessage.getAuthor().getId().equals(meStore.getUser().getId()))
            return;

        try{
            Uri notificationRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(App.getContext(), notificationRingtone);
            r.play();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
