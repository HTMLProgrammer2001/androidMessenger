package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import htmlprogrammer.labs.messanger.api.SearchAPI;
import htmlprogrammer.labs.messanger.constants.SearchTypes;
import htmlprogrammer.labs.messanger.dialogs.InfoDialog;
import htmlprogrammer.labs.messanger.fragments.UserAvatar;
import htmlprogrammer.labs.messanger.interfaces.BaseActivity;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.search.SearchDialogsStore;
import htmlprogrammer.labs.messanger.store.search.SearchMessagesStore;
import htmlprogrammer.labs.messanger.store.search.SearchStore;
import htmlprogrammer.labs.messanger.store.search.SearchUserStore;
import htmlprogrammer.labs.messanger.viewmodels.MeViewModel;
import htmlprogrammer.labs.messanger.viewmodels.search.SearchViewModel;
import okhttp3.Response;

public class DialogsActivity extends BaseActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBar bar;
    private ScrollView scrollView;

    private UserAvatar userAvatarFragment;
    private MeViewModel meVM;
    private SearchViewModel searchVM;

    private SearchStore searchStore = SearchStore.getInstance();
    private SearchDialogsStore searchDialogsStore = SearchDialogsStore.getInstance();
    private SearchMessagesStore searchMessagesStore = SearchMessagesStore.getInstance();
    private SearchUserStore searchUserStore = SearchUserStore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        //find elements
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navView);
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

        subscribe();
        startDialogsLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUserStore.reset();
                searchDialogsStore.reset();
                searchMessagesStore.reset();

                searchStore.setSearchText(query);
                startDialogsLoading();

                if(query != null && query.startsWith("@"))
                    startUserLoading();
                else if(query != null && !query.isEmpty())
                    startMessagesLoading();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            searchStore.setSearchText("");
            searchUserStore.reset();
            searchDialogsStore.reset();
            searchMessagesStore.reset();

            startDialogsLoading();

            return false;
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.reload) {
            searchStore.setSearchText("");
            searchUserStore.reset();
            searchDialogsStore.reset();
            searchMessagesStore.reset();

            startDialogsLoading();
        }
        else
            toggleSidebar();

        return true;
    }

    private void startUserLoading(){
        searchUserStore.startLoading();

        //get user
        SearchAPI.getUserByNickname(MeStore.getInstance().getToken(),
                searchStore.getSearchText().substring(1), this::onUserLoaded);
    }

    private void onUserLoaded(Exception e, Response response){
        //stop loading
        searchUserStore.stopLoading();

        if(e != null || !response.isSuccessful()){
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            searchUserStore.setError(errorText);
        }
        else{
            try {
                //parse new dialogs
                JSONObject object = new JSONObject(response.body().string());
                User newUser = User.fromJSON(object.getJSONObject("user"));

                //add it to state
                searchUserStore.setUser(newUser);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public void startMessagesLoading(){
        searchMessagesStore.startLoading();

        //get dialogs
        SearchAPI.getMessagesByName(MeStore.getInstance().getToken(),
                searchStore.getSearchText(),
                searchMessagesStore.getCurPage() + 1,
                searchMessagesStore.getPageSize(),
                this::onMessagesLoaded
        );
    }

    private void onMessagesLoaded(Exception e, Response response){
        //stop loading
        searchMessagesStore.stopLoading();

        if(e != null || !response.isSuccessful()){
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            searchMessagesStore.setError(errorText);
        }
        else{
            try {
                //parse new dialogs
                JSONObject object = new JSONObject(response.body().string());
                JSONArray array = object.getJSONArray("data");

                ArrayList<Message> newMessages = new ArrayList<>();

                for(int i = 0; i < array.length(); i++){
                    newMessages.add(Message.fromJSON(array.getJSONObject(i)));
                }

                //add it to state
                searchMessagesStore.setCurPage(object.getInt("page"));
                searchMessagesStore.setTotalPages(object.getInt("totalPages"));
                searchMessagesStore.addMessages(newMessages);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public void startDialogsLoading(){
        searchDialogsStore.startLoading();
        SearchTypes type = searchVM.getSearchType().getValue();

        //get dialogs
        if(type != null && type.equals(SearchTypes.NICK)){
            SearchAPI.getDialogsByNick(MeStore.getInstance().getToken(),
                    searchStore.getSearchText().substring(1),
                    searchDialogsStore.getCurPage() + 1,
                    searchDialogsStore.getPageSize(),
                    this::onDialogsLoaded
            );
        }
        else{
            SearchAPI.getDialogsByName(MeStore.getInstance().getToken(),
                    searchStore.getSearchText(),
                    searchDialogsStore.getCurPage() + 1,
                    searchDialogsStore.getPageSize(),
                    this::onDialogsLoaded
            );
        }
    }

    private void onDialogsLoaded(Exception e, Response response){
        //stop loading
        searchDialogsStore.stopLoading();

        if(e != null || !response.isSuccessful()){
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            searchDialogsStore.setError(errorText);
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
                searchDialogsStore.setCurPage(object.getInt("page"));
                searchDialogsStore.setTotalPages(object.getInt("totalPages"));
                searchDialogsStore.addDialogs(newDialogs);
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
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            boolean loadMore = !scrollView.canScrollVertically(20) && !searchDialogsStore.getLoading()
                    && searchDialogsStore.hasMore() && searchVM.getSearchType().getValue().equals(SearchTypes.DEFAULT)
                    && searchVM.getSearchType().getValue().equals(SearchTypes.DEFAULT);

            if(loadMore)
                startDialogsLoading();
        });

        searchVM.getSearchType().observe(this, (type) -> {
            switch (type){
                case NICK:
                    nickUI();
                    break;

                case TEXT:
                    textUI();
                    break;

                default:
                    defaultUI();
            }
        });

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

    private void nickUI(){
        Fragment message = getSupportFragmentManager().findFragmentById(R.id.messageFragment);
        Fragment user = getSupportFragmentManager().findFragmentById(R.id.userFragment);

        if(user != null)
            getSupportFragmentManager().beginTransaction().show(user).commit();

        if(message != null)
            getSupportFragmentManager().beginTransaction().hide(message).commit();
    }

    private void textUI(){
        Fragment message = getSupportFragmentManager().findFragmentById(R.id.messageFragment);
        Fragment user = getSupportFragmentManager().findFragmentById(R.id.userFragment);

        if(user != null)
            getSupportFragmentManager().beginTransaction().hide(user).commit();

        if(message != null)
            getSupportFragmentManager().beginTransaction().show(message).commit();
    }

    private void defaultUI(){
        Fragment message = getSupportFragmentManager().findFragmentById(R.id.messageFragment);
        Fragment user = getSupportFragmentManager().findFragmentById(R.id.userFragment);

        if(user != null)
            getSupportFragmentManager().beginTransaction().hide(user).commit();

        if(message != null)
            getSupportFragmentManager().beginTransaction().hide(message).commit();
    }
}
