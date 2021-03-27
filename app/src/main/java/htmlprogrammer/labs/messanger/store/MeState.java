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

    private void setLoading(Boolean isLoading){
        loadingData.postValue(isLoading);
    }

    public void getMe(String token){
        setLoading(true);

        UserActionsAPI.getMe(token, (err, response) -> {
            //error in loading
            if(err != null || !response.isSuccessful()){
                setLoading(false);
                return;
            }

            try {
                //parse response
                JSONObject userObj = new JSONObject(response.body().string());
                setLoading(false);
                setUser(User.fromJSON(userObj));
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        });
    }
}
