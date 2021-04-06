package htmlprogrammer.labs.messanger.fragments;


import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.constants.ActionBarType;
import htmlprogrammer.labs.messanger.dialogs.PhoneChangeDialog;
import htmlprogrammer.labs.messanger.fragments.common.UserAvatar;
import htmlprogrammer.labs.messanger.interfaces.ActionBarChanged;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeState;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private TextView name;
    private TextView phone;
    private TextView nick;
    private TextView description;

    private TextView logout;
    private LinearLayout uploader;

    private MeState meState;
    private UserAvatar avatarFragment;
    private ActionBarChanged actionBarChanged;

    private final static int PHOTO_CODE = 1000;

    public SettingsFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionBarChanged = (ActionBarChanged) context;
        actionBarChanged.setActionBarType(ActionBarType.BACK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get state
        meState = ViewModelProviders.of(requireActivity()).get(MeState.class);
        avatarFragment = UserAvatar.getInstance("", "");

        //find elements
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        nick = view.findViewById(R.id.nick);
        description = view.findViewById(R.id.description);

        logout = view.findViewById(R.id.logout);
        uploader = view.findViewById(R.id.photoUploader);

        //show avatar
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.avatar, avatarFragment, null)
                .commit();

        addHandlers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        actionBarChanged.setActionBarType(ActionBarType.TOOLBAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PHOTO_CODE && resultCode == Activity.RESULT_OK){
            if(data == null){
                Toast.makeText(requireActivity(), getString(R.string.errorOccured), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addHandlers(){
        meState.getUser().observe(requireActivity(), (User user) -> {
            if(user == null)
                return;

            //show user data
            avatarFragment.initUI(user.getFullName(), user.getAvatar());
            name.setText(user.getFullName());
            nick.setText(user.getNick());
            phone.setText(user.getPhone());
        });

        logout.setOnClickListener(this::logout);
        uploader.setOnClickListener(this::uploadPhoto);
        phone.setOnClickListener(this::changePhone);
    }

    private void logout(View view){
        //logout
        meState.setUser(null);
        meState.setToken(null);

        //delete from shared preference
        SharedPreferences.Editor editor = requireActivity()
                .getSharedPreferences("store", 0).edit();

        editor.remove("token");
        editor.apply();
    }

    private void uploadPhoto(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_CODE);
    }

    private void changePhone(View view){
        PhoneChangeDialog dialog = new PhoneChangeDialog();
        dialog.show(requireFragmentManager(), "changePhoneDialog");
    }
}
