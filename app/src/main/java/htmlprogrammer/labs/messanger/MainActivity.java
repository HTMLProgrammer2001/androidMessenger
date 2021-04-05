package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.dialogs.InfoDialog;
import htmlprogrammer.labs.messanger.fragments.LoginFragment;
import htmlprogrammer.labs.messanger.fragments.MainFragment;
import htmlprogrammer.labs.messanger.fragments.SettingsFragment;
import htmlprogrammer.labs.messanger.fragments.common.Loader;
import htmlprogrammer.labs.messanger.fragments.common.UserAvatar;
import htmlprogrammer.labs.messanger.interfaces.DrawerClosable;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeState;

public class MainActivity extends AppCompatActivity implements DrawerClosable {
    private MeState meState;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navView);

        navigationView.setNavigationItemSelectedListener(this::onNavigationSelect);

        meState = ViewModelProviders.of(this).get(MeState.class);
        initMe();
        subscribe();
    }

    public void setDrawerOpen(boolean isOpen){
        int lockMode = isOpen ? DrawerLayout.LOCK_MODE_UNDEFINED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
    }

    public boolean onNavigationSelect(MenuItem item){
        switch (item.getItemId()){
            case R.id.settings:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new SettingsFragment(), null)
                        .addToBackStack("settings")
                        .commit();

                drawerLayout.closeDrawer(Gravity.START);

                break;

            case R.id.info:
                InfoDialog dialog = new InfoDialog();
                dialog.show(getSupportFragmentManager(), "info");
                break;
        }

        return true;
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
            if (user == null) {
                redirectToLogin();
            } else {
                redirectToMain(user);
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

    private void redirectToLogin(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LoginFragment(), null)
                .addToBackStack("login")
                .commit();

        this.setDrawerOpen(false);
    }

    private void redirectToMain(User user){
        String shortName = user.getFullName();
        UserAvatar avatarFragment = UserAvatar.getInstance(shortName, user.getAvatar());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MainFragment(), null)
                .addToBackStack("main")
                .replace(R.id.avatarFrame, avatarFragment, null)
                .commit();

        NavigationView navigationView = findViewById(R.id.navView);
        View headerView = navigationView.getHeaderView(0);

        TextView name = headerView.findViewById(R.id.name);
        name.setText(user.getFullName() + "(" + user.getNick() + ")");

        TextView phone = headerView.findViewById(R.id.phone);
        phone.setText(user.getPhone());

        this.setDrawerOpen(true);
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
