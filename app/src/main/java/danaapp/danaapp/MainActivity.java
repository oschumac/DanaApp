package danaapp.danaapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.squareup.otto.Subscribe;

import danaapp.danaapp.bolus.BolusArrayAdapter;
import danaapp.danaapp.bolus.BolusDialogFragment;
import danaapp.danaapp.bolus.BolusUI;
import danaapp.danaapp.calc.IobCalc;
import danaapp.danaapp.carbs.CarbsDialogFragment;
import danaapp.danaapp.db.Bolus;
import danaapp.danaapp.db.DatabaseHelper;
import danaapp.danaapp.db.TempBasal;
import danaapp.danaapp.event.ConnectionStatusEvent;
import danaapp.danaapp.event.LowSuspendStatus;
import danaapp.danaapp.tempBasal.TempBasalArrayAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import danaapp.danaapp.event.StatusEvent;
import danaapp.danaapp.event.StopEvent;

public class MainActivity extends Activity
        implements BolusDialogFragment.Communicator, CarbsDialogFragment.Communicator {
    private static Logger log = LoggerFactory.getLogger(MainActivity.class);

    DecimalFormat formatNumber1place = new DecimalFormat("0.00");
    DateFormat formatDateToJustTime = new SimpleDateFormat("HH:mm");

    TextView uRemaining;
    TextView batteryStatus;
    TextView tempBasalRatio;
    TextView tempBasalRemain;
    TextView currentBasal;

    TextView lastBolusAmount;
    TextView lastBolusTime;
    TextView lastCheck;

    TextView iob;
    TextView basalIob;
    TextView connection;


    ListView bolusListView;
    ListView tempBasalListView;

    Button tbButton;
    Button carbsButton;
    NavigationView mNavigationView;

    DrawerLayout mDrawerLayout;

    static Handler mHandler;
    static private HandlerThread mHandlerThread;
    private BolusUI bolusUI;
    private TextView lowSuspendData;
    private TextView lowSuspendStatus;
    private TextView lowSuspendDataTextOpenAps;
    private Switch switchOpenAPS;
    private Switch switchLowSuspend;


    private void initNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener());
    }

    @Override
    protected void onResume() {
        super.onResume();

        log.debug("onResume");

        updateTempBasalUI();
        updateBolusUI();

        onStatusEvent(StatusEvent.getInstance());

        updateLowSuspendData();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.debug("onCreate");
        Iconify.with(new FontAwesomeModule());


        setContentView(R.layout.activity_main);

        initNavDrawer();

        bolusUI = new BolusUI(this);
        bolusUI.bolusInit();

        bolusListView = (ListView) findViewById(R.id.bolusListView);
        iob = (TextView) findViewById(R.id.iob);
        basalIob = (TextView) findViewById(R.id.basal_iob);
        updateBolusUI();



        tempBasalListView = (ListView) findViewById(R.id.tempBasalListView);
        updateTempBasalUI();




        if(mHandler==null) {
            mHandlerThread = new HandlerThread(MainActivity.class.getSimpleName() + "Handler");
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setupAlarmManager();
                log.debug("setupAlarmManager");
            }
        });

        registerBus();

        uRemaining = (TextView) findViewById(R.id.uRemaining);
        batteryStatus = (TextView) findViewById(R.id.batteryStatus);
        tempBasalRatio = (TextView) findViewById(R.id.tempBasalRatio);
        //((ArrayAdapter)tempBasalRatio.getAdapter()).set
        currentBasal = (TextView) findViewById(R.id.currentBasal);
        tempBasalRemain = (TextView) findViewById(R.id.tempBasalRemain);

        lastBolusAmount =   (TextView) findViewById(R.id.lastBolusAmount);
        lastBolusTime =     (TextView) findViewById(R.id.lastBolusTime);
        lastCheck =         (TextView) findViewById(R.id.lastCheck);
        connection =         (TextView) findViewById(R.id.connection);
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DanaConnection dc = MainApp.getDanaConnection();
                        dc.connectIfNotConnected("connect req from UI");
                    }}
                );
            }
        });

        lowSuspendData = (TextView) findViewById(R.id.lowSuspendData);
        lowSuspendDataTextOpenAps = (TextView) findViewById(R.id.lowSuspendStatusTextOpenAps);
        lowSuspendStatus = (TextView) findViewById(R.id.lowSuspendStatus);
        switchLowSuspend = (Switch) findViewById(R.id.switchLowSuspend);
        switchOpenAPS = (Switch) findViewById(R.id.switchOpenAPS);

        boolean openAPSenabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("OpenAPSenabled", false);
        switchOpenAPS.setChecked(openAPSenabled);
        switchOpenAPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("OpenAPSenabled",isChecked);
                editor.commit();

            }
        });

        boolean LowSuspendenabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("LowSuspendEnabled", false);
        switchLowSuspend.setChecked(LowSuspendenabled);
        switchLowSuspend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("LowSuspendEnabled",isChecked);
                editor.commit();

            }
        });

        tbButton = (Button) findViewById(R.id.buttonTB);

        tbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DanaConnection dc = MainApp.getDanaConnection();
                            dc.connectIfNotConnected("tempBasal UI Button");
                            if ("STOP".equals(tbButton.getText())) {
                                dc.tempBasalOff();
                            } else {
                                dc.tempBasal(0, 1);
                            }
                        } catch (Exception e) {
                            log.error("tempBasal", e);

                        }
                    }
                });
            }
        });

        carbsButton = (Button) findViewById(R.id.carbsButton);

        carbsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                CarbsDialogFragment carbsDialogFragment = new CarbsDialogFragment();
                carbsDialogFragment.show(manager, "CarbsDialog");
            }
        });
    }

    private void updateTempBasalUI() {
        List<TempBasal> tempBasalList = loadTempBasalsDB();

        IobCalc.Iob iobNum = getIobFromTempBasals(tempBasalList);
        basalIob.setText(
                "bIOB: "+formatNumber1place.format(iobNum.iobContrib) + " "
                + formatNumber1place.format(iobNum.activityContrib*Settings.insSensitivity));

        TempBasalArrayAdapter tempBasalArrayAdapter = new TempBasalArrayAdapter(this,tempBasalList);
        tempBasalListView.setAdapter(tempBasalArrayAdapter);

        if(tempBasalList.size()>=1) {
            View item = tempBasalArrayAdapter.getView(0, null, tempBasalListView);
            item.measure(0, 0);
            int tempBasalArrayAdapterCount = tempBasalArrayAdapter.getCount();
            if(tempBasalArrayAdapterCount>3) tempBasalArrayAdapterCount = 3;
            int height = (int) (tempBasalArrayAdapterCount * item.getMeasuredHeight());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            tempBasalListView.setLayoutParams(params);
        }
    }

    public static IobCalc.Iob getIobFromTempBasals(List<TempBasal> tempBasalList) {
        IobCalc.Iob iob = new IobCalc.Iob();

        Iterator<TempBasal> tempBasalIterator = tempBasalList.iterator();
        while(tempBasalIterator.hasNext()) {
            TempBasal tempBasal = tempBasalIterator.next();
            IobCalc.Iob calcIob = tempBasal.calcIob();
            if(tempBasal.getMsAgo()>4*60*60_000) {
//                tempBasalIterator.remove();
            }
            iob.plus(calcIob);
        }
        return iob;
    }

    @Nullable
    public static List<TempBasal> loadTempBasalsDB() {
        List<TempBasal> tempBasalList = null;

        try {
            Dao<TempBasal, Long> daoTempBasals = MainApp.getDbHelper().getDaoTempBasals();
            QueryBuilder<TempBasal, Long> queryBuilder = daoTempBasals.queryBuilder();
            queryBuilder.orderBy("timeIndex",false);
            queryBuilder.limit(20l);
            PreparedQuery<TempBasal> preparedQuery = queryBuilder.prepare();
            tempBasalList = daoTempBasals.query(preparedQuery);

        } catch (SQLException e) {
            log.debug(e.getMessage(),e);
        }
        return tempBasalList;
    }

    private void updateBolusUI() {
        List<Bolus> bolusList = null;

        bolusList = loadBoluses();
        IobCalc.Iob iobNum = getIobFromBoluses(bolusList);

        iob.setText(
                "IOB: "+formatNumber1place.format(iobNum.iobContrib) + " "
                + formatNumber1place.format(iobNum.activityContrib));

        BolusArrayAdapter bolusArrayAdapter = new BolusArrayAdapter(this,bolusList);
        bolusListView.setAdapter(bolusArrayAdapter);

//        if(bolusArrayAdapter.getCount() > 0){
            View item = bolusArrayAdapter.getView(0, null, bolusListView);
            item.measure(0, 0);
        int itemsToDisplay = bolusArrayAdapter.getCount() > 3 ? 3 : bolusArrayAdapter.getCount();
        int height = (int) (itemsToDisplay * item.getMeasuredHeight());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            bolusListView.setLayoutParams(params);
//        }

    }

    public static IobCalc.Iob getIobOpenAPSFromBoluses(List<Bolus> bolusList) {
        IobCalc.Iob iob= new IobCalc.Iob ();
        Iterator<Bolus> bolusIterator = bolusList.iterator();
        while(bolusIterator.hasNext()) {
            Bolus bolus = bolusIterator.next();
            IobCalc.Iob calcIob = bolus.calcIobOpenAPS();
            if(bolus.getMsAgo()>5*60*60_000) {
                bolusIterator.remove();
            }
            iob= calcIob.plus(iob);
        }
        return iob;
    }

    public static IobCalc.Iob getIobFromBoluses(List<Bolus> bolusList) {
        IobCalc.Iob iob= new IobCalc.Iob ();
        Iterator<Bolus> bolusIterator = bolusList.iterator();
        while(bolusIterator.hasNext()) {
            Bolus bolus = bolusIterator.next();
            IobCalc.Iob calcIob = bolus.calcIob();
            if(bolus.getMsAgo()>5*60*60_000) {
                bolusIterator.remove();
            }
            iob= calcIob.plus(iob);
        }
        return iob;
    }

    public static List<Bolus> loadBoluses() {
        List<Bolus> bolusList = null;
        try {
            Dao<Bolus, Long> dao = MainApp.getDbHelper().getDaoBolus();
            QueryBuilder<Bolus, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("timeIndex",false);
            queryBuilder.limit(4l);
            PreparedQuery<Bolus> preparedQuery = queryBuilder.prepare();
            bolusList = dao.query(preparedQuery);
        } catch (SQLException e) {
            log.debug(e.getMessage(),e);
        }
        return bolusList;
    }

    private void registerBus() {
        try {
            MainApp.bus().unregister(this);
        } catch (RuntimeException x) {
            // Ignore
        }
        MainApp.bus().register(this);
    }

    private void chancelAlarmManager() {
        AlarmManager am = ( AlarmManager ) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent( "danaapp.danaapp.ReceiverKeepAlive.action.PING"  );
        PendingIntent pi = PendingIntent.getBroadcast( this, 0, intent, 0 );

        am.cancel(pi);
    }

    private void setupAlarmManager() {
        AlarmManager am = ( AlarmManager ) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent( "danaapp.danaapp.ReceiverKeepAlive.action.PING"  );
        PendingIntent pi = PendingIntent.getBroadcast( this, 0, intent, 0 );

        long interval = 30*60_000L;
        long triggerTime = SystemClock.elapsedRealtime() + interval;

        try {
            pi.send();
        } catch (PendingIntent.CanceledException e) {
        }

        am.cancel(pi);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), interval, pi);

        List<ResolveInfo> queryBroadcastReceivers = getPackageManager().queryBroadcastReceivers(intent, 0);

        log.debug("queryBroadcastReceivers "+queryBroadcastReceivers);

    }

    @Subscribe
    public void onStatusEvent(final StatusEvent ev) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uRemaining.setText(formatNumber1place.format(ev.remainUnits)+"u");
                updateBatteryStatus(ev);


                currentBasal.setText(formatNumber1place.format(ev.currentBasal)+"u/h");
                tempBasalRemain.setText((ev.tempBasalRemainMin/60)+ ":"+ev.tempBasalRemainMin%60);

                lastBolusAmount.setText(formatNumber1place.format(ev.last_bolus_amount));
                lastBolusTime.setText(formatDateToJustTime.format(ev.last_bolus_time));

                long checkMinsAgo = ((new Date()).getTime() - ev.timeLastSync.getTime()) / 60_000;
                if(checkMinsAgo>999) checkMinsAgo=999;
                lastCheck.setText(
                        formatDateToJustTime.format(ev.timeLastSync)
                        + " "
                        + checkMinsAgo
                        );
                if(ev.tempBasalRatio!=-1) {
                    tempBasalRatio.setText(ev.tempBasalRatio + "%");
                    tbButton.setText("STOP");
                } else {
                    tbButton.setText("TEMP");
                    tempBasalRatio.setText("100%");
                }

                updateLowSuspendData();
            }
        });
    }

    private void updateBatteryStatus(StatusEvent ev) {
        batteryStatus.setText("{fa-battery-"+(ev.remainBattery/25)+"}");
    }

    private void updateLowSuspendData() {
        LowSuspendStatus lowSuspendStatusRef = LowSuspendStatus.getInstance();
        lowSuspendData.setText(lowSuspendStatusRef.dataText);
        lowSuspendDataTextOpenAps.setText(lowSuspendStatusRef.lowSuspendDataTextOpenAps);
        lowSuspendStatus.setText(lowSuspendStatusRef.statusText);
    }

    @Override
    public void bolusDialogDeliver(final double amount) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                DanaConnection dc = MainApp.getDanaConnection();
                dc.connectIfNotConnected("bolusDialogDeliver");
                bolusUI.bolusStart(amount);
                try {
                    dc.bolus((int) (bolusUI.bolusAmount*100),bolusUI);
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        });


    }

    public void bolusStop() {
        DanaConnection dc = MainApp.getDanaConnection();
        dc.connectIfNotConnected("bolusStop");
        try {
            dc.bolusStop(bolusUI);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }




    @Override
    public void carbsDialogDeliver(final int amount) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                DanaConnection dc = MainApp.getDanaConnection();
                dc.connectIfNotConnected("carbsDialogDeliver");
                try {
                    dc.carbsEntry( amount);
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        });


    }

    @Subscribe
    public void onStatusEvent(final ConnectionStatusEvent c) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(c.sConnecting) {
                    connection.setText("{fa-bluetooth-b spin} "+c.sConnectionAttemptNo);
                } else {
                    if (c.sConnected) {
                        connection.setText("{fa-bluetooth}");
                    } else {
                        connection.setText("{fa-bluetooth-b}");
                    }
                }
            }
          }
        );

    }


    private class OnNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            mDrawerLayout.closeDrawers();
            menuItem.setChecked(true);
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                break;

                case R.id.nav_broadcast: {
                    List<TempBasal> tempBasalsList = loadTempBasalsDB();

                    for(TempBasal tempBasal:tempBasalsList) {
                        DanaConnection.broadcastTempBasal(tempBasal);
                    }

                    break;
                }

                case R.id.nav_backup: {

                    try {
                        File sd = Environment.getExternalStorageDirectory();
                        File data = Environment.getDataDirectory();

                        if (sd.canWrite()) {
                            String currentDBPath = "/data/danaapp.danaapp/databases/"+ DatabaseHelper.DATABASE_NAME;
                            String backupDBPath = DatabaseHelper.DATABASE_NAME;
                            File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDBPath);

                            if (currentDB.exists()) {
                                FileChannel src = new FileInputStream(currentDB).getChannel();
                                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                dst.transferFrom(src, 0, src.size());
                                src.close();
                                dst.close();
                            }
                        }
                    } catch (Exception e) {
                        log.error("Excpetion "+e.getMessage(),e);
                    }
                    break;
                }


//                case R.id.nav_test_alarm: {
//                    Intent alarmServiceIntent = new Intent(MainApp.instance().getApplicationContext(), ServiceAlarm.class);
//                    alarmServiceIntent.putExtra("alarmText","Connection error");
//                    MainApp.instance().getApplicationContext().startService(alarmServiceIntent);
//                    break;
//                }

                case R.id.nav_exit: {
                    log.debug("Exiting");
                    chancelAlarmManager();

                    MainApp.bus().post(new StopEvent());
                    MainApp.closeDbHelper();

                    finish();
                    System.runFinalization();
                    System.exit(0);

                    break;
                }

            }

            mDrawerLayout.closeDrawers();


            return true;
        }
    }


}
