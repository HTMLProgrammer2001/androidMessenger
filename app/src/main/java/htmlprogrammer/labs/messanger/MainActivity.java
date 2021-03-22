package htmlprogrammer.labs.messanger;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import htmlprogrammer.labs.messanger.fragments.LoginFragment;
import htmlprogrammer.labs.messanger.fragments.MainFragment;
import htmlprogrammer.labs.messanger.store.MeState;

public class MainActivity extends AppCompatActivity {
    private MeState meState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meState = ViewModelProviders.of(this).get(MeState.class);
        subscribe();
    }

    private void subscribe() {
        meState.getUser().observe(this, (user) -> {
            if (user == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.container, new LoginFragment(), null)
                        .addToBackStack("login")
                        .commit();
            }
            else{
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.container, new MainFragment(), null)
                        .addToBackStack("main")
                        .commit();
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
