package htmlprogrammer.labs.messanger;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

import htmlprogrammer.labs.messanger.api.EditMeAPI;
import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.dialogs.DescriptionChangeDialog;
import htmlprogrammer.labs.messanger.dialogs.LanguageDialog;
import htmlprogrammer.labs.messanger.dialogs.NameChangeDialog;
import htmlprogrammer.labs.messanger.dialogs.NickChangeDialog;
import htmlprogrammer.labs.messanger.fragments.UserAvatar;
import htmlprogrammer.labs.messanger.interfaces.BaseActivity;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.viewmodels.MeViewModel;
import okhttp3.Response;

public class SettingsActivity extends BaseActivity {
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
    private ActionBar bar;

    private final static int PHOTO_CODE = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //get state
        meVM = ViewModelProviders.of(this).get(MeViewModel.class);

        User user = meStore.getUser();
        avatarFragment = UserAvatar.getInstance(user.getFullName(), user.getAvatar());

        //find elements
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        nick = findViewById(R.id.nick);
        description = findViewById(R.id.description);

        phoneLayout = findViewById(R.id.phoneLayout);
        nickLayout = findViewById(R.id.nickLayout);
        descLayout = findViewById(R.id.descLayout);
        deleteAvatar = findViewById(R.id.deleteAvatar);

        language = findViewById(R.id.language);
        logout = findViewById(R.id.logout);
        uploader = findViewById(R.id.photoUploader);
        notificationSwitch = findViewById(R.id.notificationSwitch);

        notificationSwitch.setChecked(preferences.getBoolean("notification", true));

        //show action bar
        bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        //show avatar
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileAvatar, avatarFragment, null)
                .commit();

        addHandlers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PHOTO_CODE && resultCode == Activity.RESULT_OK){
            if(data == null){
                Toast.makeText(this, getString(R.string.errorOccured), Toast.LENGTH_SHORT).show();
            }

            try {
                //get file
                Uri uri = data.getData();
                String filePath = getRealPathFromUri(this, uri);
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
        meVM.getUser().observe(this, (User user) -> {
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
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notification", isChecked);
        editor.apply();
    }

    private void changeLanguage(View view){
        //show dialog
        LanguageDialog dialog = LanguageDialog.getInstance(preferences);
        dialog.show(getSupportFragmentManager(), "changeLanguage");
    }

    private void changeNick(View view){
        //show dialog
        NickChangeDialog dialog = new NickChangeDialog();
        dialog.show(getSupportFragmentManager(), "changeNickDialog");
    }

    private void changeName(View view){
        //show dialog
        NameChangeDialog dialog = new NameChangeDialog();
        dialog.show(getSupportFragmentManager(), "changeNameDialog");
    }

    private void changePhone(View view){
        Intent changePhoneIntent = new Intent(this, ChangePhoneActivity.class);
        startActivity(changePhoneIntent);
    }

    private void changeDescription(View view){
        //show dialog
        DescriptionChangeDialog dialog = new DescriptionChangeDialog();
        dialog.show(getSupportFragmentManager(), "changeDescriptionDialog");
    }

    private void deleteAvatar(View view){
        EditMeAPI.deleteAvatar(preferences.getString("token", ""), this::onPhotoChanged);
    }

    private void logout(View view){
        //make api call
        UserActionsAPI.logout(preferences.getString("token", ""), (e, response) -> {});

        //logout
        meStore.setUser(null);
        meStore.setToken(null);

        //delete from shared preference
        SharedPreferences.Editor editor = preferences.edit();

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
                runOnUiThread(() -> {
                    //get error
                    String errorText = e != null ? e.getMessage() : "";
                    errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

                    Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
                });
            }
        }
        catch (Exception err){
            //show error
            runOnUiThread(() -> Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
