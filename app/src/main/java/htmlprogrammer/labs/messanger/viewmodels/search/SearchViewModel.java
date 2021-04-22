package htmlprogrammer.labs.messanger.viewmodels.search;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import htmlprogrammer.labs.messanger.constants.SearchTypes;
import htmlprogrammer.labs.messanger.store.search.SearchStore;

public class SearchViewModel extends ViewModel {
    private SearchStore searchStore = SearchStore.getInstance();
    private MutableLiveData<String> searchText = new MutableLiveData<>();
    private MutableLiveData<SearchTypes> type = new MutableLiveData<>();

    SearchViewModel(){
        type.postValue(SearchTypes.DEFAULT);

        searchStore.addSearchTextObserver((newSearchText) -> {
            searchText.postValue(newSearchText);

            if(newSearchText.equals(""))
                type.postValue(SearchTypes.DEFAULT);
            else if(newSearchText.startsWith("@"))
                type.postValue(SearchTypes.NICK);
            else
                type.postValue(SearchTypes.TEXT);
        });
    }

    public MutableLiveData<String> getSearchText(){
        return searchText;
    }

    public MutableLiveData<SearchTypes> getSearchType(){
        return type;
    }
}
