package htmlprogrammer.labs.messanger.websockets;

import org.json.JSONObject;

import java.net.URISyntaxException;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.constants.WSEvents;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import htmlprogrammer.labs.messanger.store.search.SearchMessagesStore;
import htmlprogrammer.labs.messanger.store.search.SearchUserStore;
import io.socket.client.IO;
import io.socket.client.Socket;

public class Websocket {
    private static Socket socket;

    public static void connect(String token) throws URISyntaxException{
        if(socket != null)
            socket.close();

        socket = IO.socket(BuildConfig.WS_URL + "?Token=" + token);
        addListeners();
        socket.connect();
    }

    private static void addListeners(){
        socket.on(WSEvents.NEW_MESSAGE.getValue(), args -> {
            SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
            ChatStore chatStore = ChatStore.getInstance();
            ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();

            //parse data
            Message newMessage = Message.fromJSON((JSONObject) args[0]);
            Dialog dialog = newMessage.getDialog();

            //set message to dialog
            dialog.setMessage(newMessage);

            //add dialog to search bar
            searchDialogsStore.addDialog(dialog);

            if(chatStore.getDialog() != null && chatStore.getDialog().getId().equals(dialog.getId()))
                chatMessagesStore.addMessage(newMessage);
        });

        socket.on(WSEvents.DELETE_MESSAGE.getValue(), args -> {
            //get stores
            SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
            SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();
            ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();

            String msgID = (String) args[0];

            //update data
            searchMessagesStore.deleteMessageById(msgID);
            chatMessagesStore.deleteMessageById(msgID);
            searchDialogsStore.deleteLastMessage(msgID);
        });

        socket.on(WSEvents.UPDATE_MESSAGE.getValue(), args -> {
            //get stores
            SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
            SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();
            ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();

            Message message = Message.fromJSON((JSONObject) args[0]);

            //update data
            searchMessagesStore.updateMessage(message);
            searchDialogsStore.updateLastMessage(message);
            chatMessagesStore.updateMessage(message);
        });

        socket.on(WSEvents.NEW_DIALOG.getValue(), args -> {
            SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();

            Dialog dialog = Dialog.fromJSON((JSONObject) args[0]);
            searchDialogsStore.addDialog(dialog);
        });

        socket.on(WSEvents.ONLINE.getValue(), args -> setOnlineStatus((String) args[0], true));
        socket.on(WSEvents.OFFLINE.getValue(), args -> setOnlineStatus((String) args[0], false));

        socket.on(WSEvents.TOGGLE_BAN.getValue(), args -> {
           ChatStore chatStore = ChatStore.getInstance();

           String toggleBanForUser = (String) args[0];

           //update store
           User chatUser = chatStore.getUser();
           Dialog chatDialog = chatStore.getDialog();
           if(chatUser != null && chatUser.getId().equals(toggleBanForUser)){
               chatDialog.setActive(!chatDialog.isActive());
               chatStore.setDialog(chatDialog);
           }
        });

        socket.on("error", args -> socket.connect());
    }

    private static void setOnlineStatus(String userID, boolean isOnline){
        //get stores
        ChatStore chatStore = ChatStore.getInstance();
        SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();
        SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
        SearchUserStore searchUserStore = SearchUserStore.getInstance();

        //update chat user state
        User chatUser = chatStore.getUser();
        if(chatUser != null && chatUser.getId().equals(userID)){
            chatUser.setOnline(isOnline);
            chatStore.setUser(chatUser);
        }

        //update search state
        searchMessagesStore.updateUserOnline(userID, isOnline);
        searchDialogsStore.updateUserOnline(userID, isOnline);

        User searchUser = searchUserStore.getUser();
        if(searchUser != null && searchUser.getId().equals(userID)){
            searchUser.setOnline(isOnline);
            searchUserStore.setUser(searchUser);
        }
    }
}
