package htmlprogrammer.labs.messanger.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import htmlprogrammer.labs.messanger.models.User;

public class UserInfoViewModel extends ViewModel {
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<String> dialogID = new MutableLiveData<>();

    public MutableLiveData<User> getUser(){
        return user;
    }

    public void setUser(User newUser){
        user.postValue(newUser);
    }

    public MutableLiveData<String> getDialogID(){
        return dialogID;
    }

    public void setDialogID(String newDialog){
        dialogID.postValue(newDialog);
    }
}
