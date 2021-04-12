package htmlprogrammer.labs.messanger.store;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.models.Dialog;

public class SearchState extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Dialog>> dialogs = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> searchText = new MutableLiveData<>();

    SearchState(){
        isLoading.setValue(false);
        dialogs.setValue(new ArrayList<>());
        error.setValue(null);
        searchText.setValue("");
    }

    public MutableLiveData<Boolean> getLoading() {
        return isLoading;
    }

    public void setIsLoading(Boolean loading) {
        this.isLoading.postValue(loading);
    }

    public MutableLiveData<ArrayList<Dialog>> getDialogs() {
        return dialogs;
    }

    public void setDialogs(ArrayList<Dialog> dialogs) {
        this.dialogs.postValue(dialogs);
    }

    public void addDialog(Dialog dialog){
        ArrayList<Dialog> curDialogs = this.dialogs.getValue();
        curDialogs.add(dialog);

        this.dialogs.postValue(curDialogs);
    }

    public void addDialogs(ArrayList<Dialog> dialogs){
        ArrayList<Dialog> curDialogs = this.dialogs.getValue();
        curDialogs.addAll(dialogs);

        this.dialogs.postValue(curDialogs);
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public void setError(String error) {
        this.error.postValue(error);
    }

    public MutableLiveData<String> getSearchText(){
        return searchText;
    }

    public void setSearchText(String searchText){
        this.searchText.postValue(searchText);
    }

    public void startLoading(){
        this.error.postValue(null);
        this.isLoading.postValue(true);
    }
}
