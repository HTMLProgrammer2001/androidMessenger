package htmlprogrammer.labs.messanger.store.chat;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.User;

public class ChatStore {
    private static ChatStore instance;

    private Observable<User> user = new Observable<>(null);
    private Observable<Dialog> dialog = new Observable<>(null);
    private Observable<Boolean> isLoading = new Observable<>(false);

    public static ChatStore getInstance(){
        if(instance == null)
            instance = new ChatStore();

        return instance;
    }

    public void setUser(User user){
        this.user.notifyObservers(user);
    }

    public User getUser(){
        return user.getState();
    }

    public void setDialog(Dialog dialog){
        this.dialog.notifyObservers(dialog);
    }

    public Dialog getDialog(){
        return dialog.getState();
    }

    public void setLoading(boolean isLoading){
        this.isLoading.notifyObservers(isLoading);
    }

    public boolean getLoading(){
        return isLoading.getState();
    }

    public void addUserObserver(Observer<User> observer){
        user.addObserver(observer);
    }

    public void addDialogObserver(Observer<Dialog> observer){
        dialog.addObserver(observer);
    }

    public void addLoadingObserver(Observer<Boolean> observer){
        isLoading.addObserver(observer);
    }

    public void reset(){
        dialog.notifyObservers(null);
        user.notifyObservers(null);
        isLoading.notifyObservers(false);
    }

    public void startLoading() {
        isLoading.notifyObservers(true);
    }

    public void stopLoading(){
        isLoading.notifyObservers(false);
    }
}
