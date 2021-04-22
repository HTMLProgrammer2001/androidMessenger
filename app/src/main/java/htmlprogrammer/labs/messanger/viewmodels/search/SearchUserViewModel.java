package htmlprogrammer.labs.messanger.viewmodels.search;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.search.SearchUserStore;

public class SearchUserViewModel extends ViewModel {
    private SearchUserStore searchUserStore = SearchUserStore.getInstance();

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    SearchUserViewModel(){
        searchUserStore.addLoadingObserver((newLoading) -> isLoading.postValue(newLoading));
        searchUserStore.addUserObserver((newUser) -> user.postValue(newUser));
        searchUserStore.addErrorObserver((newError) -> error.postValue(newError));
    }

    public MutableLiveData<Boolean> getLoading() {
        return isLoading;
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<String> getError() {
        return error;
    }
}
