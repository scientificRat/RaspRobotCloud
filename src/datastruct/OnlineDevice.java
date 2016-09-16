package datastruct;

import java.net.InetAddress;

/**
 * Created by huangzhengyue on 9/16/16.
 */
public class OnlineDevice {
    public final int sessionID;
    public final InetAddress inetAddress;
    public final int port;
    public final String info;

    public OnlineDevice(int sessionID, InetAddress inetAddress, int port, String info) {
        this.sessionID = sessionID;
        this.inetAddress = inetAddress;
        this.port = port;
        this.info = info;
    }
}
