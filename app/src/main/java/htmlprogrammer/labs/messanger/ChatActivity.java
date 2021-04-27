package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.api.SearchAPI;
import htmlprogrammer.labs.messanger.constants.DialogTypes;
import htmlprogrammer.labs.messanger.fragments.Loader;
import htmlprogrammer.labs.messanger.fragments.UserAvatar;
import htmlprogrammer.labs.messanger.fragments.chatFragments.BannedFragment;
import htmlprogrammer.labs.messanger.fragments.chatFragments.DialogFragment;
import htmlprogrammer.labs.messanger.fragments.chatFragments.NoDialogFragment;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import htmlprogrammer.labs.messanger.viewmodels.chat.ChatViewModel;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private UserAvatar avatar;
    private ChatViewModel chatVM;
    private ChatStore chatStore = ChatStore.getInstance();
    private String nick;

    private TextView name;
    private TextView info;
    private ImageView back;
    private ConstraintLayout data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatVM = ViewModelProviders.of(this).get(ChatViewModel.class);

        //set toolbar
        setSupportActionBar(findViewById(R.id.toolbar));

        //init avatars
        avatar = UserAvatar.getInstance("", "");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.avatar, avatar, null).commit();

        //find views
        name = findViewById(R.id.name);
        info = findViewById(R.id.info);
        back = findViewById(R.id.back);
        data = findViewById(R.id.data);

        //start loading
        nick = getIntent().getStringExtra("nick");
        startUserLoading();

        addHandlers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void addHandlers(){
        data.setOnClickListener(v -> {
            if(chatStore.getUser() != null) {
                //show user info
                Intent userIntent = new Intent(this, UserInfoActivity.class);
                userIntent.putExtra("userNick", chatStore.getUser().getNick());

                startActivity(userIntent);
            }
            else if(chatStore.getDialog() != null){
                //show dialog info
                Intent groupIntent = new Intent(this, GroupInfoActivity.class);
                groupIntent.putExtra("groupNick", chatStore.getDialog().getNick());

                startActivity(groupIntent);
            }
        });

        back.setOnClickListener(v -> finish());

        chatVM.getDialogData().observe(this, dialog -> {
            if(dialog == null) {
                replaceFragment(new NoDialogFragment());
                return;
            }

            if(dialog.getType().equals(DialogTypes.CHAT))
                initUI(dialog.getName(), dialog.getAvatar(), getString(R.string.participants, dialog.getPartCount()));

            if(!dialog.isActive())
                replaceFragment(new BannedFragment());
            else
                replaceFragment(new DialogFragment());
        });

        chatVM.getUserData().observe(this, user -> {
            if(user == null)
                return;

            //update ui
            String info = user.isOnline() ? getString(R.string.online) : getString(R.string.lastSeen, user.getDateString());
            initUI(user.getFullName(), user.getAvatar(), info);
        });

        chatVM.getLoadingData().observe(this, isLoading -> {
            //show loader
            if(isLoading)
                replaceFragment(new Loader());
        });
    }

    private void initUI(String name, String avatar, String info){
        this.name.setText(name);
        this.avatar.initUI(name, avatar);
        this.info.setText(info);
    }

    private void startUserLoading(){
        chatStore.reset();
        chatStore.startLoading();
        SearchAPI.getUserByNickname(MeStore.getInstance().getToken(), nick, this::onUserLoaded);
    }

    private void onUserLoaded(Exception e, Response response){
        //stop loading
        chatStore.stopLoading();

        if(e != null || !response.isSuccessful()){
            runOnUiThread(() -> {
                //get error
                String errorText = e != null ? e.getMessage() : "";
                errorText = e == null && !response.isSuccessful() ? response.message() : errorText;
                Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
            });
        }
        else{
            try {
                //parse new dialogs
                JSONObject object = new JSONObject(response.body().string());
                User newUser = User.fromJSON(object.getJSONObject("user"));

                //add it to state
                chatStore.setUser(newUser);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        startDialogLoading();
    }

    private void startDialogLoading(){
        chatStore.startLoading();
        SearchAPI.getDialogByNickname(MeStore.getInstance().getToken(), nick, this::onDialogLoaded);
    }

    private void onDialogLoaded(Exception e, Response response){
        //stop loading
        chatStore.stopLoading();

        if(e != null || !response.isSuccessful()){
            chatStore.setDialog(null);

            runOnUiThread(() -> {
                //get error
                String errorText = e != null ? e.getMessage() : "";
                errorText = e == null && !response.isSuccessful() ? response.message() : errorText;
                Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
            });
        }
        else{
            try {
                //parse new dialogs
                JSONObject object = new JSONObject(response.body().string());
                Dialog newDialog = Dialog.fromJSON(object.getJSONObject("dialog"));

                //add it to state
                chatStore.setDialog(newDialog);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment, null)
                .commit();
    }
}
