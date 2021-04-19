package htmlprogrammer.labs.messanger.store;


import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.User;

public class MeStore {
    private static MeStore instance;

    private Observable<String> token = new Observable<>("");
    private Observable<User> user = new Observable<>();
    private Observable<Boolean> loading = new Observable<>(false);

    public static MeStore getInstance(){
        if(instance == null)
            instance = new MeStore();

        return instance;
    }

    public void setToken(String newToken){
        this.token.notifyObservers(newToken);
    }

    public void setUser(User newUser){
        this.user.notifyObservers(newUser);
    }

    public void startLoading(){
        this.loading.notifyObservers(true);
    }

    public void stopLoading(){
        this.loading.notifyObservers(false);
    }

    public void addTokenObserver(Observer<String> observer){
        this.token.addObserver(observer);
    }

    public void addUserObserver(Observer<User> observer){
        this.user.addObserver(observer);
    }

    public void addLoadingObserver(Observer<Boolean> observer){
        this.loading.addObserver(observer);
    }

    public String getToken(){
        return token.getState();
    }

    public User getUser() {
        return user.getState();
    }
}
