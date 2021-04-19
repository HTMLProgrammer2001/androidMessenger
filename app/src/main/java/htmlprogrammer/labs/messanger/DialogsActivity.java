package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.adapters.DialogAdapter;
import htmlprogrammer.labs.messanger.api.SearchAPI;
import htmlprogrammer.labs.messanger.dialogs.InfoDialog;
import htmlprogrammer.labs.messanger.fragments.UserAvatar;
import htmlprogrammer.labs.messanger.interfaces.BaseActivity;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.SearchStore;
import htmlprogrammer.labs.messanger.viewmodels.MeViewModel;
import htmlprogrammer.labs.messanger.viewmodels.SearchViewModel;
import okhttp3.Response;

public class DialogsActivity extends BaseActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBar bar;
    private RecyclerView list;
    private ScrollView scrollView;

    private UserAvatar userAvatarFragment;
    private MeViewModel meVM;
    private DialogAdapter adapter;
    private SearchViewModel searchVM;
    private SearchStore searchStore = SearchStore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        //find elements
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navView);
        list = findViewById(R.id.list);
        scrollView = findViewById(R.id.scroll);

        //create avatar fragment
        userAvatarFragment = UserAvatar.getInstance("", "");

        //show action bar
        bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeAsUpIndicator(R.drawable.burger);

        //init listeners
        navigationView.setNavigationItemSelectedListener(this::onNavigationSelect);

        meVM = ViewModelProviders.of(this).get(MeViewModel.class);
        searchVM = ViewModelProviders.of(this).get(SearchViewModel.class);

        initList();
        subscribe();
        startLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.reload)
            startLoading();
        else
            toggleSidebar();

        return true;
    }

    private void initList(){
        adapter = new DialogAdapter(this);
        list.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
    }

    private void startLoading(){
        searchStore.startLoading();
        searchStore.setDialogs(new ArrayList<>());

        //get dialogs
        SearchAPI.getDialogsByName(MeStore.getInstance().getToken(), "", this::onLoaded);
    }

    private void onLoaded(Exception e, Response response){
        //stop loading
        searchStore.stopLoading();

        if(e != null || !response.isSuccessful()){
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            searchStore.setError(errorText);
        }
        else{
            try {
                //parse new dialogs
                JSONObject object = new JSONObject(response.body().string());
                JSONArray array = object.getJSONArray("data");

                ArrayList<Dialog> newDialogs = new ArrayList<>();

                for(int i = 0; i < array.length(); i++){
                    newDialogs.add(Dialog.fromJSON(array.getJSONObject(i)));
                }

                //add it to state
                searchStore.addDialogs(newDialogs);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private void toggleSidebar() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.START);
        else
            drawerLayout.openDrawer(Gravity.LEFT);
    }

    public boolean onNavigationSelect(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                drawerLayout.closeDrawer(Gravity.START);

                break;

            case R.id.info:
                InfoDialog dialog = new InfoDialog();
                dialog.show(getSupportFragmentManager(), "info");
                break;
        }

        return true;
    }

    private void subscribe() {
        searchVM.getDialogs().observe(this, (dialogs) -> adapter.setData(dialogs));
        searchVM.getLoading().observe(this, (isLoading) -> adapter.setLoading(isLoading));

        meVM.getUser().observe(this, (user) -> {
            if (user == null)
                return;

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
    }
}
