package htmlprogrammer.labs.messanger.websockets.listeners;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import io.socket.emitter.Emitter;

public class NewDialogListener implements Emitter.Listener {
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();

    @Override
    public void call(Object... args) {
        Dialog dialog = Dialog.fromJSON((JSONObject) args[0]);
        searchDialogsStore.addDialog(dialog);
    }
}
