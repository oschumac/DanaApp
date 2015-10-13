package danaapp.danaapp.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgTempBasalStop extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgTempBasalStop.class);

    public MsgTempBasalStop() {
        super("CMD_PUMPSET_EXERCISE_S");
        SetCommand(SerialParam.CTRL_CMD_TB);
        SetSubCommand(SerialParam.CTRL_SUB_TB_STOP);
    }

    public MsgTempBasalStop(String cmdName) {
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
