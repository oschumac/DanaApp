package danaapp.danaapp.comm;

import android.content.Intent;
import android.os.Bundle;
import danaapp.danaapp.MainApp;
import danaapp.danaapp.event.StatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class MsgStatusBolusExtended extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgStatusBolusExtended.class);

    public MsgStatusBolusExtended() {
        super("CMD_PUMP_EXPANS_INS_I");
        SetCommand(SerialParam.CTRL_CMD_STATUS);
        SetSubCommand(SerialParam.CTRL_SUB_STATUS_EXTBOLUS);

        StatusEvent ev = StatusEvent.getInstance();
//        ev.last_bolus_time = new Date(0,0,0);
//        ev.last_bolus_amount = 0;

    }

    public MsgStatusBolusExtended(String cmdName) {
        super(cmdName);
    }

    public void handleMessage(byte[] bytes) {

        int statusBolusExtendedInProgress = DanaRMessages.byteArrayToInt(bytes, 0, 1);
        int statusBolusExtendedDurationInHalfHours = DanaRMessages.byteArrayToInt(bytes, 1, 1);
        int statusBolusExtendedDurationInMinutes = statusBolusExtendedDurationInHalfHours * 30;

        double statusBolusExtendedPlannedAmount = DanaRMessages.byteArrayToInt(bytes, 2, 2) *0.01d;
        int statusBolusExtendedDurationSoFarInSecs = DanaRMessages.byteArrayToInt(bytes, 10 - 6, 3);
        int statusBolusExtendedDurationSoFarInMinutes = statusBolusExtendedDurationSoFarInSecs / 60;
        int statusBolusExtendedDeliveredSoFarIn750parts = DanaRMessages.byteArrayToInt(bytes, 6, 3);
        double statusBolusExtendedDeliveredSoFar = statusBolusExtendedDeliveredSoFarIn750parts / 750d;

        log.debug("statusBolusExtendedInProgress:"+statusBolusExtendedInProgress
                + " statusBolusExtendedDurationInMinutes:"+statusBolusExtendedDurationInMinutes
                + " statusBolusExtendedPlannedAmount:"+statusBolusExtendedPlannedAmount
                + " statusBolusExtendedDurationSoFarInMinutes:"+statusBolusExtendedDurationSoFarInMinutes
                + " statusBolusExtendedDeliveredSoFar:"+statusBolusExtendedDeliveredSoFar

        );

    }

}
