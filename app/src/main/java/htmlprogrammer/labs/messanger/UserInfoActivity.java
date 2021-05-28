package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.api.ChatAPI;
import htmlprogrammer.labs.messanger.api.SearchAPI;
import htmlprogrammer.labs.messanger.fragments.UserAvatar;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import htmlprogrammer.labs.messanger.viewmodels.UserInfoViewModel;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {
    private ImageView back;
    private TextView ban;
    private TextView clear;
    private TextView name;
    private TextView lastSeen;
    private TextView phone;
    private TextView nick;
    private TextView description;
    private FrameLayout message;

    private ChatStore chatStore = ChatStore.getInstance();
    private ChatMessagesStore chatMessagesStore = ChatMessagesStore.getInstance();
    private UserInfoViewModel userInfoVM;
    private UserAvatar userAvatar;

    private boolean isBanLoading = false;
    private boolean isClearLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setSupportActionBar(findViewById(R.id.toolbar));

        //get data
        userInfoVM = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        userAvatar = UserAvatar.getInstance("", "");
        String userNick = getIntent().getStringExtra("userNick");
        String userDialogID = getIntent().getStringExtra("dialogID");

        userInfoVM.setDialogID(userDialogID);

        //find elements
        back = findViewById(R.id.back);
        ban = findViewById(R.id.ban);
        clear = findViewById(R.id.clear);
        name = findViewById(R.id.name);
        lastSeen = findViewById(R.id.lastSeen);
        phone = findViewById(R.id.phone);
        nick = findViewById(R.id.nick);
        description = findViewById(R.id.description);
        message = findViewById(R.id.message);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.avatar, userAvatar, null).commit();

        addHandlers();
        startUserLoading(userNick);
    }

    private void addHandlers(){
        back.setOnClickListener(v -> finish());
        message.setOnClickListener(v -> onMessage());
        ban.setOnClickListener(v -> onBan());
        clear.setOnClickListener(v -> onClear());

        userInfoVM.getDialogID().observe(this, dialogID -> {
            clear.setVisibility(dialogID == null ? View.GONE : View.VISIBLE);
        });

        userInfoVM.getUser().observe(this, user -> {
            if(user == null)
                return;

            String descriptionStr = user.getDescription();
            String lastSeenStr = user.isOnline() ? getString(R.string.online) : getString(R.string.lastSeen, user.getDateTimeString());

            if(descriptionStr == null || descriptionStr.isEmpty())
                descriptionStr = getString(R.string.notSet);

            //update ui
            userAvatar.initUI(user.getFullName(), user.getAvatar());
            name.setText(user.getFullName());
            phone.setText(user.getPhone());
            nick.setText(user.getNick());
            lastSeen.setText(lastSeenStr);
            description.setText(descriptionStr);
            ban.setText(user.isBanned() ? R.string.unban : R.string.ban);
        });
    }

    private void onMessage(){
        User user = userInfoVM.getUser().getValue();

        if(user != null){
            //show user chat
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("nick", user.getNick());
            startActivity(intent);
        }
        else
            finish();
    }

    private void onBan(){
        User user = userInfoVM.getUser().getValue();

        if(user != null && !isBanLoading){
            ban.setTextColor(getResources().getColor(R.color.textGray));
            isBanLoading = true;

            String id = userInfoVM.getUser().getValue().getId();
            ChatAPI.ban(MeStore.getInstance().getToken(), id, this::onBanResult);
        }
    }

    private void onBanResult(Exception e, Response response){
        //stop loading
        runOnUiThread(() -> ban.setTextColor(getResources().getColor(R.color.textBlue)));
        isBanLoading = false;

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
                //show message
                runOnUiThread(() -> {
                    Toast.makeText(this, "Toggled", Toast.LENGTH_SHORT).show();

                    ban.setTextColor(getResources().getColor(R.color.textBlue));
                    isBanLoading = false;

                    //update user
                    User curUser = userInfoVM.getUser().getValue();

                    if(curUser == null)
                        return;

                    curUser.setBanned(!curUser.isBanned());
                    userInfoVM.setUser(curUser);
                });
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private void onClear(){
        User user = userInfoVM.getUser().getValue();

        if(user != null && !isClearLoading){
            clear.setTextColor(getResources().getColor(R.color.textGray));
            isClearLoading = true;

            String id = userInfoVM.getDialogID().getValue();
            ChatAPI.clear(MeStore.getInstance().getToken(), id, this::onClearResult);
        }
    }

    private void onClearResult(Exception e, Response response){
        //stop loading
        runOnUiThread(() -> clear.setTextColor(getResources().getColor(R.color.textBlue)));
        isClearLoading = false;

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
                //show message
                runOnUiThread(() -> Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show());

                //clear store
                User chatUser = chatStore.getUser();

                if(chatUser != null && chatUser.getId().equals(userInfoVM.getUser().getValue().getId()))
                    chatMessagesStore.reset();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private void startUserLoading(String userNick){
        SearchAPI.getUserByNickname(MeStore.getInstance().getToken(), userNick, this::onUserLoaded);
    }

    private void onUserLoaded(Exception err, Response response){
        if(err != null || !response.isSuccessful()){
            runOnUiThread(() -> {
                //get error
                String errorText = err != null ? err.getMessage() : "";
                errorText = err == null && !response.isSuccessful() ? response.message() : errorText;
                Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
            });
        }
        else{
            try {
                //parse new dialogs
                JSONObject object = new JSONObject(response.body().string());
                User newUser = User.fromJSON(object.getJSONObject("user"));

                //add it to state
                runOnUiThread(() -> userInfoVM.setUser(newUser));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
