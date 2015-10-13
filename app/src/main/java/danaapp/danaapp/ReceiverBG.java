package danaapp.danaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import danaapp.danaapp.services.ServiceBG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiverBG extends WakefulBroadcastReceiver {
    private static Logger log = LoggerFactory.getLogger(ReceiverBG.class);

    @Override
	public void onReceive(Context context, Intent intent) {
        log.debug("onReceive "+intent);
        startWakefulService(context, new Intent(context, ServiceBG.class)
                .setAction(intent.getAction())
                .putExtras(intent));
    }

}
