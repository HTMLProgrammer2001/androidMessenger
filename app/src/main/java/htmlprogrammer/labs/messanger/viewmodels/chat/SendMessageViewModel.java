package htmlprogrammer.labs.messanger.viewmodels.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.TreeSet;

import htmlprogrammer.labs.messanger.store.chat.SendMessagesStore;
import htmlprogrammer.labs.messanger.models.Message;

public class SendMessageViewModel extends ViewModel {
    private SendMessagesStore sendMessagesStore = SendMessagesStore.getInstance();
    private MutableLiveData<TreeSet<Message>> messages = new MutableLiveData<>();

    public void addHandlerFor(String dialogID){
        sendMessagesStore.addObserverForDialog(dialogID, newMessages -> messages.postValue(newMessages));
    }

    public MutableLiveData<TreeSet<Message>> getMessages(){
        return messages;
    }
}
