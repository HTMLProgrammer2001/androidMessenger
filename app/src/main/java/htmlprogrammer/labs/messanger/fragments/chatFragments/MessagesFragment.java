package htmlprogrammer.labs.messanger.fragments.chatFragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.ChatAdapter;
import htmlprogrammer.labs.messanger.api.MessageAPI;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import htmlprogrammer.labs.messanger.viewmodels.chat.ChatMessagesViewModel;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {
    private RecyclerView list;
    private FloatingActionButton fab;
    private ChatAdapter adapter;

    private ChatMessagesViewModel chatMessagesVM;
    private ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();

    public MessagesFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        chatMessagesVM = ViewModelProviders.of(this).get(ChatMessagesViewModel.class);
        list = view.findViewById(R.id.list);
        fab = view.findViewById(R.id.fab);
        adapter = new ChatAdapter(getFragmentManager(), requireActivity());

        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(false);

        list.setLayoutManager(manager);
        list.setAdapter(adapter);

        chatMessagesStore.reset();
        addHandlers();
        startLoading();
    }

    private void addHandlers(){
        chatMessagesVM.getMessages().observe(this, msg -> adapter.setData(msg));

        chatMessagesVM.getError().observe(this, err -> {
            if(err != null)
                Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show();
        });

        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int offset = recyclerView.computeVerticalScrollOffset();

                //load new messages
                if(!chatMessagesStore.getLoading() && offset < 200
                        && chatMessagesStore.hasMore())
                    startLoading();

                //toggle fab
                boolean isVisible = recyclerView.computeVerticalScrollRange() - offset - recyclerView.computeVerticalScrollExtent() > 300;
                fab.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        });

        fab.setOnClickListener(v -> list.scrollToPosition(0));
    }

    private void startLoading(){
        chatMessagesStore.startLoading();

        MessageAPI.getMessages(
                MeStore.getInstance().getToken(),
                ChatStore.getInstance().getDialog().getId(),
                chatMessagesStore.getCurPage() + 1,
                chatMessagesStore.getPageSize(),
                this::onMessagesLoaded
        );
    }

    private void onMessagesLoaded(Exception err, Response response){
        //stop loading
        chatMessagesStore.stopLoading();

        if(err != null || !response.isSuccessful()){
            //get error
            String errorText = err != null ? err.getMessage() : "";
            errorText = err == null && !response.isSuccessful() ? response.message() : errorText;

            chatMessagesStore.setError(errorText);
        }
        else{
            try {
                //parse new dialogs
                JSONObject object = new JSONObject(response.body().string());
                JSONArray array = object.getJSONArray("data");

                ArrayList<Message> newMessages = new ArrayList<>();

                for(int i = 0; i < array.length(); i++){
                    newMessages.add(Message.fromJSON(array.getJSONObject(i)));
                }

                //add it to state
                chatMessagesStore.setCurPage(object.getInt("page"));
                chatMessagesStore.setTotalPages(object.getInt("totalPages"));
                chatMessagesStore.addMessages(newMessages);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
