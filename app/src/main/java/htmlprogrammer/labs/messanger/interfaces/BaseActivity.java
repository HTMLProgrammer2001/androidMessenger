package htmlprogrammer.labs.messanger.interfaces;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected static SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
}
