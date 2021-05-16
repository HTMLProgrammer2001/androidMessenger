package htmlprogrammer.labs.messanger.viewmodels.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.store.chat.SelectedMessagesStore;
import htmlprogrammer.labs.messanger.models.Message;


public class SelectedMessagesViewModel extends ViewModel {
    private SelectedMessagesStore selectedMessagesStore = SelectedMessagesStore.getInstance();
    private MutableLiveData<ArrayList<Message>> selectedMessages = new MutableLiveData<>();
    private MutableLiveData<Message> editMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSelectMode = new MutableLiveData<>();

    SelectedMessagesViewModel(){
        selectedMessagesStore.addSelectedObserver(selected -> {
            selectedMessages.postValue(selected);
            isSelectMode.postValue(selected.size() > 0);
        });

        selectedMessagesStore.addEditMessageObserver(editMsg -> {
            editMessage.postValue(editMsg);
        });
    }

    public MutableLiveData<ArrayList<Message>> getSelectedMessages(){
        return selectedMessages;
    }

    public MutableLiveData<Boolean> getIsSelectMode(){
        return isSelectMode;
    }

    public MutableLiveData<Message> getEditMessage(){
        return editMessage;
    }
}
