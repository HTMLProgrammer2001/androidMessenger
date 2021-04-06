package htmlprogrammer.labs.messanger.fragments;


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

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.DialogAdapter;
import htmlprogrammer.labs.messanger.constants.ActionBarType;
import htmlprogrammer.labs.messanger.interfaces.ActionBarChanged;
import htmlprogrammer.labs.messanger.models.Dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private RecyclerView list;

    public MainFragment() { }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ActionBarChanged actionBarChanged = (ActionBarChanged) context;
        actionBarChanged.setActionBarType(ActionBarType.TOOLBAR);
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

        ArrayList<Dialog> arr = new ArrayList<>();
        Dialog dialog = new Dialog();
        dialog.setName("Test");
        dialog.setNick("Nick");
        arr.add(dialog);

        DialogAdapter adapter = new DialogAdapter(getContext(), arr);
        list.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
    }
}
