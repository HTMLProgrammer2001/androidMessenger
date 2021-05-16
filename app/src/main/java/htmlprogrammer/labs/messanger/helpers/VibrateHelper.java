package htmlprogrammer.labs.messanger.helpers;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import htmlprogrammer.labs.messanger.App;

public class VibrateHelper {
    private static VibrateHelper instance;
    private Vibrator vibrator;

    private VibrateHelper(){
        vibrator = (Vibrator) App.getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static VibrateHelper getInstance() {
        if(instance == null)
            instance = new VibrateHelper();

        return instance;
    }

    public void vibrateChoose(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else{
            vibrator.vibrate(50);
        }
    }
}
