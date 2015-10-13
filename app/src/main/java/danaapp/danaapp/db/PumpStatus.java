package danaapp.danaapp.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "PumpStatus")
public class PumpStatus {
    public long getTimeIndex() {
        return (long) Math.ceil(time.getTime() / 60000d );
    }

    public void setTimeIndex(long timeIndex) {
        this.timeIndex = timeIndex;
    }

    @DatabaseField(id = true, useGetSet = true)
    public long timeIndex;

    @DatabaseField
    public Date time;

    @DatabaseField
    public double remainUnits = 0;

    @DatabaseField
    public int remainBattery = 0;

    @DatabaseField
    public double currentBasal = 0;

    @DatabaseField
    public int tempBasalInProgress = 0;

    @DatabaseField
    public int tempBasalRatio = 0;

    @DatabaseField
    public int tempBasalRemainMin = 0 ;

    @DatabaseField
    public Date last_bolus_time ;

    @DatabaseField
    public double last_bolus_amount = 0;

    @DatabaseField
    public Date tempBasalStart;

    @Override
    public String toString() {
        return "PumpStatus{" +
                "timeIndex=" + timeIndex +
                ", time=" + time +
                ", remainUnits=" + remainUnits +
                ", remainBattery=" + remainBattery +
                ", currentBasal=" + currentBasal +
                ", tempBasalInProgress=" + tempBasalInProgress +
                ", tempBasalRatio=" + tempBasalRatio +
                ", tempBasalRemainMin=" + tempBasalRemainMin +
                ", last_bolus_time=" + last_bolus_time +
                ", last_bolus_amount=" + last_bolus_amount +
                ", tempBasalStart=" + tempBasalStart +
                '}';
    }
}

