package htmlprogrammer.labs.messanger.store.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.Message;

public class ChatMessagesStore {
    private static ChatMessagesStore instance;

    private Observable<TreeSet<Message>> messages = new Observable<>(new TreeSet<>());
    private Observable<Boolean> loading = new Observable<>(false);
    private Observable<String> error = new Observable<>("");

    private int pageSize = 10;
    private int curPage = 0;
    private int totalPages = 1;

    public static ChatMessagesStore getInstance(){
        if(instance == null)
            instance = new ChatMessagesStore();

        return instance;
    }

    public void reset(){
        loading.notifyObservers(false);
        error.notifyObservers(null);
        messages.notifyObservers(new TreeSet<>());

        pageSize = 10;
        curPage = 0;
        totalPages = 1;
    }

    public void addMessages(ArrayList<Message> newMessages){
        TreeSet<Message> curMessages = messages.getState();
        curMessages.addAll(newMessages);
        messages.notifyObservers(curMessages);
    }

    public void deleteMessageById(String msgID){
        TreeSet<Message> curMessages = messages.getState();
        Iterator<Message> it = curMessages.iterator();

        //iterate through all elements
        while (it.hasNext()){
            Message msg = it.next();

            //if match then delete and notify others
            if(msg.getId().equals(msgID)) {
                it.remove();
                messages.notifyObservers(curMessages);
                break;
            }
        }
    }

    public void updateMessage(Message updatedMsg){
        TreeSet<Message> curMessages = messages.getState();

        if(curMessages.remove(updatedMsg)) {
            curMessages.add(updatedMsg);
            messages.notifyObservers(curMessages);
        }
    }

    public void removeMessages(ArrayList<Message> deleteMessages){
        TreeSet<Message> curMessages = messages.getState();
        curMessages.removeAll(deleteMessages);
        messages.notifyObservers(curMessages);
    }

    public Message readMessage(String id){
        TreeSet<Message> curMessages = messages.getState();

        for(Message msg : curMessages){
            if(msg.getId().equals(id)){
                msg.setReaded(true);
                messages.notifyObservers(curMessages);
                return msg;
            }
        }

        return null;
    }

    public void setMessages(ArrayList<Message> newMessages){
        TreeSet<Message> messagesSet = new TreeSet<>(newMessages);
        messages.notifyObservers(messagesSet);
    }

    public void addMessage(Message newMessage) {
        TreeSet<Message> curMessages = messages.getState();
        curMessages.add(newMessage);
        messages.notifyObservers(curMessages);
    }

    public TreeSet<Message> getMessages(){
        return messages.getState();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean hasMore(){
        return totalPages > curPage;
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

    public void addMessagesObserver(Observer<TreeSet<Message>> observer){
        messages.addObserver(observer);
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
}
