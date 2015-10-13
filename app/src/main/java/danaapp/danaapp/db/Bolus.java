package danaapp.danaapp.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import danaapp.danaapp.calc.IobCalc;

import java.util.Date;

@DatabaseTable(tableName = "Bolus")
public class Bolus {
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
    public double amount;

    public IobCalc.Iob calcIobOpenAPS() {
        IobCalc calc = new IobCalc(timeStart,amount,new Date());
        calc.setBolusDiaTimesTwo();
        IobCalc.Iob iob = calc.invoke();

        return iob;
    }
    public IobCalc.Iob calcIob() {
        IobCalc calc = new IobCalc(timeStart,amount,new Date());
        IobCalc.Iob iob = calc.invoke();

        return iob;
    }

    public long getMsAgo() {
        return new Date().getTime() - timeStart.getTime();
    }
}
