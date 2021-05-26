package htmlprogrammer.labs.messanger.store.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;
import htmlprogrammer.labs.messanger.models.Dialog;

public class SearchDialogsStore {
    private static SearchDialogsStore instance;

    private Observable<TreeSet<Dialog>> dialogs = new Observable<>(new TreeSet<>());
    private Observable<Boolean> loading = new Observable<>(false);
    private Observable<String> error = new Observable<>("");

    private int pageSize = 15;
    private int curPage = 0;
    private int totalPages = 1;

    public static SearchDialogsStore getInstance(){
        if(instance == null)
            instance = new SearchDialogsStore();

        return instance;
    }

    public void reset(){
        loading.notifyObservers(false);
        error.notifyObservers(null);
        dialogs.notifyObservers(new TreeSet<>());

        pageSize = 10;
        curPage = 0;
        totalPages = 1;
    }

    public void addDialogs(List<Dialog> newDialogs){
        TreeSet<Dialog> curDialogs = dialogs.getState();
        curDialogs.removeAll(newDialogs);
        curDialogs.addAll(newDialogs);
        dialogs.notifyObservers(curDialogs);
    }

    public void addDialog(Dialog newDialog){
        addDialogs(Arrays.asList(newDialog));
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

    public boolean getLoading() {
        return loading.getState();
    }
}
