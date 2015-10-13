package danaapp.danaapp.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgStatusBasic_InitialConnection extends MsgStatusBasic{
    private static Logger log = LoggerFactory.getLogger(MsgStatusBasic_InitialConnection.class);

    public MsgStatusBasic_InitialConnection(String cmdName) {
        super(cmdName);
    }

    public MsgStatusBasic_InitialConnection() {
        super("CMD_PUMPINIT_INIT_INFO");
        SetCommand(SerialParam.CMD_PUMPINIT);
        SetSubCommand(SerialParam.CMD_PUMPINIT_INIT_INFO);
    }
}
