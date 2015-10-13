package danaapp.danaapp.event;

import java.util.Date;

public class LowSuspendStatus {
    private static LowSuspendStatus instance = null;

    public Date last_time = new Date(0,0,0);
    public String statusText = "";
    public String dataText = "";
    public String lowSuspendDataTextOpenAps ="";

    public static LowSuspendStatus getInstance() {
        if(instance == null) {
            instance = new LowSuspendStatus();
        }
        return instance;
    }
}
