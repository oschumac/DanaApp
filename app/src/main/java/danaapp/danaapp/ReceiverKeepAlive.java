package danaapp.danaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiverKeepAlive extends BroadcastReceiver {
    private static Logger log = LoggerFactory.getLogger(ReceiverKeepAlive.class);

    @Override
	public void onReceive(Context context, Intent intentX) {

        Intent intent = new Intent(context, ServiceConnection.class);
        context.startService(intent);
        log.info( "RefreshReceiver started ServiceConnection "+intent);

    }

}
