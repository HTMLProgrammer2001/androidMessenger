package htmlprogrammer.labs.messanger.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.DialogAdapter;
import htmlprogrammer.labs.messanger.api.SearchAPI;
import htmlprogrammer.labs.messanger.constants.ActionBarType;
import htmlprogrammer.labs.messanger.helpers.SwipeDetector;
import htmlprogrammer.labs.messanger.interfaces.ActionBarChanged;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.SearchStore;
import htmlprogrammer.labs.messanger.viewmodels.SearchViewModel;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private RecyclerView list;

    private DialogAdapter adapter;
    private SearchViewModel searchVM;
    private SearchStore searchStore = SearchStore.getInstance();
    private GestureDetectorCompat detector;

    public MainFragment() { }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ActionBarChanged actionBarChanged = (ActionBarChanged) context;
        actionBarChanged.setActionBarType(ActionBarType.TOOLBAR);

        searchVM = ViewModelProviders.of(requireActivity()).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        view.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_MOVE)
                detector.onTouchEvent(event);
            else if(event.getAction() == MotionEvent.ACTION_DOWN)
                view.performClick();

            return true;
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = view.findViewById(R.id.list);

        initUI();
        addHandlers();
        startLoading();
    }

    private void initUI(){
        initList();
        initSwipe();
    }

    private void initSwipe(){
        detector = new GestureDetectorCompat(requireActivity(), new SwipeDetector() {
            @Override
            public boolean onSwipe(Direction direction) {
                System.out.println(direction);
                return false;
            }

            @Override
            public boolean validate(MotionEvent e1, MotionEvent e2, float velX, float velY) {
                return velY > 0;
            }
        });
    }

    private void addHandlers(){
        searchVM.getDialogs().observe(requireActivity(), (dialogs) -> adapter.setData(dialogs));
        searchVM.getLoading().observe(requireActivity(), (isLoading) -> adapter.setLoading(isLoading));
    }

    private void initList(){
        adapter = new DialogAdapter(getContext());
        list.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
    }

    private void startLoading(){
        searchStore.startLoading();
        searchStore.setDialogs(new ArrayList<>());

        //get token
        SharedPreferences pref = requireActivity().getSharedPreferences("store", 0);
        SearchAPI.getDialogsByName(pref.getString("token", ""), "", this::onLoaded);
    }

    private void onLoaded(Exception e, Response response){
        //stop loading
        searchStore.stopLoading();

        if(e != null || !response.isSuccessful()){
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            searchStore.setError(errorText);
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
                searchStore.addDialogs(newDialogs);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
