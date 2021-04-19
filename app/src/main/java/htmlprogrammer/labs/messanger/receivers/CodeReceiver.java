package htmlprogrammer.labs.messanger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.interfaces.CodeHandler;

public class CodeReceiver extends BroadcastReceiver {
    private CodeHandler handler;

    public CodeReceiver(CodeHandler handler){
        super();
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
//                if(!smsMessage.getDisplayOriginatingAddress().equals(BuildConfig.SMS_SENDER))
//                    return;

                Matcher matcher = Pattern.compile("\\d+").matcher(smsMessage.getMessageBody());

                while (matcher.find()){
                    this.handler.handle(matcher.group(0));
                }
            }
        }
    }
}
