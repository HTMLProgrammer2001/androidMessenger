package htmlprogrammer.labs.messanger.store.chat;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.Message;

public class SelectedMessagesStore {
    private static SelectedMessagesStore instance;

    private Observable<ArrayList<Message>> selectedMessages = new Observable<>(new ArrayList<>());
    private Observable<Message> editMessage = new Observable<>(null);

    public static SelectedMessagesStore getInstance(){
        if(instance == null)
            instance = new SelectedMessagesStore();

        return instance;
    }

    public ArrayList<Message> getSelectedMessages(){
        return selectedMessages.getState();
    }

    public Message getEditMessage(){
        return editMessage.getState();
    }

    public void removeMessage(Message msg){
        ArrayList<Message> curMessages = selectedMessages.getState();
        curMessages.remove(msg);
        selectedMessages.notifyObservers(curMessages);
    }

    public void addMessage(Message msg){
        ArrayList<Message> curMessages = selectedMessages.getState();
        curMessages.add(msg);
        selectedMessages.notifyObservers(curMessages);
    }

    public void setEditMessage(Message edit){
        this.editMessage.notifyObservers(edit);
    }

    public void addSelectedObserver(Observer<ArrayList<Message>> observer){
        selectedMessages.addObserver(observer);
    }

    public void addEditMessageObserver(Observer<Message> observer){
        editMessage.addObserver(observer);
    }

    public boolean isSelectMode(){
        return selectedMessages.getState().size() > 0;
    }

    public void reset(){
        editMessage.notifyObservers(null);
        selectedMessages.notifyObservers(new ArrayList<>());
    }
}
