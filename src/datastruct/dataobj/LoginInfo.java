package datastruct.dataobj;

/**
 * Created by huangzhengyue on 2016/10/30.
 */
public class LoginInfo {
    private boolean login;
    private String userName;
    private String sessionID;

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
