package danaapp.danaapp;

import android.app.ActivityManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Message;
import danaapp.danaapp.comm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SerialEngine extends Thread {
    private static Logger log = LoggerFactory.getLogger(SerialEngine.class);
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;

    private HashMap<Integer,DanaRMessage>  pendingMessages = new HashMap<Integer,DanaRMessage>();

    private boolean mRun = true;
    private byte[] readBuff = new byte[0];
    private BluetoothSocket mRfcommSocket;


    public SerialEngine(InputStream mInputStream, OutputStream mOutputStream, BluetoothSocket mRfcommSocket) {
        super("SerialEngine"); // Thread name
        this.mInputStream = mInputStream;
        this.mOutputStream = mOutputStream;
        this.mRfcommSocket = mRfcommSocket;

        MsgStatusBasic_InitialConnection msgStatusBasic_initialConnection= new MsgStatusBasic_InitialConnection();
        pendingMessages.put(msgStatusBasic_initialConnection.getCommand(), msgStatusBasic_initialConnection);

        MsgStatusTime_InitialConnection msgStatusTime_initialConnection = new MsgStatusTime_InitialConnection();
        pendingMessages.put(msgStatusTime_initialConnection.getCommand(), msgStatusTime_initialConnection);

        this.start();
    }

    public final void run() {
        while(mRun) {
            try {

                int available = mInputStream.available();
                byte[] responseMessageBytes = null;

                if (available>0) {
                    byte[] readBuffTmp = new byte[available];
                    int checkRead = mInputStream.read(readBuffTmp);
                    if(checkRead!=available) { // could be more?
                        throw new IOException("");
                    }

                    {
                        byte[] buffNew = new byte[readBuff.length + available];

                        System.arraycopy(readBuff, 0, buffNew, 0, readBuff.length);
                        int availableBytes = readBuffTmp.length;
                        System.arraycopy(readBuffTmp, 0, buffNew, readBuff.length, availableBytes);
                        readBuff = buffNew;
                    }

                    if(readBuff.length<3) { continue; }

                    while(readBuff.length>3) {

                        if (readBuff[0] == (byte) 126 && readBuff[1] == (byte) 126) {
                            int length = (readBuff[2] & 0xFF) + 7;
                            responseMessageBytes = new byte[length];

                            if (readBuff.length < length) {
                                continue;
                            }

                            if (readBuff[length - 3] == (byte) 0x2e && readBuff[length - 2] == (byte) 0x2e) {
                                log.error("err lenght="+length+" data "+toHexString(readBuff));
                            }

                            {
                                short crc = CRC.getCrc16(readBuff, 3, length - 7);
                                byte crcByte0 = (byte) (crc >> 8 & (int) 0xFF);
                                byte crcByte1 = (byte) (crc & (int) 0xFF);

                                byte crcByte0received = readBuff[length - 4];
                                byte crcByte1received = readBuff[length - 3];

                                if (crcByte0 == crcByte0received && crcByte1 == crcByte1received) {

                                } else {
                                    log.error("CRC Error" + String.format("%02x ", crcByte0) + String.format("%02x ", crcByte1) + String.format("%02x ", crcByte0received) + String.format("%02x ", crcByte1received));
                                }
                            }


                            System.arraycopy(readBuff, 0, responseMessageBytes, 0, length);
                            byte[] buffNew = new byte[readBuff.length - (length)];
                            System.arraycopy(readBuff, length, buffNew, 0, buffNew.length);
                            readBuff = buffNew;

                            int command = (responseMessageBytes[5] & 255) | ((responseMessageBytes[4] << 8) & 65280);

                            //DanaRMessage danaRMessage = DanaRMessages.sDanaRMessages.get(command);
                            DanaRMessage danaRMessage = pendingMessages.get(command);
                            if(danaRMessage==null) {
                                danaRMessage = DanaRMessages.sDanaRMessages.get(command);
                                log.warn("MSG unexpected " + danaRMessage.getMessageName() + " " + toHexString(responseMessageBytes));
                            } else {
                                log.debug("MSG " + danaRMessage.getMessageName() + " " + toHexString(responseMessageBytes));
                            }

                            danaRMessage.handleMessage(responseMessageBytes);

                            pendingMessages.remove(command);

                            synchronized (danaRMessage) {
                                danaRMessage.notify();
                            }
                        }
                    }
                }
            } catch (Throwable x) {
                if(x instanceof IOException || "socket closed".equals(x.getMessage())) {
                    log.info("Thread run "+x.getMessage());
                } else {
                    log.error("Thread run ", x);
                }

                mRun = false;
            }
            try {
                synchronized (this.mInputStream) {
                    this.mInputStream.wait(100);
                }
            } catch (InterruptedException e) {
            }
        }
        try {mInputStream.close();} catch (Exception e)  {log.debug(e.getMessage());}
        try {mOutputStream.close();} catch (Exception e) {log.debug(e.getMessage());}
        try {mRfcommSocket.close(); } catch (Exception e) {log.debug(e.getMessage());}
        try {System.runFinalization();} catch (Exception e) {log.debug(e.getMessage());}
    }

    public static String toHexString(byte[] value)
	{
		StringBuffer sb = new StringBuffer();

        int count = 0;
		for (byte element : value)
		{
			sb.append(String.format("%02x ", element));
			if(++count%4==0) sb.append(" ");
		}

		return sb.toString();
	}


    public void stopIt() {
        mRun = false;
    }

    public void expectMessage(DanaRMessage message) {
        pendingMessages.put(message.getCommand(),message);
    }

    public synchronized void sendMessage(DanaRMessage message) {
        pendingMessages.put(message.getCommand(),message);

        byte[] messageBytes = message.getMessageBytes();
        log.debug(" about to WriteBytes " + message.getMessageName() +  " "+ toHexString(messageBytes));
        try {
            while(this.mInputStream.available()>0) {
                log.debug("something still there");
                synchronized (this.mInputStream) {
                    this.mInputStream.notify();
                }
                Thread.sleep(100);
            }

            this.mOutputStream.write(messageBytes);
        } catch (Exception ex) {
            log.error("sendMessage",ex);
        }
        if(message instanceof MsgDummy) {
            return;
        }


        synchronized (this.mInputStream) {
            this.mInputStream.notify();
        }

        synchronized (message) {
            try {
                message.wait(5000);
            } catch (InterruptedException e) {
                log.error("sendMessage InterruptedException",e);
            }
        }
        if(pendingMessages.containsKey(message.getCommand())) {
            log.error("message lost "+message.getMessageName());
            pendingMessages.remove(message.getCommand());
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {

        }
    }
}


