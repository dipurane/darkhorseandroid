package billfold.com.synerzip.billfold.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by synerzip on 27/11/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        SmsMessage shortMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);

        if (shortMessage.getOriginatingAddress().equals("BH-netsms")) {
            Log.e("SMSReceiver", "SMS message sender: " +
                    shortMessage.getOriginatingAddress());
            Log.e("SMSReceiver", "SMS message text: " +
                    shortMessage.getDisplayMessageBody());


            Intent intentRceveir = new Intent("custom-event-name");
            // You can also include some extra data.
            intentRceveir.putExtra("message", shortMessage.getDisplayMessageBody());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentRceveir);
        }
    }
}
