package htmlprogrammer.labs.messanger.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.DialogAdapter;
import htmlprogrammer.labs.messanger.api.SearchAPI;
import htmlprogrammer.labs.messanger.constants.ActionBarType;
import htmlprogrammer.labs.messanger.interfaces.ActionBarChanged;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.SearchState;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private RecyclerView list;

    private DialogAdapter adapter;
    private SearchState searchState;

    public MainFragment() { }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ActionBarChanged actionBarChanged = (ActionBarChanged) context;
        actionBarChanged.setActionBarType(ActionBarType.TOOLBAR);

        searchState = ViewModelProviders.of(requireActivity()).get(SearchState.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = view.findViewById(R.id.list);

        initList();
        addHandlers();
        startLoading();
    }

    private void addHandlers(){
        searchState.getDialogs().observe(requireActivity(), (dialogs) -> adapter.setData(dialogs));
        searchState.getLoading().observe(requireActivity(), (isLoading) -> adapter.setLoading(isLoading));
    }

    private void initList(){
        adapter = new DialogAdapter(getContext());
        list.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
    }

    private void startLoading(){
        searchState.startLoading();
        searchState.setDialogs(new ArrayList<>());

        //get token
        SharedPreferences pref = requireActivity().getSharedPreferences("store", 0);
        SearchAPI.getDialogsByName(pref.getString("token", ""), "", this::onLoaded);
    }

    private void onLoaded(Exception e, Response response){
        //stop loading
        searchState.setIsLoading(false);

        if(e != null || !response.isSuccessful()){
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            searchState.setError(errorText);
        }
        else{
            try {
                //parse new dialogs
                JSONObject object = new JSONObject(response.body().string());
                JSONArray array = object.getJSONArray("data");

                ArrayList<Dialog> newDialogs = new ArrayList<>();

                for(int i = 0; i < array.length(); i++){
                    newDialogs.add(Dialog.fromJSON(array.getJSONObject(i)));
                }

                //add it to state
                searchState.addDialogs(newDialogs);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
