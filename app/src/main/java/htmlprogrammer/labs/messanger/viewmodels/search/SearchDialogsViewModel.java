package htmlprogrammer.labs.messanger.viewmodels.search;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.TreeSet;

import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;

public class SearchDialogsViewModel extends ViewModel {
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<TreeSet<Dialog>> dialogs = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    SearchDialogsViewModel(){
        searchDialogsStore.addLoadingObserver((newLoading) -> isLoading.postValue(newLoading));
        searchDialogsStore.addDialogsObserver((newDialogs) -> dialogs.postValue(newDialogs));
        searchDialogsStore.addErrorObserver((newError) -> error.postValue(newError));
    }

    public MutableLiveData<Boolean> getLoading() {
        return isLoading;
    }

    public MutableLiveData<TreeSet<Dialog>> getDialogs() {
        return dialogs;
    }

    public MutableLiveData<String> getError() {
        return error;
    }
}
