package htmlprogrammer.labs.messanger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import htmlprogrammer.labs.messanger.api.ChatAPI;
import htmlprogrammer.labs.messanger.store.MeStore;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {
    private ImageView back;
    private TextView id;
    private TextView ban;
    private TextView clear;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setSupportActionBar(findViewById(R.id.toolbar));

        //get id
        userID = getIntent().getStringExtra("userID");

        //find elements
        back = findViewById(R.id.back);
        id = findViewById(R.id.id);
        ban = findViewById(R.id.ban);
        clear = findViewById(R.id.clear);

        id.setText(userID);

        addHandlers();
    }

    private void addHandlers(){
        back.setOnClickListener(v -> finish());
        ban.setOnClickListener(v -> ChatAPI.ban(MeStore.getInstance().getToken(), userID, this::onBanResult));
        clear.setOnClickListener(v -> ChatAPI.clear(MeStore.getInstance().getToken(), userID, this::onClearResult));
    }

    private void onBanResult(Exception e, Response response){
        runOnUiThread(() -> Toast.makeText(this, "Ban", Toast.LENGTH_SHORT).show());
    }

    private void onClearResult(Exception e, Response response){
        runOnUiThread(() -> Toast.makeText(this, "Clear", Toast.LENGTH_SHORT).show());
    }
}
