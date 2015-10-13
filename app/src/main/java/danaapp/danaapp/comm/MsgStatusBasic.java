package danaapp.danaapp.comm;

import danaapp.danaapp.MainApp;
import danaapp.danaapp.event.StatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgStatusBasic extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgStatusBasic.class);

    public MsgStatusBasic() {
        super("CMD_PUMP_INITVIEW_I");
        SetCommand(SerialParam.CTRL_CMD_STATUS);
        SetSubCommand(SerialParam.CTRL_SUB_STATUS_INIT);
    }

    public MsgStatusBasic(String cmdName) {
        super(cmdName);
    }


    public void handleMessage(byte[] bytes) {

        byte statusSuspend = (byte) DanaRMessages.byteArrayToInt(bytes,0,1); //0 - 0
        int num1 = DanaRMessages.byteArrayToInt(bytes,1,1); // 1 - 1
        long statusDailyPulse = (int) DanaRMessages.byteArrayToInt(bytes,2,3); // 2 - 4
        int statusDailyMaxRate = (int) DanaRMessages.byteArrayToInt(bytes,5,2);; // 5 - 6
        long statusRemainPulse = DanaRMessages.byteArrayToInt(bytes, 7, 3); // 7 - 9
        byte statusBolusBlock = (byte) DanaRMessages.byteArrayToInt(bytes, 10, 1); // 10
        byte statusBolusExtended = (byte) DanaRMessages.byteArrayToInt(bytes, 14, 1); // 14

        log.debug("remain: " + statusRemainPulse + " " +
                " statusSuspend:"+statusSuspend + " " + (statusRemainPulse / 750d) +
                " statusBolusExtended:"+statusBolusExtended +
                " statusDailyPulse:" + statusDailyPulse / 750d
                );

        double currentBasal =  DanaRMessages.byteArrayToInt(bytes, 11, 2) * 0.01;// resp[17] & 255 + resp[18] & 255;

        StatusEvent ev = StatusEvent.getInstance();

        ev.remainUnits = (double) (statusRemainPulse / 750d);
        ev.remainBattery = DanaRMessages.byteArrayToInt(bytes, 20, 1);
        ev.tempBasalInProgress = DanaRMessages.byteArrayToInt(bytes, 15, 1);

        ev.tempBasalRatio = DanaRMessages.byteArrayToInt(bytes, 13, 1) & 255;
        if(ev.tempBasalInProgress!=1) {
            ev.tempBasalRatio = -1;
        }

        ev.currentBasal = currentBasal;

        MainApp.bus().post(ev);
    }
}
