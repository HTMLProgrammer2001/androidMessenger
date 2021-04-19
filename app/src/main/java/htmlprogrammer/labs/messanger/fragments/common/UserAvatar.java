package htmlprogrammer.labs.messanger.fragments.common;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.dialogs.ImageDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAvatar extends Fragment {
    private CircleImageView imgView;
    private TextView shortNameView;
    private ConstraintLayout root;

    private String imgURL = "";
    private String name = "";

    public UserAvatar() { }

    public static UserAvatar getInstance(String shortName, String url){
        UserAvatar avatarFragment = new UserAvatar();
        Bundle args = new Bundle();

        args.putString("name", shortName);
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

        //find elements
        imgView = view.findViewById(R.id.img);
        shortNameView = view.findViewById(R.id.shortName);
        root = view.findViewById(R.id.root);

        //add handlers
        imgView.setOnClickListener(this::imgListener);

        //get data from bundle
        Bundle args = getArguments();

        if(this.name == null || this.name.equals(""))
            this.name = args.getString("name");

        if(this.imgURL == null || this.imgURL.equals(""))
            this.imgURL = args.getString("imgURL");

        initUI(name, imgURL);
    }

    private void imgListener(View view){
        if(imgURL != null && !imgURL.equals("")){
            ImageDialog dialog = ImageDialog.getInstance(this.imgURL);
            dialog.show(requireFragmentManager(), "imgView");
        }
    }

    public void initUI(String name, String imgURL){
        boolean hasImg = imgURL != null && !imgURL.equals("");
        boolean hasName = name != null && !name.equals("");

        //set data
        this.name = name;
        this.imgURL = imgURL;

        if(root == null)
            return;

        if(hasImg && imgView != null){
            //show image
            Picasso.get().load(imgURL).into(imgView);

            root.setBackground(null);
            shortNameView.setVisibility(View.INVISIBLE);
            imgView.setVisibility(View.VISIBLE);
        }
        else if(hasName){
            //parse name
            String[] words = name.split(" ");
            String shortName = "";

            for(int i = 0; i < words.length; i++) {
                shortName += words[i].substring(0, 1);
            }

            //show name
            root.setBackground(getResources().getDrawable(R.drawable.bg_round));
            shortNameView.setText(shortName);

            shortNameView.setVisibility(View.VISIBLE);
            imgView.setVisibility(View.INVISIBLE);
        }
    }
}
