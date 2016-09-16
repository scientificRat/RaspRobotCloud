package datastruct;

import java.net.InetAddress;

/**
 * Created by huangzhengyue on 9/16/16.
 */
public class OnlineUser {
    public final int userID;
    public final InetAddress inetAddress;
    public final int port;
    public final String info;

    public OnlineUser(int userID, InetAddress inetAddress, int port, String info) {
        this.userID = userID;
        this.inetAddress = inetAddress;
        this.port = port;
        this.info = info;
    }
}
