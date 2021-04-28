package htmlprogrammer.labs.messanger.store.search;

import java.util.ArrayList;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.Message;

public class SearchMessagesStore {
    private static SearchMessagesStore instance;

    private Observable<TreeSet<Message>> messages = new Observable<>(new TreeSet<>());
    private Observable<Boolean> loading = new Observable<>(false);
    private Observable<String> error = new Observable<>("");

    private int pageSize = 15;
    private int curPage = 0;
    private int totalPages = 1;

    public static SearchMessagesStore getInstance(){
        if(instance == null)
            instance = new SearchMessagesStore();

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

    public void setMessages(ArrayList<Message> newMessages){
        TreeSet<Message> messagesSet = new TreeSet<>(newMessages);
        messages.notifyObservers(messagesSet);
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
