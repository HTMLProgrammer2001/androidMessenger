package htmlprogrammer.labs.messanger.fragments.chatFragments;


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

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.ChatAdapter;
import htmlprogrammer.labs.messanger.fragments.chatFragments.actions.TextActionFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragment extends Fragment {
    private TextView loading;

    public DialogFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loading = view.findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
    }
}
