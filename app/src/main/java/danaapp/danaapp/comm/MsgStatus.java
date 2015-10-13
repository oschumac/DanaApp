package danaapp.danaapp.comm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import danaapp.danaapp.MainApp;
import danaapp.danaapp.event.StatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class MsgStatus extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgStatus.class);

    public MsgStatus() {
        super("CMD_PUMP_STATUS");
        SetCommand(SerialParam.CTRL_CMD_STATUS);
        SetSubCommand(SerialParam.CTRL_SUB_STATUS_PUMP);

        StatusEvent ev = StatusEvent.getInstance();
        ev.last_bolus_time = new Date(0,0,0);
        ev.last_bolus_amount = 0;

    }

    public MsgStatus(String cmdName) {
        super(cmdName);
    }

    public void handleMessage(byte[] bytes) {
        int daily_total = DanaRMessages.byteArrayToInt(bytes, 0, 3);
        Date last_bolus_time  =
                new Date(
                    100 + DanaRMessages.byteArrayToInt(bytes, 8, 1),
                    DanaRMessages.byteArrayToInt(bytes, 9, 1) - 1,
                    DanaRMessages.byteArrayToInt(bytes, 10, 1),
                    DanaRMessages.byteArrayToInt(bytes, 11, 1),
                    DanaRMessages.byteArrayToInt(bytes, 12, 1)
                );
        int last_bolus_amount = DanaRMessages.byteArrayToInt(bytes, 13, 2);

        broadCastLastBolus(last_bolus_time, last_bolus_amount);

        StatusEvent ev = StatusEvent.getInstance();
        ev.last_bolus_time = last_bolus_time;
        ev.last_bolus_amount = last_bolus_amount * 0.01d;

        int status_bolus_extended = DanaRMessages.byteArrayToInt(bytes, 9-6, 1);
        int extended_bolus_min = DanaRMessages.byteArrayToInt(bytes, 10-6, 2);
        int extended_bolus_rate = DanaRMessages.byteArrayToInt(bytes, 12-6, 2);

        log.debug("status_bolus_extended:"+status_bolus_extended
                + "extended_bolus_min:"+extended_bolus_min
                + "extended_bolus_rate:"+extended_bolus_rate);

        MainApp.bus().post(ev);

    }

    private void broadCastLastBolus(Date last_bolus_time, int last_bolus_amount) {
        Intent intent = new Intent("danaR.action.USER_EVENT_DATA");

        Bundle bundle = new Bundle();

        bundle.putLong("time", last_bolus_time.getTime());
        bundle.putInt("value",last_bolus_amount);

        intent.putExtras(bundle);
        MainApp.instance().getApplicationContext().sendBroadcast(intent);
    }
}
