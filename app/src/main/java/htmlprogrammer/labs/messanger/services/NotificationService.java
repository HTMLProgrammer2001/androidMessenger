package htmlprogrammer.labs.messanger.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.App;
import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.constants.WSEvents;
import htmlprogrammer.labs.messanger.models.Message;
import io.socket.client.IO;
import io.socket.client.Socket;


public class NotificationService extends Service {
    private Socket socket;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
//        try {
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//            socket = IO.socket(BuildConfig.WS_URL + "?Token=" + preferences.getString("token", ""));
//
//            socket.on(WSEvents.NEW_MESSAGE.getValue(), args -> {
//                Message msg = Message.fromJSON((JSONObject) args[0]);
//
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getContext())
//                        .setSmallIcon(R.drawable.logo)
//                        .setContentTitle(msg.getAuthor().getFullName())
//                        .setContentText(msg.getMessage());
//
//                NotificationManager manager = (NotificationManager) App.getContext().getSystemService(NOTIFICATION_SERVICE);
//
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                    NotificationChannel channel = new NotificationChannel(
//                            msg.getDialog().getId(),
//                            msg.getDialog().getName(),
//                            NotificationManager.IMPORTANCE_HIGH
//                    );
//
//                    manager.createNotificationChannel(channel);
//                    builder.setChannelId(msg.getDialog().getId());
//                }
//
//                Notification notification = builder.build();
//                manager.notify(msg.getDialog().getId().hashCode(), notification);
//            });
//
//            socket.connect();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
//        socket.close();
//
//        Intent notificationIntent = new Intent(getApplicationContext(), NotificationService.class);
//        startService(notificationIntent);
    }
}
