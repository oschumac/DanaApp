package danaapp.danaapp.comm;

public class SerialParam {
    public static byte RECORD_TYPE_BOLUS = (byte) 1;
    public static byte RECORD_TYPE_DAILY = (byte) 2;
    public static byte RECORD_TYPE_PRIME = (byte) 3;
    public static byte RECORD_TYPE_ERROR = (byte) 4;
    public static byte RECORD_TYPE_ALARM = (byte) 5;
    public static byte RECORD_TYPE_GLUCOSE = (byte) 6;
    public static byte RECORD_TYPE_CARBO = (byte) 8;
    public static byte RECORD_TYPE_REFILL = (byte) 9;
    public static byte RECORD_TYPE_BASALHOUR = (byte) 12;
    public static byte RECORD_TYPE_SUSPEND = (byte) 11;
    public static byte RECORD_TYPE_TB = (byte) 13;

    public static byte CTRL_CMD_BOLUS = (byte) 1;
    public static byte CTRL_SUB_BOLUS_START = (byte) 2;
    public static byte CTRL_SUB_BOLUS_STOP = (byte) 1;
    public static byte CTRL_SUB_BOLUS_START_NONE = (byte) 3;

    public static byte CTRL_CMD_STATUS = (byte) 2;
    public static byte CTRL_SUB_STATUS_INIT = (byte) 10;    // CMD_PUMP_INITVIEW_I 522
    public static byte CTRL_SUB_STATUS_CARBO = (byte) 4;
    public static byte CTRL_SUB_STATUS_TEMPBASAL = (byte) 5;
    public static byte CTRL_SUB_STATUS_EXTBOLUS = (byte) 7;
    public static byte CTRL_SUB_STATUS_PUMP = (byte) 11; // CMD_PUMP_STATUS 523
    public static byte CTRL_SUB_STATUS_BOLUS_PROGRESS = (byte) 2; // CMD_PUMP_THIS_REMAINDER_MEAL_INS

    public static byte CTRL_CMD_TB = (byte) 4;
    public static byte CTRL_SUB_TB_START = (byte) 1;
    public static byte CTRL_SUB_TB_STOP = (byte) 3;

    public static byte CTRL_CMD_EB = (byte) 4;
    public static byte CTRL_SUB_EB_START = (byte) 7;
    public static byte CTRL_SUB_EB_STOP = (byte) 6;

    public static byte CTRL_CMD_DB = (byte) 4;
    public static byte CTRL_SUB_DB_START = (byte) 8;

    public static byte CTRL_CMD_SUSPEND = (byte) 4;
    public static byte CTRL_SUB_SUSPEND_ON = (byte) 4;
    public static byte CTRL_SUB_SUSPEND_OFF = (byte) 5;

    public static byte CTRL_CMD_COMM = (byte) 48;
    public static byte CTRL_SUB_COMM_CONNECT = (byte) 1;
    public static byte CTRL_SUB_COMM_DISCONNECT = (byte) 2;

    public static byte DOWNLOAD_CMD = (byte) 49;
    public static byte DOWNLOAD_SUB_BOLUS = (byte) 1;
    public static byte DOWNLOAD_SUB_DAILY = (byte) 2;
    public static byte DOWNLOAD_SUB_PRIME = (byte) 3;
    public static byte DOWNLOAD_SUB_GLUCO = (byte) 4;
    public static byte DOWNLOAD_SUB_ALARM = (byte) 5;
    public static byte DOWNLOAD_SUB_ERROR = (byte) 6;
    public static byte DOWNLOAD_SUB_CARBO = (byte) 7;
    public static byte DOWNLOAD_SUB_REFILL = (byte) 8;
    public static byte DOWNLOAD_SUB_SUSPEND = (byte) 9;
    public static byte DOWNLOAD_SUB_BASALHOUR = (byte) 10;
    public static byte DOWNLOAD_SUB_TB = (byte) 11;

    public static byte SYNC_CMD_WRITE = (byte) 51;
    public static byte SYNC_CMD_READ = (byte) 50;
    public static byte SYNC_SUB_BOLUS = (byte) 1;
    public static byte SYNC_SUB_BASAL = (byte) 2;
    public static byte SYNC_SUB_GENER = (byte) 3;
    public static byte SYNC_SUB_CARBO = (byte) 4;
    public static byte SYNC_SUB_MAX = (byte) 5;
    public static byte SYNC_SUB_BASAL_PROFILE = (byte) 6;
    public static byte SYNC_SUB_SHIPPING = (byte) 7;
    public static byte SYNC_SUB_PWM = (byte) 8;
    public static byte SYNC_SUB_TIME = (byte) 10;
    public static byte SYNC_SUB_GLUCOMODE = (byte) 9;
    public static byte SYNC_SUB_BASAL_INDEX = (byte) 12;
    public static byte SYNC_SUB_OPTION = (byte) 11;
    public static byte SYNC_SUB_CIRCF = (byte) 13;

    public static byte CTRL_CMD_HIST = (byte) 4;
    public static byte CTRL_CMD_HIST_SUB_ENTRY = (byte) 2;

    public static byte CMD_PUMPINIT = (byte) 0x03;
    public static byte CMD_PUMPINIT_TIME_INFO = (byte) 0x01;
    public static byte CMD_PUMPINIT_BOLUS_INFO = (byte) 0x02;
    public static byte CMD_PUMPINIT_INIT_INFO = (byte) 0x03;
    public static byte CMD_PUMPINIT_OPTION = (byte) 0x04;

}