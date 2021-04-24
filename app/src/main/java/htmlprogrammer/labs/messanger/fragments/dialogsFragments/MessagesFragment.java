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

import htmlprogrammer.labs.messanger.DialogsActivity;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.MessageAdapter;
import htmlprogrammer.labs.messanger.constants.SearchTypes;
import htmlprogrammer.labs.messanger.store.search.SearchMessagesStore;
import htmlprogrammer.labs.messanger.viewmodels.search.SearchMessagesViewModel;
import htmlprogrammer.labs.messanger.viewmodels.search.SearchViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {
    private SearchMessagesViewModel searchMessagesVM;
    private SearchViewModel searchVM;
    private SearchMessagesStore messagesStore = SearchMessagesStore.getInstance();
    private MessageAdapter adapter;

    private DialogsActivity activity;

    private RecyclerView list;
    private TextView title;
    private TextView error;
    private TextView loading;
    private TextView loadMore;

    public MessagesFragment() { }

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
        searchMessagesVM = ViewModelProviders.of(this).get(SearchMessagesViewModel.class);
        searchVM = ViewModelProviders.of(this).get(SearchViewModel.class);

        list = view.findViewById(R.id.list);
        title = view.findViewById(R.id.title);
        error = view.findViewById(R.id.error);
        loading = view.findViewById(R.id.loading);
        loadMore = view.findViewById(R.id.loadMore);

        adapter = new MessageAdapter(requireActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        loadMore.setVisibility(View.GONE);
        title.setText(R.string.messages);

        addHandlers();
    }

    private void addHandlers(){
        searchVM.getSearchType().observe(this, type -> {
            title.setVisibility(type.equals(SearchTypes.DEFAULT) ? View.GONE : View.VISIBLE);
        });

        searchMessagesVM.getLoading().observe(this, isLoading -> {
            loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            loadMore.setVisibility(messagesStore.hasMore() ? View.VISIBLE : View.GONE);
        });

        searchMessagesVM.getError().observe(this, errorStr -> {
            error.setVisibility(errorStr == null || errorStr.isEmpty() ? View.GONE : View.VISIBLE);
        });

        searchMessagesVM.getMessages().observe(this, msg -> {
            if(msg.size() == 0)
                loadMore.setVisibility(View.GONE);

            adapter.setData(msg);
        });
        loadMore.setOnClickListener((view) -> activity.startMessagesLoading());
    }
}
