package htmlprogrammer.labs.messanger.websockets;

import org.json.JSONArray;

import java.net.URISyntaxException;
import java.util.List;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.constants.WSEvents;
import htmlprogrammer.labs.messanger.websockets.listeners.ChangeOnlineListener;
import htmlprogrammer.labs.messanger.websockets.listeners.DeleteMessageListener;
import htmlprogrammer.labs.messanger.websockets.listeners.NewDialogListener;
import htmlprogrammer.labs.messanger.websockets.listeners.NewMessageListener;
import htmlprogrammer.labs.messanger.websockets.listeners.ToggleBanListener;
import htmlprogrammer.labs.messanger.websockets.listeners.UpdateMessageListener;
import htmlprogrammer.labs.messanger.websockets.listeners.ViewMessagesListener;
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
        socket.on(WSEvents.NEW_MESSAGE.getValue(), new NewMessageListener());
        socket.on(WSEvents.DELETE_MESSAGE.getValue(), new DeleteMessageListener());
        socket.on(WSEvents.UPDATE_MESSAGE.getValue(), new UpdateMessageListener());
        socket.on(WSEvents.NEW_DIALOG.getValue(), new NewDialogListener());
        socket.on(WSEvents.TOGGLE_BAN.getValue(), new ToggleBanListener());
        socket.on(WSEvents.ONLINE.getValue(), new ChangeOnlineListener(true));
        socket.on(WSEvents.OFFLINE.getValue(), new ChangeOnlineListener(false));
        socket.on(WSEvents.VIEW_MESSAGES.getValue(), new ViewMessagesListener());

        socket.on("error", args -> socket.connect());
    }

    public static void viewMessages(List<String> msgIds){
        socket.emit(WSEvents.VIEW_MESSAGES.getValue(), new JSONArray(msgIds));
        new ViewMessagesListener().call(new JSONArray(msgIds));
    }
}
