package danaapp.danaapp.alarm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import danaapp.danaapp.AlarmMessage;

import java.util.Date;

public class ServiceAlarm extends Service {
    private static final String TAG = ServiceAlarm.class.getSimpleName();

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long lastAlarm = preferences.getLong("lastAlarm", 0);
        long currentTime = new Date().getTime();

        if((currentTime - lastAlarm) < 15*60*1000 ) {
            stopSelf(startId);
            Log.i(TAG, "Alarm posponed");
        } else {

            AlarmMessage alarm = new AlarmMessage(getApplicationContext());

            if (intent != null) {
                String alarmText = intent.getStringExtra("alarmText");
                if (alarmText != null) {
                    alarm.setText(alarmText);
                }

                alarm.setOnDismiss(new Runnable() {

                    @Override
                    public void run() {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putLong("lastAlarm", new Date().getTime());

                        editor.commit();
                        ServiceAlarm.this.stopSelf();
                    }
                });

                alarm.showMessage();
            }
        }
        Log.i(TAG, "onStartCommand end");
		return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
