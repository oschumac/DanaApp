package danaapp.danaapp.event;

public class ConnectionStatusEvent {
    public boolean sConnecting = false;
    public boolean sConnected = false;
    public int sConnectionAttemptNo =0;

    public ConnectionStatusEvent(boolean connecting, boolean connected, int connectionAttemptNo) {
        sConnecting = connecting;
        sConnected = connected;

        if(connectionAttemptNo!=0)
            sConnectionAttemptNo = connectionAttemptNo;
    }

    public ConnectionStatusEvent() {

    }
}
