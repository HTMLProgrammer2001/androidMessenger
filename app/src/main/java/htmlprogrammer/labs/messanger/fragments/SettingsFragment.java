package htmlprogrammer.labs.messanger.fragments;


import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.EditMeAPI;
import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.constants.ActionBarType;
import htmlprogrammer.labs.messanger.dialogs.NickChangeDialog;
import htmlprogrammer.labs.messanger.fragments.common.UserAvatar;
import htmlprogrammer.labs.messanger.interfaces.ActionBarChanged;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeState;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private TextView name;
    private TextView phone;
    private TextView nick;
    private TextView description;

    private LinearLayout phoneLayout;
    private LinearLayout nickLayout;
    private LinearLayout descLayout;

    private TextView logout;
    private LinearLayout uploader;

    private MeState meState;
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
        meState = ViewModelProviders.of(requireActivity()).get(MeState.class);
        avatarFragment = UserAvatar.getInstance("", "");

        //find elements
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        nick = view.findViewById(R.id.nick);
        description = view.findViewById(R.id.description);

        phoneLayout = view.findViewById(R.id.phoneLayout);
        nickLayout = view.findViewById(R.id.nickLayout);
        descLayout = view.findViewById(R.id.descLayout);

        logout = view.findViewById(R.id.logout);
        uploader = view.findViewById(R.id.photoUploader);

        pref = requireActivity().getSharedPreferences("store", 0);

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

            try {
                //get file
                Uri uri = data.getData();
                String filePath = getRealPathFromUri(requireActivity(), uri);
                File file = new File(filePath);

                //save on server
                EditMeAPI.editAvatar(pref.getString("token", ""), file, this::onPhotoChanged);
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
        meState.getUser().observe(requireActivity(), (User user) -> {
            if(user == null)
                return;

            //show user data
            avatarFragment.initUI(user.getFullName(), user.getAvatar());
            name.setText(user.getFullName());
            nick.setText(user.getNick());
            phone.setText(user.getPhone());
        });

        //add handlers
        logout.setOnClickListener(this::logout);
        uploader.setOnClickListener(this::uploadPhoto);
        nickLayout.setOnClickListener(this::changeNick);
        phoneLayout.setOnClickListener(this::changePhone);
        descLayout.setOnClickListener(this::changeDescription);
    }

    private void logout(View view){
        //make api call
        UserActionsAPI.logout(pref.getString("token", ""), (e, response) -> {});

        //logout
        meState.setUser(null);
        meState.setToken(null);

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

    private void changeNick(View view){
        //show nick
        NickChangeDialog dialog = new NickChangeDialog();
        dialog.show(requireFragmentManager(), "changeNickDialog");
    }

    private void changePhone(View view){
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new ChangePhoneFragment(), null)
                .addToBackStack("changePhone")
                .commit();
    }

    public void changeDescription(View view){

    }

    private void onPhotoChanged(Exception e, Response response){
        try{
            if (e == null && response.isSuccessful()) {
                //parse response
                JSONObject respObj = new JSONObject(response.body().string());

                //change me
                meState.setUser(User.fromJSON(respObj.getJSONObject("newUser")));
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
