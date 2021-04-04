package htmlprogrammer.labs.messanger.fragments.common;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import htmlprogrammer.labs.messanger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAvatar extends Fragment {
    private ImageView imgView;
    private TextView shortName;


    public UserAvatar() { }

    public static UserAvatar getInstance(String shortName, String url){
        UserAvatar avatarFragment = new UserAvatar();
        Bundle args = new Bundle();

        args.putString("shortName", shortName);
        args.putString("imgURL", url);

        avatarFragment.setArguments(args);
        return avatarFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_avatar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgView = view.findViewById(R.id.img);
        shortName = view.findViewById(R.id.shortName);

        initUI();
    }

    private void initUI(){
        String imgURL = getArguments().getString("imgURL");

        if(imgURL == null || imgURL.equals("")){
            String name = getArguments().getString("shortName");
            shortName.setText(name);

            shortName.setVisibility(View.VISIBLE);
            imgView.setVisibility(View.INVISIBLE);

            System.err.println("Name: " + name);
        }
        else{
            Picasso.get().load(imgURL).into(imgView);

            shortName.setVisibility(View.INVISIBLE);
            imgView.setVisibility(View.VISIBLE);
        }
    }
}
