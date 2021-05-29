package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.fragments.Loader;
import htmlprogrammer.labs.messanger.helpers.LanguageHelper;
import htmlprogrammer.labs.messanger.interfaces.BaseActivity;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.services.NotificationService;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.viewmodels.MeViewModel;

public class MainActivity extends BaseActivity {
    private boolean isMain = false;
    private MeViewModel meVM;
    private MeStore meStore = MeStore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change language
        LanguageHelper.setLocale(this, preferences.getString("language", "en"));
        meVM = ViewModelProviders.of(this).get(MeViewModel.class);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loading, new Loader(), null)
                .commit();

        initMe();
        subscribe();
    }

    private void initMe() {
        meStore.startLoading();
        meStore.setToken(preferences.getString("token", ""));

        UserActionsAPI.getMe(preferences.getString("token", ""), (err, response) -> {
            //error in loading
            if (err != null || !response.isSuccessful()) {
                meStore.stopLoading();
                return;
            }

            try {
                //parse response
                JSONObject userObj = new JSONObject(response.body().string());
                meStore.setUser(User.fromJSON(userObj));
                meStore.stopLoading();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void subscribe() {
        meVM.getUser().observe(this, (user) -> {
            if (user == null) {
                redirectToLogin();
                isMain = false;
                return;
            }
            else if(!isMain) {
                redirectToMain();
                isMain = true;
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void redirectToMain(){
        Intent intent = new Intent(this, DialogsActivity.class);
        startActivity(intent);
    }
}
