package htmlprogrammer.labs.messanger.viewmodels.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.TreeSet;

import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.models.Message;

public class ChatMessagesViewModel extends ViewModel {
    private ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<TreeSet<Message>> messages = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    ChatMessagesViewModel(){
        chatMessagesStore.addLoadingObserver((newLoading) -> isLoading.postValue(newLoading));
        chatMessagesStore.addMessagesObserver((newMessages) -> messages.postValue(newMessages));
        chatMessagesStore.addErrorObserver((newError) -> error.postValue(newError));
    }

    public MutableLiveData<Boolean> getLoading() {
        return isLoading;
    }

    public MutableLiveData<TreeSet<Message>> getMessages() {
        return messages;
    }

    public MutableLiveData<String> getError() {
        return error;
    }
}
