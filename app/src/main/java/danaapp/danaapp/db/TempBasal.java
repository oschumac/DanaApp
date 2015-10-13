package danaapp.danaapp.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import danaapp.danaapp.calc.IobCalc;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@DatabaseTable(tableName = "TempBasals")
public class TempBasal {
    public long getTimeIndex() {
        return (long) Math.ceil(timeStart.getTime() / 60000d );
    }

    public void setTimeIndex(long timeIndex) {
        this.timeIndex = timeIndex;
    }

    @DatabaseField(id = true, useGetSet = true)
    public long timeIndex;


    @DatabaseField
    public Date timeStart;

    @DatabaseField
    public Date timeEnd;

    @DatabaseField
    public int percent;

    @DatabaseField
    public int duration;

    @DatabaseField
    public int baseRatio;

    @DatabaseField
    public int tempRatio;

    public Date getPlannedTimeEnd() {
         return new Date( timeStart.getTime()+60*60*1_000*duration );
    }

    public long getMsAgo() {
        return new Date().getTime() - timeStart.getTime();
    }

    public IobCalc.Iob calcIob() {
        IobCalc.Iob iob = new IobCalc.Iob();

        long msAgo = getMsAgo();
        Calendar startAdjusted = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startAdjusted.setTime(this.timeStart);
        int minutes = startAdjusted.get(Calendar.MINUTE);
        minutes = minutes % 4;
        if(startAdjusted.get(Calendar.SECOND)>0 && minutes==0) {
            minutes+=4;
        }
        startAdjusted.add(Calendar.MINUTE,minutes);
        startAdjusted.set(Calendar.SECOND,0);

        IobCalc iobCalc = new IobCalc();
        iobCalc.setTime(new Date());
        iobCalc.setAmount(-1.0d*(baseRatio- tempRatio)/15.0d/100.0d);

        long timeStartTime = startAdjusted.getTimeInMillis();
        Date currentTimeEnd = timeEnd;
        if(currentTimeEnd==null) {
            currentTimeEnd = new Date();
            if(getPlannedTimeEnd().getTime()<currentTimeEnd.getTime()) {
                currentTimeEnd = getPlannedTimeEnd();
            }
        }
        for (long time = timeStartTime;time< currentTimeEnd.getTime();time+=4*60_000) {
            Date start = new Date(time);

            iobCalc.setTimeStart(start);
            iob.plus(iobCalc.invoke());
        }

        return iob;
    }

    public Date getCurrentTimeEnd() {
        Date tempBasalTimePlannedEnd = getPlannedTimeEnd();
        return new Date(
                timeEnd !=null ?
                        timeEnd.getTime() :
                        tempBasalTimePlannedEnd.getTime() < new Date().getTime() ?
                                tempBasalTimePlannedEnd.getTime() :
                                timeStart.getTime()+60*60_000*duration );
    }

    @Override
    public String toString() {
        return "TempBasal{" +
                "timeIndex=" + timeIndex +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", percent=" + percent +
                ", duration=" + duration +
                ", baseRatio=" + baseRatio +
                ", tempRatio=" + tempRatio +
                '}';
    }
}
