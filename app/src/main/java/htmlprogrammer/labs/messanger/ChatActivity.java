package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.api.SearchAPI;
import htmlprogrammer.labs.messanger.constants.ChatTypes;
import htmlprogrammer.labs.messanger.constants.DialogTypes;
import htmlprogrammer.labs.messanger.fragments.UserAvatar;
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

    private TextView name;
    private TextView info;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatVM = ViewModelProviders.of(this).get(ChatViewModel.class);

        //set toolbar
        setSupportActionBar(findViewById(R.id.toolbar));

        //init avatars
        avatar = UserAvatar.getInstance("Test", "");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.avatar, avatar, null).commit();

        //find views
        name = findViewById(R.id.name);
        info = findViewById(R.id.info);
        back = findViewById(R.id.back);

        //start loading
        String nick = getIntent().getStringExtra("nick");
        ChatTypes type = (ChatTypes) getIntent().getSerializableExtra("type");

        if(type.equals(ChatTypes.USER))
            startUserLoading(nick);
        else
            startDialogLoading(nick);

        addHandlers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void addHandlers(){
        back.setOnClickListener(v -> finish());

        chatVM.getDialogData().observe(this, dialog -> {
            if(dialog == null)
                return;

            String info = getString(R.string.participants, dialog.getPartCount());
            if(dialog.getType().equals(DialogTypes.PERSONAL))
                info = "Online";

            initUI(dialog.getName(), dialog.getAvatar(), info);
        });

        chatVM.getUserData().observe(this, user -> {
            if(user == null)
                return;

            initUI(user.getFullName(), user.getAvatar(), "Online");
        });
    }

    private void initUI(String name, String avatar, String info){
        this.name.setText(name);
        this.avatar.initUI(name, avatar);
        this.info.setText(info);
    }

    private void startUserLoading(String userNick){
        chatStore.startLoading();

        SearchAPI.getUserByNickname(
                MeStore.getInstance().getToken(),
                userNick,
                this::onUserLoaded
        );
    }

    private void onUserLoaded(Exception e, Response response){
        //stop loading
        chatStore.stopLoading();

        if(e != null || !response.isSuccessful()){
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
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
    }

    private void startDialogLoading(String dialogNick){
        chatStore.startLoading();

        SearchAPI.getDialogByNickname(
                MeStore.getInstance().getToken(),
                dialogNick,
                this::onDialogLoaded
        );
    }

    private void onDialogLoaded(Exception e, Response response){
        //stop loading
        chatStore.stopLoading();

        if(e != null || !response.isSuccessful()){
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
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
}
