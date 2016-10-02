package datastruct.dataobj;

/**
 * Created by huangzhengyue on 2016/10/2.
 */
public class Request {
    private String loginName;
    private String password;
    private String requestType;
    private String sessionID;
    private String connectingID;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getConnectingID() {
        return connectingID;
    }

    public void setConnectingID(String connectingID) {
        this.connectingID = connectingID;
    }
}
