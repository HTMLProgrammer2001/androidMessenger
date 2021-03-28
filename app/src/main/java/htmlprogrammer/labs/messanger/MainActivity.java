package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.fragments.LoginFragment;
import htmlprogrammer.labs.messanger.fragments.MainFragment;
import htmlprogrammer.labs.messanger.fragments.common.Loader;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeState;

public class MainActivity extends AppCompatActivity {
    private MeState meState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meState = ViewModelProviders.of(this).get(MeState.class);
        initMe();
        subscribe();
    }

    private void initMe() {
        meState.setLoading(true);

        SharedPreferences prefs = getSharedPreferences("store", 0);
        UserActionsAPI.getMe(prefs.getString("token", ""), (err, response) -> {
            //error in loading
            if (err != null || !response.isSuccessful()) {
                meState.setLoading(false);
                return;
            }

            try {
                //parse response
                JSONObject userObj = new JSONObject(response.body().string());
                meState.setLoading(false);
                meState.setUser(User.fromJSON(userObj));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void subscribe() {
        meState.getUser().observe(this, (user) -> {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true);

            if (user == null) {
                transaction
                        .replace(R.id.container, new LoginFragment(), null)
                        .addToBackStack("login")
                        .commit();
            } else {
                transaction
                        .replace(R.id.container, new MainFragment(), null)
                        .addToBackStack("main")
                        .commit();
            }
        });

        meState.getLoadingData().observe(this, isLoading -> {
            if (isLoading) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new Loader(), null)
                        .addToBackStack("loading")
                        .commit();
            } else {
                getSupportFragmentManager().popBackStack("loading", 1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 1)
            fm.popBackStack();
        else
            finish();
    }
}
