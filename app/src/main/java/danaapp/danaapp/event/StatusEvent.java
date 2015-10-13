package danaapp.danaapp.event;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import danaapp.danaapp.MainApp;
import danaapp.danaapp.db.PumpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;

public class StatusEvent {
    private static Logger log = LoggerFactory.getLogger(StatusEvent.class);

    private static StatusEvent statusEvent = null;

    public double remainUnits = 0;
    public int remainBattery = 0;

    public int tempBasalInProgress = 0;
    public int tempBasalRatio = -1;
    public int tempBasalRemainMin = 0 ;
    public int tempBasalTotalSec;
    public int tempBasalAgoSecs;
    public Date tempBasalStart;

    public Date time = new Date(0,0,0);
    public Date timeLastSync = new Date(0,0,0);


    public double currentBasal = 0;
    public double last_bolus_amount = 0;
    public Date last_bolus_time = new Date(0,0,0);

    public static StatusEvent getInstance() {
        if(statusEvent == null) {
            PumpStatus pumpStatus = null;
            statusEvent = new StatusEvent();

            loadLastStatus();

        }
        statusEvent.updateTempBasalData();


        return statusEvent;
    }

    private static void loadLastStatus() {
        PumpStatus pumpStatus;
        try {
            Dao<PumpStatus, Long> dao = MainApp.getDbHelper().getDaoPumpStatus();
            QueryBuilder<PumpStatus, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("timeIndex",false);
            queryBuilder.limit(1l);
            PreparedQuery<PumpStatus> preparedQuery = queryBuilder.prepare();
            pumpStatus = dao.queryForFirst(preparedQuery);

            statusEvent.remainBattery = pumpStatus.remainBattery;
            statusEvent.remainUnits = pumpStatus.remainUnits;
            statusEvent.currentBasal = pumpStatus.currentBasal;
            statusEvent.last_bolus_amount = pumpStatus.last_bolus_amount;
            statusEvent.last_bolus_time = pumpStatus.last_bolus_time;
            statusEvent.tempBasalInProgress = pumpStatus.tempBasalInProgress;
            statusEvent.tempBasalRatio = pumpStatus.tempBasalRatio;
            statusEvent.tempBasalRemainMin = pumpStatus.tempBasalRemainMin;
            statusEvent.tempBasalStart = pumpStatus.tempBasalStart;
            statusEvent.time = pumpStatus.time;
            statusEvent.timeLastSync = statusEvent.time;

            log.debug("loadLastStatus "+pumpStatus.toString() + " into "+statusEvent.toString());
        } catch (SQLException e) {
            log.error("SQLException",e);
        }
    }

    private void updateTempBasalData() {
        Date currentTime = new Date();
        long lastSyncMinAgo = (currentTime.getTime() - timeLastSync.getTime())/60_000;
        log.debug("updateTempBasalData: lastSyncMinAgo:"+lastSyncMinAgo +
                " tempBasalInProgress:"+tempBasalInProgress);

        if(lastSyncMinAgo>1 && tempBasalInProgress==1) {
            if(statusEvent.tempBasalStart != null) {

                long tempBasalMinAgo = (currentTime.getTime() - tempBasalStart.getTime()) / 60_000;
                tempBasalRemainMin = /* duration */ (int) (60 - tempBasalMinAgo);
                log.debug("updateTempBasalData: tempBasalMinAgo:" + tempBasalMinAgo +
                        " tempBasalStart:" + tempBasalStart +
                        " tempBasalRemainMin:" + tempBasalRemainMin);

                if (tempBasalRemainMin <= 0) {
                    tempBasalRemainMin = 0;
                    tempBasalInProgress = 0;
                    tempBasalRatio = -1;
                    log.debug("Temp basal expired");
                }
            }
        }
    }

    @Override
    public String toString() {
        return "StatusEvent{" +
                "remainUnits=" + remainUnits +
                ", remainBattery=" + remainBattery +
                ", tempBasalInProgress=" + tempBasalInProgress +
                ", tempBasalRatio=" + tempBasalRatio +
                ", tempBasalRemainMin=" + tempBasalRemainMin +
                ", tempBasalTotalSec=" + tempBasalTotalSec +
                ", tempBasalAgoSecs=" + tempBasalAgoSecs +
                ", tempBasalStart=" + tempBasalStart +
                ", time=" + time +
                ", timeLastSync=" + timeLastSync +
                ", currentBasal=" + currentBasal +
                ", last_bolus_amount=" + last_bolus_amount +
                ", last_bolus_time=" + last_bolus_time +
                '}';
    }
}
