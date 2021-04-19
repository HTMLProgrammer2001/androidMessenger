package htmlprogrammer.labs.messanger.store;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.Dialog;

public class SearchStore {
    private static SearchStore instance;

    private Observable<ArrayList<Dialog>> dialogs = new Observable<>(new ArrayList<>());
    private Observable<Boolean> loading = new Observable<>(false);
    private Observable<String> searchText = new Observable<>("");
    private Observable<String> error = new Observable<>("");

    public static SearchStore getInstance(){
        if(instance == null)
            instance = new SearchStore();

        return instance;
    }

    public void addDialogs(ArrayList<Dialog> newDialogs){
        ArrayList<Dialog> curDialogs = dialogs.getState();
        curDialogs.addAll(newDialogs);

        dialogs.notifyObservers(curDialogs);
    }

    public void setDialogs(ArrayList<Dialog> newDialogs){
        this.dialogs.notifyObservers(newDialogs);
    }

    public void setError(String error){
        this.error.notifyObservers(error);
    }

    public void setSearchText(String searchText){
        this.searchText.notifyObservers(searchText);
    }

    public void startLoading(){
        this.error.notifyObservers(null);
        this.loading.notifyObservers(true);
    }

    public void stopLoading(){
        this.loading.notifyObservers(false);
    }

    public void addDialogsObserver(Observer<ArrayList<Dialog>> observer){
        this.dialogs.addObserver(observer);
    }

    public void addLoadingObserver(Observer<Boolean> observer){
        this.loading.addObserver(observer);
    }

    public void addErrorObserver(Observer<String> observer){
        this.error.addObserver(observer);
    }

    public void addSearchTextObserver(Observer<String> observer){
        this.searchText.addObserver(observer);
    }
}
