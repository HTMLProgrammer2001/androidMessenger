package htmlprogrammer.labs.messanger.fragments.common;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import htmlprogrammer.labs.messanger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Loader extends Fragment {


    public Loader() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loader, container, false);
    }

}