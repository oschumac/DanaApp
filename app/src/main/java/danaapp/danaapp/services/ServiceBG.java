package danaapp.danaapp.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import danaapp.danaapp.*;
import danaapp.danaapp.calc.IobCalc;
import danaapp.danaapp.event.LowSuspendStatus;
import danaapp.danaapp.event.StatusEvent;
import org.openaps.openAPS.DatermineBasalResult;
import org.openaps.openAPS.DetermineBasalAdapterJS;
import org.openaps.openAPS.ScriptReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceBG extends android.app.IntentService {
    private static Logger log = LoggerFactory.getLogger(ServiceBG.class);

    public static final String ACTION_NEW_DATA = "danaR.action.BG_DATA";

    public static final DecimalFormat numberFormat = new DecimalFormat("0.00");
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    boolean weRequestStartOfConnection = false;

//    DetermineBasalAdapterJS determineBasalAdapterJS;

    public ServiceBG() {
        super("ServiceBG");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
            return;

        try {
            Bundle bundle = intent.getExtras();
            Date time =  new Date(bundle.getLong("time"));
            int glucoseValue = bundle.getInt("value");
            int delta = bundle.getInt("delta");
            double deltaAvg30min = bundle.getDouble("deltaAvg30min");
            double deltaAvg15min = bundle.getDouble("deltaAvg15min");
            double avg30min = bundle.getDouble("avg30min");
            double avg15min = bundle.getDouble("avg15min");

            String msgReceived = "time:" + dateFormat.format(time)
                    + " bg " + glucoseValue
                    + " dlta: " + delta
                    + " dltaAvg30m:" + numberFormat.format(deltaAvg30min)
                    + " dltaAvg15m:" + numberFormat.format(deltaAvg15min)
                    + " avg30m:" + numberFormat.format(avg30min)
                    + " avg15m:" + numberFormat.format(avg15min);
            log.debug("onHandleIntent "+msgReceived);


            LowSuspendStatus lowSuspendStatus = LowSuspendStatus.getInstance();
            lowSuspendStatus.dataText = msgReceived;

            StatusEvent statusEvent = StatusEvent.getInstance();
            DanaConnection danaConnection = getDanaConnection();

            DatermineBasalResult datermineBasalResult = openAps(glucoseValue, delta, deltaAvg15min, msgReceived, statusEvent, lowSuspendStatus);

            double percent = 100 ;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean openAPSenabled = preferences.getBoolean("OpenAPSenabled", false);
            if(openAPSenabled) {
                if (datermineBasalResult.tempBasalRate == -1) {
                    percent = statusEvent.tempBasalRatio;
                } else if (datermineBasalResult.duration == 0) {
                    percent = 100;
                } else {
                    percent = datermineBasalResult.tempBasalRate / statusEvent.currentBasal * 10;
                    log.debug("openApsTempAbsolute:" + datermineBasalResult.tempBasalRate + " percent:" + percent);
                    percent = Math.floor(percent) * 10;
                    log.debug(" percent rounded :" + percent);
                    if (percent > 200) {
                        percent = 200;
                    }
                }
            }

            int lowSuspendTempPercent = lowSuspend( LowSuspendStatus.getInstance(),glucoseValue,deltaAvg15min);
            boolean LowSuspendenabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("LowSuspendEnabled", false);

            int tempPercent = (LowSuspendenabled && lowSuspendTempPercent==0) ? 0 : (int) percent ;

            if((new Date().getTime() -  statusEvent.timeLastSync.getTime())>60*60_000) {
                log.debug("Requesting status ...");

                danaConnection.connectIfNotConnected("ServiceBG 1hour");

            }

            if(tempPercent!=100 && tempPercent!=statusEvent.tempBasalRatio) {
                danaConnection.connectIfNotConnected("ServiceBG setTemp");



                try {
                    if(statusEvent.tempBasalRemainMin != 0) {
                        danaConnection.tempBasalOff();
                        danaConnection.tempBasal(tempPercent,1);
                    } else {
                        danaConnection.tempBasal(tempPercent,1);
                    }

                    if(statusEvent.tempBasalRatio!=tempPercent) {
                        log.error("Temp basal set failed");
                    } else {
                        log.info("Temp basal set "+statusEvent.tempBasalRatio);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            } else if (tempPercent==100 && statusEvent.tempBasalRemainMin != 0
//                    && statusEvent.tempBasalRatio==0
                    ) {
                log.error("Temp basal off ");
                danaConnection.connectIfNotConnected("ServiceBG");
                danaConnection.tempBasalOff();
                if(statusEvent.tempBasalRemainMin != 0) {
                    log.error("Temp basal off failed");
                }
            } else {
                log.info("No Action: Temp basal as requested: " + tempPercent + " tempBasalRatio:" + statusEvent.tempBasalRatio);
            }


        } catch (Throwable x){
            log.error(x.getMessage(),x);

        } finally {
            ReceiverBG.completeWakefulIntent(intent);
        }

    }

    private DatermineBasalResult openAps(int glucoseValue, int delta, double deltaAvg15min, String msgReceived, StatusEvent status, LowSuspendStatus lowSuspendStatus) {
        DetermineBasalAdapterJS determineBasalAdapterJS = null;
        try {
            determineBasalAdapterJS = new DetermineBasalAdapterJS(new ScriptReader(MainApp.instance().getBaseContext()));
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }

        determineBasalAdapterJS.setGlucoseStatus(glucoseValue,delta,deltaAvg15min);
        determineBasalAdapterJS.setProfile_Sens(Settings.insSensitivity);

        IobCalc.Iob bolusIobOpenAPS = MainActivity.getIobOpenAPSFromBoluses(MainActivity.loadBoluses());
        IobCalc.Iob bolusIob = MainActivity.getIobFromBoluses(MainActivity.loadBoluses());
        IobCalc.Iob basalIob = MainActivity.getIobFromTempBasals(MainActivity.loadTempBasalsDB());

        broadcastIob(bolusIobOpenAPS, bolusIob, basalIob);

        basalIob.plus(bolusIobOpenAPS);

        determineBasalAdapterJS.setIobData(basalIob.iobContrib,basalIob.activityContrib,bolusIobOpenAPS.iobContrib);

        determineBasalAdapterJS.setProfile_CurrentBasal(status.currentBasal);
        determineBasalAdapterJS.setProfile_MaxBasal(status.currentBasal*2);
        double tempBasalRatioAbsolute = status.tempBasalInProgress==1 ? status.tempBasalRatio/100.0d*status.currentBasal : 0;
        int tempBasalRemainMin = status.tempBasalRemainMin;
        if(tempBasalRemainMin>30) {
            tempBasalRemainMin = 30;
        }
        determineBasalAdapterJS.setCurrentTemp(tempBasalRemainMin,tempBasalRatioAbsolute);

        DatermineBasalResult determineBasalResult = determineBasalAdapterJS.invoke();


        lowSuspendStatus.lowSuspendDataTextOpenAps = determineBasalResult.reason;

        determineBasalAdapterJS.release();
        determineBasalAdapterJS = null;

        return determineBasalResult;
    }

    private void broadcastIob(IobCalc.Iob bolusIobOpenAPS, IobCalc.Iob bolusIob, IobCalc.Iob basalIob) {
        Intent intent = new Intent("danaR.action.IOB_DATA");

        Bundle bundle = new Bundle();

        bundle.putLong("time", new Date().getTime());
        bundle.putDouble("bolusIob", bolusIob.iobContrib);
        bundle.putDouble("bolusIobAPS", bolusIobOpenAPS.iobContrib);
        bundle.putDouble("bolusIobActivity", bolusIobOpenAPS.activityContrib);
        bundle.putDouble("basalIob", basalIob.iobContrib);
        bundle.putDouble("basalIobActivity", basalIob.activityContrib);

        intent.putExtras(bundle);
        MainApp.instance().getApplicationContext().sendBroadcast(intent);
    }

    private int lowSuspend(LowSuspendStatus lowSuspendStatus, int glucoseValue, double deltaAvg15min) throws InterruptedException {
        boolean lowProjected = (glucoseValue + 6.0*deltaAvg15min) <90;
        boolean nearLowAndDropping = false; //glucoseValue < 130 && deltaAvg15min <= -1.5;
        boolean low = glucoseValue < 90;
//            boolean okBg = glucoseValue > 120 ;
//            boolean rising = avg15min > 90 && deltaAvg30min >1;
        String rulesText = "n130Dropping: " + nearLowAndDropping + " low: " + low + " lowProjected:"+lowProjected;
//                + " okBg: " + okBg + " rising:"+rising;
        lowSuspendStatus.statusText = rulesText;
        log.debug("onHandleIntent "+rulesText);


        int basalPercent;

        if(low || nearLowAndDropping || lowProjected) {
            basalPercent = 0;
            log.info("lowSuspend: Temp basal 0% ");


        } else {
            basalPercent = 100;
            log.info("lowSuspend: No action ");
        }

        return basalPercent;
    }


    private DanaConnection getDanaConnection() throws InterruptedException {
        DanaConnection danaConnection = MainApp.getDanaConnection();
        if(danaConnection==null) {
            weRequestStartOfConnection = true;
            getApplicationContext().startService(new Intent(getApplicationContext(), ServiceConnection.class));
            int counter = 0;
            do{
                danaConnection = MainApp.getDanaConnection();
                Thread.sleep(100);
                counter++;
            }while(danaConnection == null && counter < 10);
            if(danaConnection == null) {
                log.error("danaConnection == null");
                weRequestStartOfConnection = false;
            }
        }
        return danaConnection;
    }
}
