package danaapp.danaapp.comm;

import danaapp.danaapp.bolus.BolusUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgBolusProgress extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgBolusProgress.class);
    private BolusUI bolusUI;

    public int progress = 0;

    public MsgBolusProgress() {
        super("CMD_PUMP_THIS_REMAINDER_MEAL_INS");
        SetCommand(SerialParam.CTRL_CMD_STATUS);
        SetSubCommand(SerialParam.CTRL_SUB_STATUS_BOLUS_PROGRESS);
    }

    public MsgBolusProgress(String cmdName) {
        super(cmdName);
    }

    public MsgBolusProgress(BolusUI bolusUI) {
        this();
        this.bolusUI = bolusUI;
    }

    public void handleMessage(byte[] bytes) {
        progress = DanaRMessages.byteArrayToInt(bytes, 0, 2);
        log.debug("remaining "+progress);
        bolusUI.bolusDeliveredAmountSoFar = progress/100d;
        bolusUI.bolusDelivering();
    }


}
