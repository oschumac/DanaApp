package danaapp.danaapp.comm;

import android.os.Handler;
import danaapp.danaapp.CRC;

import java.util.Arrays;

public class DanaRMessage {
    private String messageName;
    private byte[] bufferW = new byte[512];
    private byte cursorW = (byte) 6;

    public boolean failed = false;

    public void SetCommand(byte cmd) {
        this.bufferW[4] = cmd;
    }

    public void SetSubCommand(byte subcmd) {
        this.bufferW[5] = subcmd;
    }

    public void SetParamByte(byte data) {
        this.bufferW[(int) this.cursorW++] = data;
    }

    public void SetParamInt(int data) {
        this.bufferW[(int) this.cursorW++] = (byte) (data >> 8 & (int) 0xFF);
        this.bufferW[(int) this.cursorW++] = (byte) (data & (int) 0xFF);
    }

    public byte[] getMessageBytes() {
        this.bufferW[0] = (byte) 126;
        this.bufferW[1] = (byte) 126;

        byte length = (byte) (this.cursorW - 3);

        this.bufferW[2] = length;
        this.bufferW[3] = (byte) 241;

        //Array.Copy((Array) this.bufferW, 4, (Array) new byte[(int) length], 0, (int) length);

        this.SetParamInt(CRC.getCrc16(this.bufferW, 3, length));

        this.bufferW[5 + (int) length] = (byte) 46;
        this.bufferW[6 + (int) length] = (byte) 46;

        return Arrays.copyOf(bufferW, length + 7);
    }

    public DanaRMessage(String cmdName) {
        this.messageName = cmdName;
    }

    public DanaRMessage() {
    }

    public String getMessageName() {
        return messageName;
    }

    public void handleMessage(byte[] bytes) {

    }

    public int getCommand() {
        int command = (bufferW[5] & 255) | ((bufferW[4] << 8) & 65280);
        return command;
    }

}
