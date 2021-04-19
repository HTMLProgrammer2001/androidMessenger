package htmlprogrammer.labs.messanger.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;


public class MeViewModel extends ViewModel {
    private MeStore meStore = MeStore.getInstance();

    private MutableLiveData<User> userData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingData = new MutableLiveData<>();
    private MutableLiveData<String> tokenData = new MutableLiveData<>();

    MeViewModel(){
        meStore.addTokenObserver((newToken) -> tokenData.postValue(newToken));
        meStore.addUserObserver((newUser) -> userData.postValue(newUser));
        meStore.addLoadingObserver((newLoading) -> loadingData.postValue(newLoading));
    }

    public MutableLiveData<User> getUser(){
        return userData;
    }

    public MutableLiveData<Boolean> getLoadingData(){
        return loadingData;
    }

    public MutableLiveData<String> getToken(){
        return tokenData;
    }
}
