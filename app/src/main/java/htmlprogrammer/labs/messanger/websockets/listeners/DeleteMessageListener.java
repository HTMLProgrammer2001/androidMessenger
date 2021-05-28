package htmlprogrammer.labs.messanger.websockets.listeners;

import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import htmlprogrammer.labs.messanger.store.search.SearchMessagesStore;
import io.socket.emitter.Emitter;

public class DeleteMessageListener implements Emitter.Listener {
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
    private SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();
    private ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();

    @Override
    public void call(Object... args) {
        String msgID = (String) args[0];

        //update data
        searchMessagesStore.deleteMessageById(msgID);
        chatMessagesStore.deleteMessageById(msgID);
        searchDialogsStore.deleteLastMessage(msgID);
    }
}
