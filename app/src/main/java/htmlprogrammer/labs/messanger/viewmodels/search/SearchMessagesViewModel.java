package htmlprogrammer.labs.messanger.viewmodels.search;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.TreeSet;

import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.search.SearchMessagesStore;

public class SearchMessagesViewModel extends ViewModel {
    private SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<TreeSet<Message>> messages = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    SearchMessagesViewModel(){
        searchMessagesStore.addLoadingObserver((newLoading) -> isLoading.postValue(newLoading));
        searchMessagesStore.addMessagesObserver((newMessages) -> messages.postValue(newMessages));
        searchMessagesStore.addErrorObserver((newError) -> error.postValue(newError));
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
