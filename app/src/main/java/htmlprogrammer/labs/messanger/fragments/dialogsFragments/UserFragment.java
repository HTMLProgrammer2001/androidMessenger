package htmlprogrammer.labs.messanger.fragments.dialogsFragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import htmlprogrammer.labs.messanger.ChatActivity;
import htmlprogrammer.labs.messanger.DialogsActivity;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.constants.ChatTypes;
import htmlprogrammer.labs.messanger.fragments.UserAvatar;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.viewmodels.search.SearchUserViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    private TextView error;
    private TextView loading;
    private ConstraintLayout root;
    private TextView userName;

    private UserAvatar userAvatar;
    private User user;

    private SearchUserViewModel searchUserVM;
    private DialogsActivity activity;

    public UserFragment() { }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DialogsActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userAvatar = UserAvatar.getInstance("", "");
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dialogAvatar, userAvatar, null)
                .commit();

        //find elements
        error = view.findViewById(R.id.error);
        loading = view.findViewById(R.id.loading);
        root = view.findViewById(R.id.root);
        userName = view.findViewById(R.id.name);

        //hide unnecessary
        view.findViewById(R.id.message).setVisibility(View.GONE);
        view.findViewById(R.id.time).setVisibility(View.GONE);
        view.findViewById(R.id.unread).setVisibility(View.GONE);

        searchUserVM = ViewModelProviders.of(this).get(SearchUserViewModel.class);

        addHandlers();
    }

    private void addHandlers(){
        searchUserVM.getLoading().observe(this, isLoading -> {
            loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        searchUserVM.getError().observe(this, errorStr -> {
            boolean isGone = errorStr == null || errorStr.isEmpty();
            error.setVisibility(isGone ? View.GONE : View.VISIBLE);
        });

        searchUserVM.getUser().observe(this, user -> {
            this.user = user;

            if(user == null){
                root.setVisibility(View.GONE);
            }
            else{
                root.setVisibility(View.VISIBLE);
                userName.setText(user.getFullName());
                userAvatar.initUI(user.getFullName(), user.getAvatar());
            }
        });

        userName.setOnClickListener(v -> activity.openDialog(user.getNick(), ChatTypes.USER));
        root.setOnClickListener(v -> activity.openDialog(user.getNick(), ChatTypes.USER));
    }
}
