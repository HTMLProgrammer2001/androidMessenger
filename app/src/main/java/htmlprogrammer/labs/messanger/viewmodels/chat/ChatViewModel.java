package htmlprogrammer.labs.messanger.viewmodels.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import htmlprogrammer.labs.messanger.constants.ChatTypes;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;

public class ChatViewModel extends ViewModel {
    private ChatStore chatStore = ChatStore.getInstance();

    private MutableLiveData<User> userData = new MutableLiveData<>();
    private MutableLiveData<Dialog> dialogData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingData = new MutableLiveData<>();
    private MutableLiveData<ChatTypes> type = new MutableLiveData<>();

    ChatViewModel(){
        chatStore.addDialogObserver(newDialog -> {
            dialogData.postValue(newDialog);
            type.postValue(ChatTypes.DIALOG);
        });
        chatStore.addUserObserver(newUser -> {
            userData.postValue(newUser);
            type.postValue(ChatTypes.USER);
        });

        chatStore.addLoadingObserver(isLoading -> loadingData.postValue(isLoading));
    }

    public MutableLiveData<User> getUserData(){
        return userData;
    }

    public MutableLiveData<Dialog> getDialogData() {
        return dialogData;
    }

    public MutableLiveData<Boolean> getLoadingData() {
        return loadingData;
    }
}
