package datastruct;

import tcp.TCPConnection;

/**
 * Created by huangzhengyue on 2016/10/1.
 */
public class ForwardingTableCols {
    //user public to speed up access
    public final TCPConnection forwardingToConnection;
    public final String userSessionID;

    public ForwardingTableCols(TCPConnection forwardingToConnection, String userSessionID) {
        this.forwardingToConnection = forwardingToConnection;
        this.userSessionID = userSessionID;
    }
}
