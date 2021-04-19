package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.constants.ActionBarType;
import htmlprogrammer.labs.messanger.dialogs.InfoDialog;
import htmlprogrammer.labs.messanger.fragments.LoginFragment;
import htmlprogrammer.labs.messanger.fragments.MainFragment;
import htmlprogrammer.labs.messanger.fragments.SettingsFragment;
import htmlprogrammer.labs.messanger.fragments.common.Loader;
import htmlprogrammer.labs.messanger.fragments.common.UserAvatar;
import htmlprogrammer.labs.messanger.helpers.LanguageHelper;
import htmlprogrammer.labs.messanger.interfaces.ActionBarChanged;
import htmlprogrammer.labs.messanger.interfaces.DrawerClosable;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.viewmodels.MeViewModel;

public class MainActivity extends AppCompatActivity implements DrawerClosable, ActionBarChanged {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBar bar;
    private ActionBarType actionBarType;

    private UserAvatar userAvatarFragment;
    private boolean isMain = false;
    private MeViewModel meVM;
    private MeStore meStore = MeStore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //change language
        SharedPreferences pref = getSharedPreferences("store", 0);
        LanguageHelper.setLocale(this, pref.getString("language", "en"));

        setContentView(R.layout.activity_main);

        //find elements
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navView);

        //create avatar fragment
        userAvatarFragment = UserAvatar.getInstance("", "");

        //show action bar
        bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeAsUpIndicator(R.drawable.burger);

        //init listeners
        navigationView.setNavigationItemSelectedListener(this::onNavigationSelect);

        meVM = ViewModelProviders.of(this).get(MeViewModel.class);

        initMe();
        subscribe();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarType == ActionBarType.TOOLBAR)
            toggleSidebar();
        else if (actionBarType == ActionBarType.BACK)
            getSupportFragmentManager().popBackStack();
        else
            return super.onOptionsItemSelected(item);

        return true;
    }

    private void toggleSidebar() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.START);
        else
            drawerLayout.openDrawer(Gravity.LEFT);
    }

    public void setDrawerOpen(boolean isOpen) {
        int lockMode = isOpen ? DrawerLayout.LOCK_MODE_UNDEFINED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
    }

    @Override
    public void setActionBarType(ActionBarType type) {
        this.actionBarType = type;

        if (type == ActionBarType.NONE) {
            bar.setDisplayHomeAsUpEnabled(false);
        } else if (type == ActionBarType.BACK) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        } else {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.burger);
        }
    }

    public boolean onNavigationSelect(MenuItem item) {
        switch (item.getItemId()) {
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
        SharedPreferences prefs = getSharedPreferences("store", 0);
        meStore.startLoading();
        meStore.setToken(prefs.getString("token", ""));

        UserActionsAPI.getMe(prefs.getString("token", ""), (err, response) -> {
            //error in loading
            if (err != null || !response.isSuccessful()) {
                meStore.stopLoading();
                return;
            }

            try {
                //parse response
                JSONObject userObj = new JSONObject(response.body().string());
                meStore.stopLoading();
                meStore.setUser(User.fromJSON(userObj));
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

            if (!isMain) {
                redirectToMain();
                isMain = true;
            }

            //show avatar
            userAvatarFragment.initUI(user.getFullName(), user.getAvatar());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.avatarFrame, userAvatarFragment, null)
                    .commit();

            //show data
            View headerView = navigationView.getHeaderView(0);

            TextView name = headerView.findViewById(R.id.name);
            name.setText(user.getFullName() + "(" + user.getNick() + ")");

            TextView phone = headerView.findViewById(R.id.phone);
            phone.setText(user.getPhone());
        });

        meVM.getLoadingData().observe(this, isLoading -> {
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

    private void redirectToLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LoginFragment(), null)
                .addToBackStack("login")
                .commit();

        this.setDrawerOpen(false);
    }

    private void redirectToMain() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MainFragment(), null)
                .addToBackStack("main")
                .commit();

        this.setDrawerOpen(true);
    }
}
