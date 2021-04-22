package htmlprogrammer.labs.messanger.store.search;

import htmlprogrammer.labs.messanger.interfaces.Observable;
import htmlprogrammer.labs.messanger.interfaces.Observer;

public class SearchStore {
    private static SearchStore instance;

    private Observable<String> searchText = new Observable<>("");

    public static SearchStore getInstance(){
        if(instance == null)
            instance = new SearchStore();

        return instance;
    }

    public void addSearchTextObserver(Observer<String> observer){
        this.searchText.addObserver(observer);
    }

    public void setSearchText(String searchText){
        this.searchText.notifyObservers(searchText);
    }

    public String getSearchText(){
        return searchText.getState();
    }
}
