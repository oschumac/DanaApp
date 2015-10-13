package danaapp.danaapp.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import danaapp.danaapp.calc.IobCalc;

import java.util.Date;

@DatabaseTable(tableName = "Carbs")
public class Carbs {
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

    public double calcCob() {
        double cob = 0;


        return cob;
    }

}
