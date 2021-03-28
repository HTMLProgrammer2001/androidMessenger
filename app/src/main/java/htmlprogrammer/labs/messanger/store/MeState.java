package htmlprogrammer.labs.messanger.store;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.models.User;


public class MeState extends ViewModel {
    private MutableLiveData<User> userData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingData = new MutableLiveData<>();

    MeState(){
        userData.setValue(null);
        loadingData.setValue(false);
    }

    public MutableLiveData<User> getUser(){
        return userData;
    }

    public void setUser(User user){
        userData.postValue(user);
    }

    public MutableLiveData<Boolean> getLoadingData(){
        return loadingData;
    }

    public void setLoading(Boolean isLoading){
        loadingData.postValue(isLoading);
    }
}
