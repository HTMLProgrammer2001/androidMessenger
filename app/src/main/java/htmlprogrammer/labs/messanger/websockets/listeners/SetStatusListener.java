package htmlprogrammer.labs.messanger.websockets.listeners;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import io.socket.emitter.Emitter;

public class SetStatusListener implements Emitter.Listener {
    private ChatStore chatStore = ChatStore.getInstance();
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();

    @Override
    public void call(Object... args) {
        JSONObject data = (JSONObject) args[0];

        //update store
        Dialog chatDialog = chatStore.getDialog();

        if(chatDialog != null && chatDialog.getId().equals(data.optString("dialog", ""))){
            chatDialog.setStatus(data.optString("status", ""));
            chatStore.setDialog(chatDialog);
        }

        searchDialogsStore.updateStatus(data.optString("dialog", ""), data.optString("status", ""));
    }
}
