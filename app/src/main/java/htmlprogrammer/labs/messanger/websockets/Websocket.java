package htmlprogrammer.labs.messanger.websockets;

import org.json.JSONObject;

import java.net.URISyntaxException;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.constants.WSEvents;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import htmlprogrammer.labs.messanger.store.search.SearchMessagesStore;
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
            SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
            SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();
            ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();

            String msgID = (String) args[0];
            searchMessagesStore.deleteMessageById(msgID);
            chatMessagesStore.deleteMessageById(msgID);
            searchDialogsStore.deleteLastMessage(msgID);
        });

        socket.on(WSEvents.UPDATE_MESSAGE.getValue(), args -> {
            SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
            SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();
            ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();

            Message message = Message.fromJSON((JSONObject) args[0]);
            searchMessagesStore.updateMessage(message);
            searchDialogsStore.updateLastMessage(message);
            chatMessagesStore.updateMessage(message);
        });

        socket.on(WSEvents.NEW_DIALOG.getValue(), args -> {
            SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();

            Dialog dialog = Dialog.fromJSON((JSONObject) args[0]);
            searchDialogsStore.addDialog(dialog);
        });

        socket.on("error", args -> socket.connect());
    }
}