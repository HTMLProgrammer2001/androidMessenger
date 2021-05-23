package htmlprogrammer.labs.messanger.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.FriendListAdapter;
import htmlprogrammer.labs.messanger.api.FriendAPI;
import htmlprogrammer.labs.messanger.interfaces.IFriendListCallback;
import htmlprogrammer.labs.messanger.interfaces.IFriendListContext;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import okhttp3.Response;

public class FriendsList extends DialogFragment implements IFriendListContext {
    private View view;
    private RecyclerView list;
    private TextView loaderView;
    private EditText nameView;
    private FriendListAdapter adapter;

    private TreeSet<User> users;
    private ArrayList<String> selected = new ArrayList<>();
    private boolean isLoading;
    private int pageSize = 10;
    private int page = 0;
    private int totalPages = 1;
    private String text = "";
    private IFriendListCallback callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create view
        view = LayoutInflater.from(requireContext()).inflate(R.layout.friends_list, null, false);
        loaderView = view.findViewById(R.id.loading);
        nameView = view.findViewById(R.id.friendName);

        //init recycler view
        list = view.findViewById(R.id.list);
        adapter = new FriendListAdapter((AppCompatActivity) requireActivity(), this);

        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        list.setLayoutManager(manager);
        list.setAdapter(adapter);

        //start loading
        reset();
        startLoading();

        addHandlers();

        //create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.selectFriends);
        builder.setView(view);

        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.next), (v, which) -> callback.run(selected));

        return builder.create();
    }

    private void addHandlers(){
        nameView.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEND){
                text = nameView.getText().toString();
                reset();
                startLoading();
                return true;
            }

            return false;
        });

        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int offset = recyclerView.computeVerticalScrollOffset();

                //load new messages
                if(!isLoading && offset  < 200
                        && page < totalPages)
                    startLoading();
            }
        });
    }

    private void startLoading() {
        if (page >= totalPages)
            return;

        setLoading(true);

        FriendAPI.getFriendsByName(
                MeStore.getInstance().getToken(),
                text, page, pageSize, this::onFriendLoaded
        );
    }

    private void onFriendLoaded(Exception err, Response response) {
        requireActivity().runOnUiThread(() -> {
            //stop loading
            setLoading(false);

            if (err != null || !response.isSuccessful()) {
                //get error
                String errorText = err != null ? err.getMessage() : "";
                errorText = err == null && !response.isSuccessful() ? response.message() : errorText;

                Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show();
            } else {
                try {
                    //parse new dialogs
                    JSONObject object = new JSONObject(response.body().string());
                    JSONArray array = object.getJSONArray("data");

                    ArrayList<User> newFriends = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        newFriends.add(User.fromJSON(array.getJSONObject(i)));
                    }

                    //add it to state
                    page = object.getInt("page");
                    totalPages = object.optInt("totalPages", 100);
                    addUsers(newFriends);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLoading(boolean newLoading) {
        this.isLoading = newLoading;
        loaderView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    public void setCallback(IFriendListCallback cb){
        this.callback = cb;
    }

    private void setUsers(TreeSet<User> users) {
        this.users = users;
        adapter.setUsers(users);
    }

    private void addUsers(ArrayList<User> newFriends) {
        users.addAll(newFriends);
        setUsers(users);
    }

    private void reset() {
        setLoading(false);
        setUsers(new TreeSet<>());
    }

    public void toggleSelect(String id){
        if(selected.contains(id))
            selected.remove(id);
        else
            selected.add(id);

        adapter.setSelected(selected);
    }
}
