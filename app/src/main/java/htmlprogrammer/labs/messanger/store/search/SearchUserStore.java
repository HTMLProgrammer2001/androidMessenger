package htmlprogrammer.labs.messanger.store.search;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.User;

public class SearchUserStore {
    private static SearchUserStore instance;

    private Observable<User> user = new Observable<>(null);
    private Observable<Boolean> loading = new Observable<>(false);
    private Observable<String> error = new Observable<>("");

    public static SearchUserStore getInstance(){
        if(instance == null)
            instance = new SearchUserStore();

        return instance;
    }

    public void reset(){
        loading.notifyObservers(false);
        error.notifyObservers(null);
        user.notifyObservers(null);
    }

    public void setError(String error){
        this.error.notifyObservers(error);
    }

    public void startLoading(){
        this.error.notifyObservers(null);
        this.loading.notifyObservers(true);
    }

    public void stopLoading(){
        this.loading.notifyObservers(false);
    }

    public void addLoadingObserver(Observer<Boolean> observer){
        this.loading.addObserver(observer);
    }

    public void addErrorObserver(Observer<String> observer){
        this.error.addObserver(observer);
    }

    public boolean getLoading() {
        return loading.getState();
    }

    public void addUserObserver(Observer<User> observer){
        this.user.addObserver(observer);
    }

    public User getUser(){
        return user.getState();
    }

    public void setUser(User user){
        this.user.notifyObservers(user);
    }
}
