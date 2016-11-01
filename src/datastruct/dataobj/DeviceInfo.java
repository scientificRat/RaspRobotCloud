package datastruct.dataobj;

/**
 * Created by huangzhengyue on 9/20/16.
 */
public class DeviceInfo {

    private String deviceID;
    private String password;
    private String hardwareDescription;
    private boolean online;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHardwareDescription() {
        return hardwareDescription;
    }

    public void setHardwareDescription(String hardwareDescription) {
        this.hardwareDescription = hardwareDescription;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
