package danaapp.danaapp.comm;

import android.support.annotation.NonNull;
import danaapp.danaapp.MainApp;
import danaapp.danaapp.event.StatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class MsgStatusTempBasal extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgStatusTempBasal.class);

    public MsgStatusTempBasal() {
        super("CMD_PUMP_EXERCISE_MODE");
        SetCommand(SerialParam.CTRL_CMD_STATUS);
        SetSubCommand(SerialParam.CTRL_SUB_STATUS_TEMPBASAL);
    }

    public MsgStatusTempBasal(String cmdName) {
        super(cmdName);
    }


    public void handleMessage(byte[] bytes) {
        int tempBasalInProgress = DanaRMessages.byteArrayToInt(bytes, 0, 1);
        int tempBasalPercent = DanaRMessages.byteArrayToInt(bytes, 1, 1);
        int tempBasalTotalSec = DanaRMessages.byteArrayToInt(bytes, 2, 1) * 60 * 60;
        int tempBasalAgo =   DanaRMessages.byteArrayToInt(bytes, 3, 3);
        int tempBasalRemainMin = (tempBasalTotalSec - tempBasalAgo) / 60;

        log.debug("tempBasalInProgress:"+tempBasalInProgress
                + " tempBasalPercent:"+tempBasalPercent
                + " tempBasalAgoSecs:"+tempBasalAgo);

        StatusEvent ev = StatusEvent.getInstance();
        ev.tempBasalRemainMin = tempBasalRemainMin;
        ev.tempBasalRatio = tempBasalPercent;
        ev.tempBasalInProgress = tempBasalInProgress;
        ev.tempBasalTotalSec = tempBasalTotalSec;
        ev.tempBasalAgoSecs = tempBasalAgo;
        ev.tempBasalStart = getDateFromTempBasalSecAgo(tempBasalAgo);
        if(tempBasalInProgress!=1) {
            ev.tempBasalRatio = -1;
            ev.tempBasalStart = null;
        }

        MainApp.bus().post(ev);
    }


    @NonNull
    private Date getDateFromTempBasalSecAgo(int tempBasalAgoSecs) {
        return new Date((long) (Math.ceil(new Date().getTime() / 1000d) - tempBasalAgoSecs) * 1000);
    }
}
