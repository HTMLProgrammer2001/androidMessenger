package htmlprogrammer.labs.messanger.store.chat;

import java.util.TreeSet;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.Message;

public class SendMessagesStore {
    private static SendMessagesStore instance;

    private Observable<TreeSet<Message>> messages = new Observable<>(new TreeSet<>());

    public static SendMessagesStore getInstance(){
        if(instance == null)
            instance = new SendMessagesStore();

        return instance;
    }

    public void reset(){
        messages.notifyObservers(new TreeSet<>());
    }

    public void addMessage(Message msg){
        TreeSet<Message> curMessages = messages.getState();
        curMessages.add(msg);
        messages.notifyObservers(curMessages);
    }

    public void deleteMessage(Message msg){
        TreeSet<Message> curMessages = messages.getState();
        curMessages.remove(msg);
        messages.notifyObservers(curMessages);
    }

    public TreeSet<Message> getForDialogID(String dialogID){
        TreeSet<Message> filtered = new TreeSet<>();

        for(Message msg : messages.getState()){
            if(msg.getDialog().getId().equals(dialogID))
                filtered.add(msg);
        }

        return filtered;
    }

    public void addObserverForDialog(String dialogID, Observer<TreeSet<Message>> observer){
        messages.addObserver(messages -> {
            TreeSet<Message> filtered = new TreeSet<>();

            for(Message msg : messages){
                if(msg.getDialog().getId().equals(dialogID))
                    filtered.add(msg);
            }

            observer.update(filtered);
        });
    }
}
