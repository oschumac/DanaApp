package danaapp.danaapp.comm;

import android.content.Intent;
import android.os.Bundle;
import danaapp.danaapp.MainApp;
import danaapp.danaapp.event.StatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class MsgStatusTime extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgStatusTime.class);

    public MsgStatusTime() {
        super("CMD_SETTING_V_TIME_I");
        SetCommand(SerialParam.SYNC_CMD_READ);
        SetSubCommand(SerialParam.SYNC_SUB_TIME);
    }

    public MsgStatusTime(String cmdName) {
        super(cmdName);
    }

    public void handleMessage(byte[] bytes) {
        Date time  =
                new Date(
                    100 + DanaRMessages.byteArrayToInt(bytes, 5, 1),
                    DanaRMessages.byteArrayToInt(bytes, 4, 1) - 1,
                    DanaRMessages.byteArrayToInt(bytes, 3, 1),
                    DanaRMessages.byteArrayToInt(bytes, 2, 1),
                    DanaRMessages.byteArrayToInt(bytes, 1, 1),
                    DanaRMessages.byteArrayToInt(bytes, 0, 1)
                );

        log.debug("time: "+time);

        StatusEvent ev = StatusEvent.getInstance();
        ev.time = time;
        MainApp.bus().post(ev);

    }
}
