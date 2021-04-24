package htmlprogrammer.labs.messanger.fragments.dialogsFragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.DialogsActivity;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.DialogAdapter;
import htmlprogrammer.labs.messanger.api.SearchAPI;
import htmlprogrammer.labs.messanger.constants.SearchTypes;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import htmlprogrammer.labs.messanger.store.search.SearchStore;
import htmlprogrammer.labs.messanger.viewmodels.search.SearchDialogsViewModel;
import htmlprogrammer.labs.messanger.viewmodels.search.SearchViewModel;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogsFragment extends Fragment {
    private SearchDialogsViewModel searchDialogsVM;
    private SearchViewModel searchVM;
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
    private DialogAdapter adapter;

    private DialogsActivity activity;

    private RecyclerView list;
    private TextView title;
    private TextView error;
    private TextView loading;
    private TextView loadMore;

    public DialogsFragment() { }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DialogsActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchDialogsVM = ViewModelProviders.of(this).get(SearchDialogsViewModel.class);
        searchVM = ViewModelProviders.of(this).get(SearchViewModel.class);

        list = view.findViewById(R.id.list);
        title = view.findViewById(R.id.title);
        error = view.findViewById(R.id.error);
        loading = view.findViewById(R.id.loading);
        loadMore = view.findViewById(R.id.loadMore);

        adapter = new DialogAdapter(requireActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        loadMore.setVisibility(View.GONE);

        addHandlers();
    }

    private void addHandlers(){
        searchVM.getSearchType().observe(this, type -> {
            title.setVisibility(type.equals(SearchTypes.DEFAULT) ? View.GONE : View.VISIBLE);
            loadMore.setVisibility(type.equals(SearchTypes.DEFAULT) ? View.GONE : View.VISIBLE);
        });

        searchDialogsVM.getLoading().observe(this, isLoading -> {
            loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            boolean isAppear = searchDialogsStore.hasMore() && !searchVM.getSearchType().equals(SearchTypes.DEFAULT);
            loadMore.setVisibility(isAppear ? View.VISIBLE : View.GONE);
        });

        searchDialogsVM.getError().observe(this, errorStr -> {
            error.setVisibility(errorStr == null || errorStr.isEmpty() ? View.GONE : View.VISIBLE);
        });

        searchDialogsVM.getDialogs().observe(this, dialogs -> {
            if(dialogs.size() == 0)
                loadMore.setVisibility(View.GONE);

            adapter.setData(dialogs);
        });

        loadMore.setOnClickListener((view) -> activity.startDialogsLoading());
    }
}
