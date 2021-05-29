package htmlprogrammer.labs.messanger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import htmlprogrammer.labs.messanger.services.NotificationService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, NotificationService.class);
        context.startService(notificationIntent);
    }
}
