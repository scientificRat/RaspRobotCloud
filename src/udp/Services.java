package udp;

import datastruct.ForwardingMapping;
import datastruct.OnlineDevice;
import datastruct.OnlineUser;
import datastruct.dto.DeviceInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huangzhengyue on 9/16/16.
 */
public class Services {

    //singleton
    private static Services ourInstance = new Services();

    private Random random = new Random();

    //three table
    //id as index
    private ConcurrentHashMap<Integer, OnlineDevice> onlineDevicesTable = new ConcurrentHashMap<>();
    //sessionId as index
    private ConcurrentHashMap<Integer, OnlineUser> onlineUserHashTable = new ConcurrentHashMap<>();
    private Vector<ForwardingMapping> forwardingTable = new Vector<>();

    public static Services getInstance() {
        return ourInstance;
    }

    //disable public constructor
    private Services() {
    }

    //login logout
    //login will return the sessionID
    public synchronized int deviceLogin(int deviceID, String password, InetAddress inetAddress, int port, String info) {
        // TODO: 9/16/16 验证密码
        if (!password.equals("qwerty")) {
            return -1;
        }
        //generate sessionID
        int sessionID = random.nextInt(1000000000);
        while (onlineDevicesTable.containsKey(sessionID)) {
            sessionID = random.nextInt(1000000000);
        }
        //insert into table
        onlineDevicesTable.put(deviceID, new OnlineDevice(sessionID, inetAddress, port, info));
        return sessionID;
    }

    public synchronized int userLogin(int userID, String password, InetAddress inetAddress, int port, String info) {
        // TODO: 9/16/16 验证密码
        if (!password.equals("qwerty")) {
            return -1;
        }
        //generate sessionID
        int sessionID = random.nextInt(1000000000);
        while (onlineUserHashTable.containsKey(sessionID)) {
            sessionID = random.nextInt(1000000000);
        }
        //insert into table
        onlineUserHashTable.put(sessionID, new OnlineUser(userID, inetAddress, port, info));
        return sessionID;
    }

    public void deviceLogout(int sessionID) {
        onlineDevicesTable.values().removeIf((e) -> e.sessionID == sessionID);
        forwardingTable.removeIf((e) -> e.deviceSessionID == sessionID);
    }

    public void userLogout(int sessionID) {
        onlineUserHashTable.remove(sessionID);
        forwardingTable.removeIf((e) -> e.userSessionID == sessionID);
    }

    //query online devices
    public ArrayList<DeviceInfo> queryOnlineDevices(int userSessionID) {
        ArrayList<DeviceInfo> deviceInfoArrayList = new ArrayList<>();
        OnlineUser onlineUser = onlineUserHashTable.get(userSessionID);
        if (onlineUser == null) {
            return deviceInfoArrayList;
        }
        onlineDevicesTable.forEach((k, v) -> {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceID(k);
            deviceInfo.setInfo(v.info);
            deviceInfoArrayList.add(deviceInfo);
        });

        return deviceInfoArrayList;
    }

    //connect device with deviceID
    public boolean connectDevice(int userSessionID, int deviceID) {
        OnlineDevice onlineDevice = onlineDevicesTable.get(deviceID);
        OnlineUser onlineUser = onlineUserHashTable.get(userSessionID);
        if (onlineDevice == null || onlineUser == null) {
            return false;
        }
        return forwardingTable.add(
                new ForwardingMapping(userSessionID, onlineUser.inetAddress, onlineUser.port, onlineDevice.sessionID, onlineDevice.inetAddress, onlineDevice.port)
        );
    }

    //detach the device
    public boolean detachDevice(int userSessionID, int deviceID) {
        OnlineDevice onlineDevice = onlineDevicesTable.get(deviceID);
        OnlineUser onlineUser = onlineUserHashTable.get(userSessionID);
        if (onlineDevice == null || onlineUser == null) {
            return false;
        }
        return forwardingTable.removeIf((e) ->
                e.userSessionID == userSessionID && e.deviceSessionID == onlineDevice.sessionID
        );
    }

    public void deviceToUserForwarding(int deviceSessionID, DatagramSocket datagramSocket, DatagramPacket datagramPacket) throws IOException {
        for (ForwardingMapping e : forwardingTable) {
            if (e.deviceSessionID == deviceSessionID) {
                datagramPacket.setAddress(e.userAddress);
                datagramPacket.setPort(e.userPort);
                datagramSocket.send(datagramPacket);
            }
        }
    }

    public void userToDeviceForwarding(int userSessionID, DatagramSocket datagramSocket, DatagramPacket datagramPacket) throws IOException {
        for (ForwardingMapping e : forwardingTable) {
            if (e.userSessionID == userSessionID) {
                datagramPacket.setAddress(e.deviceAddress);
                datagramPacket.setPort(e.devicePort);
                datagramSocket.send(datagramPacket);
            }
        }
    }
}
