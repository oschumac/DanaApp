package danaapp.danaapp.comm;

import danaapp.danaapp.MainApp;
import danaapp.danaapp.event.StatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class MsgStatusTime_InitialConnection extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgStatusTime_InitialConnection.class);

    public MsgStatusTime_InitialConnection() {
        super("CMD_PUMPINIT_TIME_INFO");
        SetCommand(SerialParam.CMD_PUMPINIT);
        SetSubCommand(SerialParam.CMD_PUMPINIT_TIME_INFO);
    }

    public MsgStatusTime_InitialConnection(String cmdName) {
        super(cmdName);
    }

    public void handleMessage(byte[] bytes) {
        Date time  =
                new Date(
                    100 + DanaRMessages.byteArrayToInt(bytes, 0, 1),
                    DanaRMessages.byteArrayToInt(bytes, 1, 1) - 1,
                    DanaRMessages.byteArrayToInt(bytes, 2, 1),
                    DanaRMessages.byteArrayToInt(bytes, 3, 1),
                    DanaRMessages.byteArrayToInt(bytes, 4, 1),
                    DanaRMessages.byteArrayToInt(bytes, 5, 1)
                );

        log.debug("time: "+time);

//        StatusEvent ev = StatusEvent.getInstance();
//        ev.time = time;
//        MainApp.bus().post(ev);

    }
}
