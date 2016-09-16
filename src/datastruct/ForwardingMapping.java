package datastruct;

import java.net.InetAddress;

/**
 * Created by huangzhengyue on 9/16/16.
 */
public class ForwardingMapping {
    public final int userSessionID;
    public final InetAddress userAddress;
    public final int userPort;
    public final int deviceSessionID;
    public final InetAddress deviceAddress;
    public final int devicePort;

    public ForwardingMapping(int userSessionID, InetAddress userAddress, int userPort, int deviceSessionID, InetAddress deviceAddress, int devicePort) {
        this.userSessionID = userSessionID;
        this.userAddress = userAddress;
        this.userPort = userPort;
        this.deviceSessionID = deviceSessionID;
        this.deviceAddress = deviceAddress;
        this.devicePort = devicePort;
    }
}
