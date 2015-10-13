package danaapp.danaapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Handler;

import android.support.v7.app.NotificationCompat;
import com.squareup.otto.Subscribe;

import danaapp.danaapp.event.ConnectionStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import danaapp.danaapp.event.StopEvent;

public class ServiceConnection extends Service {
    private static Logger log = LoggerFactory.getLogger(ServiceConnection.class);

    Handler mHandler;
    private HandlerThread mHandlerThread;

    private Notification mNotification;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private DanaConnection mDanaConnection;

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		log.info("onStartCommand");

        if(mHandlerThread==null) {
            enableForeground();
            log.debug("Creating handler thread");
            this.mHandlerThread = new HandlerThread(ServiceConnection.class.getSimpleName()+"Handler");
            mHandlerThread.start();

            this.mHandler = new Handler(mHandlerThread.getLooper());

            mDanaConnection = MainApp.getDanaConnection();

            registerBus();
            if(mDanaConnection==null) {
                mDanaConnection = new DanaConnection(null,MainApp.bus());
                MainApp.setDanaConnection(mDanaConnection);
            }
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDanaConnection.connectIfNotConnected("onStartCommand connectionCheck");
            }
        });


        log.info("onStartCommand end");
		return START_STICKY;
    }

    private void registerBus() {
        try {
            MainApp.bus().unregister(this);
        } catch (RuntimeException x) {
            // Ignore
        }
        MainApp.bus().register(this);
    }
    private void enableForeground() {
        mNotificationCompatBuilder = new NotificationCompat.Builder(getApplicationContext());
        mNotificationCompatBuilder.setContentTitle("DanaR App")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setLocalOnly(true);

//        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
//        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                notificationIntent, 0);
//
//        mNotificationCompatBuilder.setContentIntent(pendingNotificationIntent);

        mNotification = mNotificationCompatBuilder.build();

        nortifManagerNotify();

        startForeground(129, mNotification);
    }

    private void nortifManagerNotify() {
        mNotificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(129,mNotification);
    }

    @Subscribe
    public void onStatusEvent(final ConnectionStatusEvent c) {
        String connectionText = "Connecting ";
        if(c.sConnecting) {
            connectionText = "Connecting ";
        } else {
            if (c.sConnected) {
                connectionText = "Connected";
            } else {
                connectionText = "Disconnected";
}
          }
//        mNotification.tickerText = connectionText;
//        mNotification.when = System.currentTimeMillis();;

        mNotificationCompatBuilder.setWhen(System.currentTimeMillis())
//                .setTicker(connectionText)
                .setContentText(connectionText);

        mNotification = mNotificationCompatBuilder.build();
        nortifManagerNotify();
    }

    @Subscribe
    public void onStopEvent(StopEvent event) {
        log.debug("onStopEvent received");
        mDanaConnection.stop();

        stopForeground(true);
        stopSelf();
        log.debug("onStopEvent finished");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
	public void onCreate() {
        log.info("onCreate");
        mHandler = new Handler();


	}

    @Override
	public void onDestroy() {
		super.onDestroy();
		log.info( "onDestroy");
	}

}
