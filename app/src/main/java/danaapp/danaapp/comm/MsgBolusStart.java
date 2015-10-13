package danaapp.danaapp.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgBolusStart extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgBolusStart.class);

    public MsgBolusStart(int amount) {
        super("CMD_MEALINS_START_DATA");
        SetCommand(SerialParam.CTRL_CMD_BOLUS);
        SetSubCommand(SerialParam.CTRL_SUB_BOLUS_START);
        SetParamInt(amount);
    }

    public MsgBolusStart(String cmdName) {
        super(cmdName);
    }

    public void handleMessage(byte[] bytes) {
        int result = DanaRMessages.byteArrayToInt(bytes, 0, 1);
        if(result!=2) {
            failed = true;
            log.error("Command response is not OK " + getMessageName());
        }
    }


}
