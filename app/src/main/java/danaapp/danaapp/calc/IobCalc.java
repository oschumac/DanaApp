package danaapp.danaapp.calc;

import danaapp.danaapp.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class IobCalc {

    public static class Iob {
        public double iobContrib = 0;
        public double activityContrib = 0;

        public Iob plus(Iob iob) {
            iobContrib += iob.iobContrib;
            activityContrib += iob.activityContrib;
            return this;
        }
    }

    private static Logger log = LoggerFactory.getLogger(IobCalc.class);
    private Date timeStart;
    private double amount;

    private Date time;

    double dia = Settings.durationOfInsActionInHours;
    double diaRatio = 3.0 / dia;
    double peak = 75;
    double sens = 1;

    Iob iob = new Iob();

    public void setTime(Date time) {
        this.time = time;
    }

    public IobCalc() {

    }

    public IobCalc(Date timeStart, double amount, Date time) {
        this.timeStart = timeStart;
        this.amount = amount;
        this.time = time;
    }

    public void setBolusDiaTimesTwo() {
        diaRatio = (3.0 / dia)*2;;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getIob() {
        return iob.iobContrib;
    }

    public Iob invoke() {

        iob.activityContrib = 0;
        iob.iobContrib = 0;

        double insulin = amount;

        double minAgo = (diaRatio * (time.getTime() - timeStart.getTime())) / 1000 / 60;

        if(minAgo < 0 ) {
            return iob;
        }
        if (minAgo < peak) {
            double x = minAgo / 5 + 1;
            iob.iobContrib = insulin * (1 - 0.001852 * x * x + 0.001852 * x);
            iob.activityContrib = sens * insulin * (2 / dia / 60 / peak) * minAgo;

        } else if (minAgo < 180) {
            double x = (minAgo - 75) / 5;
            iob.iobContrib = insulin * (0.001323 * x * x - .054233 * x + .55556);
            iob.activityContrib = sens * insulin * (2 / dia / 60 - (minAgo - peak) * 2 / dia / 60 / (60 * dia - peak));
        }

//        log.debug("calc time: "+time + " eventTime:"+latestUEIRecord.timeLocal + " minAgo: "+minAgo + " iob:" + iobContrib + " ac:"  + activityContrib);

        return iob;
    }
}
