package danaapp.danaapp.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class MsgCarbsEntry extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgCarbsEntry.class);

    public MsgCarbsEntry(Calendar time, int amount) {
        super("CMD_PUMPSET_HIS_S");
        SetCommand(SerialParam.CTRL_CMD_HIST);
        SetSubCommand(SerialParam.CTRL_CMD_HIST_SUB_ENTRY);

        SetParamByte((byte) (8 & 255)); //RecType
        SetParamByte((byte) (time.get(Calendar.YEAR) % 100));
        SetParamByte((byte) (time.get(Calendar.MONTH) + 1));
        SetParamByte((byte) (time.get(Calendar.DAY_OF_MONTH)));
        SetParamByte((byte) (time.get(Calendar.HOUR_OF_DAY)));
        SetParamByte((byte) (time.get(Calendar.MINUTE)));
        SetParamByte((byte) (time.get(Calendar.SECOND)));
        SetParamByte((byte) (67 & 255)); //??
        SetParamInt(amount);

        log.debug("CarbsEntry amount:"+amount+" time:"+time);
    }

    public MsgCarbsEntry(String cmdName) {
        super(cmdName);
    }

    public void handleMessage(byte[] bytes) {
        int result = DanaRMessages.byteArrayToInt(bytes, 0, 1);
        if(result!=1) {
            failed = true;
            log.error("Command response is not OK " + getMessageName());
        }
    }


}
