package htmlprogrammer.labs.messanger.fragments;


import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.EditMeAPI;
import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.constants.ActionBarType;
import htmlprogrammer.labs.messanger.dialogs.DescriptionChangeDialog;
import htmlprogrammer.labs.messanger.dialogs.LanguageDialog;
import htmlprogrammer.labs.messanger.dialogs.NameChangeDialog;
import htmlprogrammer.labs.messanger.dialogs.NickChangeDialog;
import htmlprogrammer.labs.messanger.fragments.common.UserAvatar;
import htmlprogrammer.labs.messanger.interfaces.ActionBarChanged;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.viewmodels.MeViewModel;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private TextView name;
    private TextView phone;
    private TextView nick;
    private TextView description;
    private TextView language;

    private LinearLayout phoneLayout;
    private LinearLayout nickLayout;
    private LinearLayout descLayout;
    private LinearLayout deleteAvatar;

    private TextView logout;
    private LinearLayout uploader;
    private Switch notificationSwitch;

    private MeViewModel meVM;
    private MeStore meStore = MeStore.getInstance();
    private UserAvatar avatarFragment;
    private ActionBarChanged actionBarChanged;

    private SharedPreferences pref;

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
        meVM = ViewModelProviders.of(requireActivity()).get(MeViewModel.class);

        User user = meVM.getUser().getValue();
        avatarFragment = UserAvatar.getInstance(user.getFullName(), user.getAvatar());

        //find elements
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        nick = view.findViewById(R.id.nick);
        description = view.findViewById(R.id.description);

        phoneLayout = view.findViewById(R.id.phoneLayout);
        nickLayout = view.findViewById(R.id.nickLayout);
        descLayout = view.findViewById(R.id.descLayout);
        deleteAvatar = view.findViewById(R.id.deleteAvatar);

        language = view.findViewById(R.id.language);
        logout = view.findViewById(R.id.logout);
        uploader = view.findViewById(R.id.photoUploader);
        notificationSwitch = view.findViewById(R.id.notificationSwitch);

        pref = requireActivity().getSharedPreferences("store", 0);
        notificationSwitch.setChecked(pref.getBoolean("notification", true));

        //show avatar
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileAvatar, avatarFragment, null)
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

            try {
                //get file
                Uri uri = data.getData();
                String filePath = getRealPathFromUri(requireActivity(), uri);
                File file = new File(filePath);

                //save on server
                EditMeAPI.editAvatar(meStore.getToken(), file, this::onPhotoChanged);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static String getRealPathFromUri(Activity activity, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void addHandlers(){
        meVM.getUser().observe(requireActivity(), (User user) -> {
            if(user == null)
                return;

            //toggle delete button
            if(user.getAvatar() == null){
                deleteAvatar.setVisibility(View.GONE);
            }
            else{
                deleteAvatar.setVisibility(View.VISIBLE);
            }

            //show user data
            avatarFragment.initUI(user.getFullName(), user.getAvatar());
            name.setText(user.getFullName());
            nick.setText(user.getNick());
            phone.setText(user.getPhone());
            description.setText(user.getDescription() == null ? getString(R.string.notSet) : user.getDescription());
        });

        //add handlers
        logout.setOnClickListener(this::logout);
        uploader.setOnClickListener(this::uploadPhoto);
        nickLayout.setOnClickListener(this::changeNick);
        phoneLayout.setOnClickListener(this::changePhone);
        descLayout.setOnClickListener(this::changeDescription);
        name.setOnClickListener(this::changeName);
        deleteAvatar.setOnClickListener(this::deleteAvatar);
        notificationSwitch.setOnCheckedChangeListener(this::notificationChanged);
        language.setOnClickListener(this::changeLanguage);
    }

    private void notificationChanged(View view, boolean isChecked){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("notification", isChecked);
        editor.apply();
    }

    private void changeLanguage(View view){
        //show dialog
        LanguageDialog dialog = new LanguageDialog();
        dialog.show(requireFragmentManager(), "changeLanguage");
    }

    private void changeNick(View view){
        //show dialog
        NickChangeDialog dialog = new NickChangeDialog();
        dialog.show(requireFragmentManager(), "changeNickDialog");
    }

    private void changeName(View view){
        //show dialog
        NameChangeDialog dialog = new NameChangeDialog();
        dialog.show(requireFragmentManager(), "changeNameDialog");
    }

    private void changePhone(View view){
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new ChangePhoneFragment(), null)
                .addToBackStack("changePhone")
                .commit();
    }

    private void changeDescription(View view){
        //show dialog
        DescriptionChangeDialog dialog = new DescriptionChangeDialog();
        dialog.show(requireFragmentManager(), "changeDescriptionDialog");
    }

    private void deleteAvatar(View view){
        EditMeAPI.deleteAvatar(pref.getString("token", ""), this::onPhotoChanged);
    }

    private void logout(View view){
        //make api call
        UserActionsAPI.logout(pref.getString("token", ""), (e, response) -> {});

        //logout
        meStore.setUser(null);
        meStore.setToken(null);

        //delete from shared preference
        SharedPreferences.Editor editor = pref.edit();

        editor.remove("token");
        editor.apply();
    }

    private void uploadPhoto(View view){
        //open image
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_CODE);
    }

    private void onPhotoChanged(Exception e, Response response){
        try{
            if (e == null && response.isSuccessful()) {
                //parse response
                JSONObject respObj = new JSONObject(response.body().string());

                //change me
                meStore.setUser(User.fromJSON(respObj.getJSONObject("newUser")));
            } else {
                //show error
                requireActivity().runOnUiThread(() -> {
                    //get error
                    String errorText = e != null ? e.getMessage() : "";
                    errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

                    Toast.makeText(getContext(), errorText, Toast.LENGTH_SHORT).show();
                });
            }
        }
        catch (Exception err){
            //show error
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), err.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
