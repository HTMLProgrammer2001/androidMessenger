package htmlprogrammer.labs.messanger.websockets.listeners;

import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import io.socket.emitter.Emitter;

public class ToggleBanListener implements Emitter.Listener {
    private ChatStore chatStore = ChatStore.getInstance();

    @Override
    public void call(Object... args) {
        String toggleBanForUser = (String) args[0];

        //update store
        User chatUser = chatStore.getUser();
        Dialog chatDialog = chatStore.getDialog();
        if(chatUser != null && chatUser.getId().equals(toggleBanForUser)){
            chatDialog.setActive(!chatDialog.isActive());
            chatStore.setDialog(chatDialog);
        }
    }
}
