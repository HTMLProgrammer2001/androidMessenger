package htmlprogrammer.labs.messanger.dialogs;


import android.app.AlertDialog;
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
import htmlprogrammer.labs.messanger.adapters.ResendDialogListAdapter;
import htmlprogrammer.labs.messanger.api.SearchAPI;
import htmlprogrammer.labs.messanger.interfaces.IFriendListCallback;
import htmlprogrammer.labs.messanger.interfaces.IFriendListContext;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.MeStore;
import okhttp3.Response;

public class ResendDialogList extends DialogFragment implements IFriendListContext {
    private View view;
    private RecyclerView list;
    private TextView loaderView;
    private EditText nameView;
    private ResendDialogListAdapter adapter;

    private TreeSet<Dialog> dialogs;
    private ArrayList<String> selected = new ArrayList<>();
    private boolean isLoading;
    private int pageSize = 10;
    private int page = 0;
    private int totalPages = 1;
    private String text = "";
    private IFriendListCallback callback;

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create view
        view = LayoutInflater.from(requireContext()).inflate(R.layout.friends_list, null, false);
        loaderView = view.findViewById(R.id.loading);
        nameView = view.findViewById(R.id.friendName);

        //init recycler view
        list = view.findViewById(R.id.list);
        adapter = new ResendDialogListAdapter((AppCompatActivity) requireActivity(), this);

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

    private void addHandlers() {
        nameView.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
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
                if (!isLoading && offset < 200
                        && page < totalPages)
                    startLoading();
            }
        });
    }

    private void startLoading() {
        if (page >= totalPages)
            return;

        setLoading(true);

        if (text != null && !text.equals("") && text.startsWith("@"))
            SearchAPI.getDialogsByNick(
                    MeStore.getInstance().getToken(),
                    text.substring(1), page + 1, pageSize, this::onDialogLoaded
            );
        else
            SearchAPI.getDialogsByName(
                    MeStore.getInstance().getToken(),
                    text, page + 1, pageSize, this::onDialogLoaded
            );
    }

    private void onDialogLoaded(Exception err, Response response) {
        requireActivity().runOnUiThread(() -> {
            //stop loading
            setLoading(false);
        });

        if (err != null || !response.isSuccessful()) {
            requireActivity().runOnUiThread(() -> {
                //get error
                String errorText = err != null ? err.getMessage() : "";
                errorText = err == null && !response.isSuccessful() ? response.message() : errorText;

                Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show();
            });
        } else {
            try {
                //parse new dialogs
                JSONObject object = new JSONObject(response.body().string());
                JSONArray array = object.getJSONArray("data");

                ArrayList<Dialog> newDialogs = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    newDialogs.add(Dialog.fromJSON(array.getJSONObject(i)));
                }

                //add it to state
                page = object.getInt("page");
                totalPages = object.getInt("totalPages");
                addDialogs(newDialogs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setLoading(boolean newLoading) {
        this.isLoading = newLoading;
        loaderView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    public void setCallback(IFriendListCallback cb) {
        this.callback = cb;
    }

    private void setDialogs(TreeSet<Dialog> dialogs) {
        this.dialogs = dialogs;
        adapter.setDialogs(dialogs);
    }

    private void addDialogs(ArrayList<Dialog> newDialogs) {
        dialogs.addAll(newDialogs);
        setDialogs(dialogs);
    }

    private void reset() {
        setLoading(false);
        setDialogs(new TreeSet<>());
        page = 0;
        totalPages = 1;
    }

    public void toggleSelect(String id) {
        if (selected.contains(id))
            selected.remove(id);
        else
            selected.add(id);

        adapter.setSelected(selected);
    }
}
