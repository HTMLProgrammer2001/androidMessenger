package htmlprogrammer.labs.messanger.websockets.listeners;

import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import htmlprogrammer.labs.messanger.store.search.SearchMessagesStore;
import htmlprogrammer.labs.messanger.store.search.SearchUserStore;
import io.socket.emitter.Emitter;

public class ChangeOnlineListener implements Emitter.Listener {
    private ChatStore chatStore = ChatStore.getInstance();
    private SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
    private SearchUserStore searchUserStore = SearchUserStore.getInstance();

    private boolean state = false;

    public ChangeOnlineListener(boolean state){
        this.state = state;
    }

    @Override
    public void call(Object... args) {
        String userID = (String) args[0];

        User chatUser = chatStore.getUser();
        if(chatUser != null && chatUser.getId().equals(userID)){
            chatUser.setOnline(state);
            chatStore.setUser(chatUser);
        }

        //update search state
        searchMessagesStore.updateUserOnline(userID, state);
        searchDialogsStore.updateUserOnline(userID, state);

        User searchUser = searchUserStore.getUser();
        if(searchUser != null && searchUser.getId().equals(userID)){
            searchUser.setOnline(state);
            searchUserStore.setUser(searchUser);
        }
    }
}
