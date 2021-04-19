package htmlprogrammer.labs.messanger.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.SearchStore;

public class SearchViewModel extends ViewModel {
    private SearchStore searchStore = SearchStore.getInstance();

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Dialog>> dialogs = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> searchText = new MutableLiveData<>();

    SearchViewModel(){
        searchStore.addLoadingObserver((newLoading) -> isLoading.postValue(newLoading));
        searchStore.addDialogsObserver((newDialogs) -> dialogs.postValue(newDialogs));
        searchStore.addErrorObserver((newError) -> error.postValue(newError));
        searchStore.addSearchTextObserver((newSearchText) -> searchText.postValue(newSearchText));
    }

    public MutableLiveData<Boolean> getLoading() {
        return isLoading;
    }

    public MutableLiveData<ArrayList<Dialog>> getDialogs() {
        return dialogs;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<String> getSearchText(){
        return searchText;
    }
}
