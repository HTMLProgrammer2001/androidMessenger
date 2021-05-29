package htmlprogrammer.labs.messanger.websockets.listeners;

import org.json.JSONArray;
import org.json.JSONException;

import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import io.socket.emitter.Emitter;

public class ViewMessagesListener implements Emitter.Listener {
    private ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();

    @Override
    public void call(Object... args) {
        JSONArray msgIds = (JSONArray) args[0];

        //update store
        for(int i = 0; i < msgIds.length(); i++){
            try {
                //update chat message state
                String id = msgIds.getString(i);
                Message msg = chatMessagesStore.readMessage(id);

                if(msg == null)
                    continue;

                //update unreaded count in search
                Dialog dlg = msg.getDialog();

                if(dlg == null)
                    continue;

                dlg.setUnread(Math.max(dlg.getUnread() - 1, 0));
                searchDialogsStore.addDialog(dlg);
            }
            catch (JSONException ex){}

        }
    }
}
