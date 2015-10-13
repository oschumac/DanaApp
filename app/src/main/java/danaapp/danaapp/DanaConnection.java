package danaapp.danaapp;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.squareup.otto.Bus;

import danaapp.danaapp.alarm.ServiceAlarm;
import danaapp.danaapp.bolus.BolusUI;
import danaapp.danaapp.comm.*;
import danaapp.danaapp.db.Bolus;
import danaapp.danaapp.db.Carbs;
import danaapp.danaapp.db.PumpStatus;
import danaapp.danaapp.db.TempBasal;
import danaapp.danaapp.event.ConnectionStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

import danaapp.danaapp.event.StatusEvent;

public class DanaConnection {

    private static Logger log = LoggerFactory.getLogger(DanaConnection.class);

    Handler mHandler;
    public static HandlerThread mHandlerThread;

    private final Bus mBus;
    private SerialEngine mSerialEngine;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private BluetoothSocket mRfcommSocket;
    private BluetoothDevice mDevice;
    private boolean connectionEnabled = false;
    PowerManager.WakeLock mWakeLock;

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public DanaConnection(BluetoothDevice bDevice, Bus bus) {
        MainApp.setDanaConnection(this);

        mHandlerThread = new HandlerThread(DanaConnection.class.getSimpleName());
        mHandlerThread.start();

        this.mHandler = new Handler(mHandlerThread.getLooper());

        this.mBus = bus;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

        for(BluetoothDevice device:devices) {
            String dName = device.getName();
            if("<DEV_NAME_HERE>".equals(dName)) {
                device.getAddress();
                mDevice = device;

                try {
                    mRfcommSocket = mDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                } catch (IOException e) {
                    log.error("err",e);
                }

                break;
            }
        }


        registerBTconnectionBroadcastReceiver();

        PowerManager powerManager = (PowerManager) MainApp.instance().getApplicationContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DanaConnection");
    }

    private void registerBTconnectionBroadcastReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String action = intent.getAction();
                Log.d("ConnectionBroadcast ", "Device  " + action + " " + device.getName());//Device has disconnected
                if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    Log.d("ConnectionBroadcast", "Device has disconnected " + device.getName());//Device has disconnected
                    if(mDevice.getName().equals(device.getName())) {
                        if(mRfcommSocket!=null) {

                            try {mInputStream.close();} catch (Exception e)  {log.debug(e.getMessage());}
                            try {mOutputStream.close();} catch (Exception e) {log.debug(e.getMessage());}
                            try {mRfcommSocket.close(); } catch (Exception e) {log.debug(e.getMessage());}


                        }
                        connectionEnabled = false;
                        mBus.post(new ConnectionStatusEvent(false,false, 0));
                        //connectionCheckAsync();
//                        MainApp.setDanaConnection(null);
                    }
                }

            }
        };
        MainApp.instance().getApplicationContext().registerReceiver(receiver,new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        MainApp.instance().getApplicationContext().registerReceiver(receiver,new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
        MainApp.instance().getApplicationContext().registerReceiver(receiver,new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }

    public void connectionCheckAsync(final String callerName) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectionCheck(callerName);
            }
        },100);
    }

    public synchronized void connectIfNotConnected(String callerName) {
//        log.debug("connectIfNotConnected caller:"+callerName);
        mWakeLock.acquire();
        long startTime = System.currentTimeMillis();
        short connectionAttemptCount = 0;
        if(!(isConnected())) {
            long timeToConnectTimeSoFar = 0;
            while (!(isConnected())) {
                timeToConnectTimeSoFar = (System.currentTimeMillis() - startTime) / 1000;
                mBus.post(new ConnectionStatusEvent(true,false, connectionAttemptCount));
                connectionCheck(callerName);
                log.debug("connectIfNotConnected waiting " + timeToConnectTimeSoFar + "s attempts:" + connectionAttemptCount + " caller:"+callerName);
                connectionAttemptCount++;

                if(timeToConnectTimeSoFar/60>15 || connectionAttemptCount >180) {
                    Intent alarmServiceIntent = new Intent(MainApp.instance().getApplicationContext(), ServiceAlarm.class);
                    alarmServiceIntent.putExtra("alarmText","Connection error");
                    MainApp.instance().getApplicationContext().startService(alarmServiceIntent);
                }
            }
            log.debug("connectIfNotConnected took " + timeToConnectTimeSoFar + "s attempts:" + connectionAttemptCount);
        } else {
            mBus.post(new ConnectionStatusEvent(false,true, 0));
        }
        mWakeLock.release();
    }

    private synchronized void  connectionCheck(String callerName) {
        if(mRfcommSocket == null) {
            try {
                mRfcommSocket = mDevice.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (IOException e) {
                log.error("err", e);
            }
            if(mRfcommSocket==null) {
                log.warn("connectionCheck() mRfcommSocket is null ");
                return;
            }
        }
        if( !mRfcommSocket.isConnected()) {
//            log.debug("not connected");
            try {
                mRfcommSocket.connect();
                log.debug( "connected");

                mOutputStream = mRfcommSocket.getOutputStream();
                mInputStream =  mRfcommSocket.getInputStream();
                if(mSerialEngine!=null) {
                    mSerialEngine.stopIt();
                };
                mSerialEngine = new SerialEngine(mInputStream,mOutputStream,mRfcommSocket   );
                mBus.post(new ConnectionStatusEvent(false,true, 0));

            } catch (IOException e) {
                log.warn( "connectionCheck() ConnectionStatusEvent attempt failed: " + e.getMessage());
                mRfcommSocket = null;
                //connectionCheckAsync("connectionCheck retry");
            }
        }


        if(isConnected()) {
            mBus.post(new ConnectionStatusEvent(false,true, 0));
            pingStatus();
        }
    }

    private boolean isConnected() {
        return mRfcommSocket!=null && mRfcommSocket.isConnected();
    }

    private void pingKeepAlive() {
        try {
            StatusEvent statusEvent = StatusEvent.getInstance();
            if(new Date().getTime() - statusEvent.timeLastSync.getTime() > 240_000) {
                pingStatus();
            } else {
                mSerialEngine.sendMessage(new MsgDummy());
            }
        } catch (Exception e) {
            log.error("err", e);
        }

    }
    private void pingStatus() {
        try {
            mSerialEngine.sendMessage(new MsgStatus());
            mSerialEngine.sendMessage(new MsgStatusBasic());
            mSerialEngine.sendMessage(new MsgStatusTempBasal());
            mSerialEngine.sendMessage(new MsgStatusTime());
//            mSerialEngine.sendMessage(new MsgStatusBolusExtended());



            StatusEvent statusEvent = StatusEvent.getInstance();
            PumpStatus pumpStatus = new PumpStatus();
            pumpStatus.remainBattery = statusEvent.remainBattery;
            pumpStatus.remainUnits = statusEvent.remainUnits;
            pumpStatus.currentBasal = statusEvent.currentBasal;
            pumpStatus.last_bolus_amount = statusEvent.last_bolus_amount;
            pumpStatus.last_bolus_time = statusEvent.last_bolus_time;
            pumpStatus.tempBasalInProgress = statusEvent.tempBasalInProgress;
            pumpStatus.tempBasalRatio = statusEvent.tempBasalRatio;
            pumpStatus.tempBasalRemainMin = statusEvent.tempBasalRemainMin;
            pumpStatus.tempBasalStart = statusEvent.tempBasalStart;
            pumpStatus.time = statusEvent.time;//Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
            statusEvent.timeLastSync = statusEvent.time;

            if(statusEvent.tempBasalInProgress==1) {
                try {

                    Dao<TempBasal, Long> daoTempBasals = MainApp.getDbHelper().getDaoTempBasals();

//                    TempBasal lastTempBasal = getTempBasalLast(daoTempBasals);
//                    if(lastTempBasal.timeEnd==null) {
//                        if(lastTempBasal.getPlannedTimeEnd().getTime() < statusEvent.tempBasalStart.getTime()) {
//                            lastTempBasal.timeEnd = statusEvent.tempBasalStart;
//                        } else {
//                            lastTempBasal.timeEnd = lastTempBasal.getPlannedTimeEnd();
//                        }
//                        daoTempBasals.update(lastTempBasal);
//                    }


                    TempBasal tempBasal = new TempBasal();
                    tempBasal.duration = statusEvent.tempBasalTotalSec / 60 / 60;
                    tempBasal.percent = statusEvent.tempBasalRatio;
                    tempBasal.timeStart = statusEvent.tempBasalStart;
                    tempBasal.timeEnd = null;
                    tempBasal.baseRatio = (int) (statusEvent.currentBasal*100);
                    tempBasal.tempRatio = (int) (statusEvent.currentBasal*100 * statusEvent.tempBasalRatio/100d);
                    log.debug("TempBasal in progress record "+tempBasal);
                    daoTempBasals.createOrUpdate(tempBasal);
                    broadcastTempBasal(tempBasal);
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                try {
                    Dao<TempBasal, Long> daoTempBasals = MainApp.getDbHelper().getDaoTempBasals();
                    TempBasal tempBasalLast = getTempBasalLast(daoTempBasals);
                    log.debug("tempBasalLast " + tempBasalLast);
                    if (tempBasalLast.timeEnd == null || tempBasalLast.timeEnd.getTime()>new Date().getTime()) {
                        tempBasalLast.timeEnd = new Date();
                        if(tempBasalLast.timeEnd.getTime()>tempBasalLast.getPlannedTimeEnd().getTime()) {
                            tempBasalLast.timeEnd = tempBasalLast.getPlannedTimeEnd();
                        }
                        log.debug("tempBasalLast updated to " + tempBasalLast);
                        daoTempBasals.update(tempBasalLast);
                        broadcastTempBasal(tempBasalLast);
                    }
                }catch (SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }

            try {
                Dao<Bolus, Long> daoBolus = MainApp.getDbHelper().getDaoBolus();
                Bolus bolus = new Bolus();
                bolus.timeStart = statusEvent.last_bolus_time;
                bolus.amount = statusEvent.last_bolus_amount;
                daoBolus.createOrUpdate(bolus);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }



            try {
                MainApp.getDbHelper().getDaoPumpStatus().createOrUpdate(pumpStatus);
            } catch (SQLException e) {
                log.error("SQLException",e);
            }
            synchronized (this) {
                this.notify();
            }
            mBus.post(statusEvent);

        } catch (Exception e) {
            log.error("err",e);
        }
    }

    public void tempBasal(int percent, int durationInHours) throws Exception {
        MsgTempBasalStart msg = new MsgTempBasalStart(percent, durationInHours);
        mSerialEngine.sendMessage(msg);

        pingStatus();
    }

    public void tempBasalOff() throws Exception {

        StatusEvent statusEvent = StatusEvent.getInstance();
        if(statusEvent.tempBasalInProgress==1) {
            try {
                Dao<TempBasal, Long> daoTempBasals = MainApp.getDbHelper().getDaoTempBasals();

                Date timeStart = statusEvent.tempBasalStart;
                TempBasal tempBasal = new TempBasal();
                tempBasal.timeStart = timeStart;

                tempBasal = daoTempBasals.queryForSameId(tempBasal);
                if (tempBasal == null) {
                    log.warn("tempBasal.timeStart not found " + timeStart);
                    tempBasal = getTempBasalLast(daoTempBasals);
                    log.warn("tempBasal.timeStart found (hope a good one)" + tempBasal.timeStart);
                }
                tempBasal.timeEnd = new Date();
                daoTempBasals.update(tempBasal);
                broadcastTempBasal(tempBasal);
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }

            MsgTempBasalStop msg = new MsgTempBasalStop();
            mSerialEngine.sendMessage(msg);
        }

        pingStatus();
    }

    private TempBasal getTempBasalLast(Dao<TempBasal, Long> daoTempBasals) throws SQLException {
        TempBasal tempBasal;QueryBuilder<TempBasal, Long> queryBuilder = daoTempBasals.queryBuilder();
        queryBuilder.orderBy("timeIndex",false);
        queryBuilder.limit(1l);
        PreparedQuery<TempBasal> preparedQuery = queryBuilder.prepare();
        tempBasal = daoTempBasals.queryForFirst(preparedQuery);

        log.info("tempBasal.timeStart found last in DB "+tempBasal.timeStart );

        return tempBasal;
    }

    public static int byteArrayToInt(byte[] bArr,int offset, int lenght) {
        switch (lenght) {
            case 1:
                return bArr[0+offset] & 255;
            case 2:
                return ((bArr[0+offset] & 255) << 8) + (bArr[1+offset] & 255);
            case 3:
                return (((bArr[2+offset] & 255) << 16) + ((bArr[1+offset] & 255) << 8)) + (bArr[0+offset] & 255);
            case 4:
                return ((((bArr[3+offset] & 255) << 24) + ((bArr[2+offset] & 255) << 16)) + ((bArr[1+offset] & 255) << 8)) + (bArr[0+offset] & 255);
            default:
                return -1;
        }
    }

    public void stop() {
        try {mInputStream.close();} catch (Exception e)  {log.debug(e.getMessage());}
        try {mOutputStream.close();} catch (Exception e) {log.debug(e.getMessage());}
        try {mRfcommSocket.close();} catch (Exception e) {log.debug(e.getMessage());}
        if(mSerialEngine!=null) mSerialEngine.stopIt();
    }

    public void bolus(int amount, BolusUI bolusUI) throws Exception {
        MsgBolusStart msg = new MsgBolusStart(amount);
        MsgBolusProgress progress = new MsgBolusProgress(bolusUI);
        MsgBolusStop stop = new MsgBolusStop(bolusUI);

        mSerialEngine.expectMessage(progress);
        mSerialEngine.expectMessage(stop);

        mSerialEngine.sendMessage(msg);
        while(!stop.stopped && bolusUI.bolusInProgress) {
            mSerialEngine.expectMessage(progress);
        }
        bolusUI.bolusFinished();

        pingStatus();
    }

    public void bolusStop( BolusUI bolusUI) throws Exception {
        MsgBolusStop stop = new MsgBolusStop(bolusUI);
        mSerialEngine.sendMessage(stop);
        while(!stop.stopped) {
            mSerialEngine.sendMessage(stop);
        }
        pingStatus();
    }

    public void carbsEntry(int amount) {
        Calendar time = Calendar.getInstance();
        MsgCarbsEntry msg = new MsgCarbsEntry(time, amount);
        mSerialEngine.sendMessage(msg);

        try {

            Dao<Carbs, Long> daoTempBasals = MainApp.getDbHelper().getDaoCarbs();
            Carbs carbs = new Carbs();
            carbs.timeStart = time.getTime();
            carbs.amount = amount;

            daoTempBasals.create(carbs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

    }

    public static void broadcastTempBasal(TempBasal tempBasal) {
        Intent intent = new Intent("danaR.action.TEMP_BASAL_DATA");

        Bundle bundle = new Bundle();

        bundle.putLong("timeStart", tempBasal.timeStart.getTime());
        bundle.putLong("timeEnd", tempBasal.getCurrentTimeEnd().getTime());
        bundle.putInt("baseRatio",tempBasal.baseRatio);
        bundle.putInt("tempRatio",tempBasal.tempRatio);
        bundle.putInt("percent",tempBasal.percent);

        intent.putExtras(bundle);
        MainApp.instance().getApplicationContext().sendBroadcast(intent);
    }
}
