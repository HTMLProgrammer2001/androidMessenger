package htmlprogrammer.labs.messanger.websockets.listeners;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import htmlprogrammer.labs.messanger.store.search.SearchMessagesStore;
import io.socket.emitter.Emitter;

public class UpdateMessageListener implements Emitter.Listener {
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
    private SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();
    private ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();

    @Override
    public void call(Object... args) {
        Message message = Message.fromJSON((JSONObject) args[0]);

        //update data
        searchMessagesStore.updateMessage(message);
        searchDialogsStore.updateLastMessage(message);
        chatMessagesStore.updateMessage(message);
    }
}
