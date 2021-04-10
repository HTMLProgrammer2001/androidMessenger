package htmlprogrammer.labs.messanger.helpers;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageHelper {
    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = new Configuration(resources.getConfiguration());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            config.setLocale(locale);
        else
            config.locale = locale;

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
