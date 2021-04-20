package htmlprogrammer.labs.messanger.store;

import java.util.ArrayList;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.Dialog;

public class SearchStore {
    private static SearchStore instance;

    private Observable<TreeSet<Dialog>> dialogs = new Observable<>(new TreeSet<>());
    private Observable<Boolean> loading = new Observable<>(false);
    private Observable<String> searchText = new Observable<>("");
    private Observable<String> error = new Observable<>("");
    private int pageSize = 15;
    private int curPage = 0;
    private int totalPages = 1;

    public static SearchStore getInstance(){
        if(instance == null)
            instance = new SearchStore();

        return instance;
    }

    public void reset(){
        loading.notifyObservers(false);
        error.notifyObservers(null);
        pageSize = 10;
        curPage = 0;
        totalPages = 1;
    }

    public void addDialogs(ArrayList<Dialog> newDialogs){
        TreeSet<Dialog> curDialogs = dialogs.getState();
        curDialogs.addAll(newDialogs);
        dialogs.notifyObservers(curDialogs);
    }

    public void setDialogs(ArrayList<Dialog> newDialogs){
        TreeSet<Dialog> dialogsSet = new TreeSet<>(newDialogs);
        this.dialogs.notifyObservers(dialogsSet);
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

    public void setSearchText(String searchText){
        this.searchText.notifyObservers(searchText);
    }

    public String getSearchText(){
        return searchText.getState();
    }

    public void startLoading(){
        this.error.notifyObservers(null);
        this.loading.notifyObservers(true);
    }

    public void stopLoading(){
        this.loading.notifyObservers(false);
    }

    public void addDialogsObserver(Observer<TreeSet<Dialog>> observer){
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

    public boolean getLoading() {
        return loading.getState();
    }
}
