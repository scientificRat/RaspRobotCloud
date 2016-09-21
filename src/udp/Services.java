package udp;

import dao.RaspDevicesRepository;
import dao.UsersRepository;
import datastruct.ForwardingMapping;
import datastruct.OnlineDevice;
import datastruct.OnlineUser;
import datastruct.dto.DeviceInfo;
import utility.DBHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
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
    private ConcurrentHashMap<Integer, OnlineUser> onlineUserTable = new ConcurrentHashMap<>();
    private Vector<ForwardingMapping> forwardingTable = new Vector<>();

    public static Services getInstance() {
        return ourInstance;
    }

    //disable public constructor
    private Services() {
    }

    //login logout
    //login will return the sessionID
    public int deviceLogin(int deviceID, String password, InetAddress inetAddress, int port, String info) {
        //验证密码
        Connection dbConnection= DBHelper.getDBConnection();
        RaspDevicesRepository raspDevicesRepository = new RaspDevicesRepository(dbConnection);
        try {
            if (!raspDevicesRepository.queryExist(deviceID,password)) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -3;
        }
        //generate sessionID
        Integer sessionID=-9;
        synchronized (sessionID){
            sessionID = random.nextInt(1000000000);
            while (onlineDevicesTable.containsKey(sessionID)) {
                sessionID = random.nextInt(1000000000);
            }
            //insert into table
            onlineDevicesTable.put(deviceID, new OnlineDevice(sessionID, inetAddress, port, info));
        }
        return sessionID;
    }

    public int userLogin(int userID, String password, InetAddress inetAddress, int port, String info) {
        //验证密码
        Connection dbConnection= DBHelper.getDBConnection();
        UsersRepository usersRepository = new UsersRepository(dbConnection);
        try {
            if (!usersRepository.queryExist(userID, password)) {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
        //generate sessionID
        Integer sessionID=-9;
        synchronized (sessionID){
            sessionID = random.nextInt(1000000000);
            while (onlineUserTable.containsKey(sessionID)) {
                sessionID = random.nextInt(1000000000);
            }
            //insert into table
            onlineUserTable.put(sessionID, new OnlineUser(userID, inetAddress, port, info));
        }
        return sessionID;
    }

    public void deviceLogout(int sessionID) {
        onlineDevicesTable.values().removeIf((e) -> e.sessionID == sessionID);
        forwardingTable.removeIf((e) -> e.deviceSessionID == sessionID);
    }

    public void userLogout(int sessionID) {
        onlineUserTable.remove(sessionID);
        forwardingTable.removeIf((e) -> e.userSessionID == sessionID);
    }

    //query online devices
    public ArrayList<DeviceInfo> queryOnlineDevices(int userSessionID) {
        ArrayList<DeviceInfo> deviceInfoArrayList = new ArrayList<>();
        OnlineUser onlineUser = onlineUserTable.get(userSessionID);
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

    //connect device with deviceID and return   [   sessionID   ]   of the device
    public int connectDevice(int userSessionID, int deviceID) {
        OnlineDevice onlineDevice = onlineDevicesTable.get(deviceID);
        OnlineUser onlineUser = onlineUserTable.get(userSessionID);
        if (onlineDevice == null || onlineUser == null) {
            return -1;
        }
        forwardingTable.add(new ForwardingMapping(userSessionID, onlineUser.inetAddress, onlineUser.port, onlineDevice.sessionID, onlineDevice.inetAddress, onlineDevice.port));
        return onlineDevice.sessionID;
    }

    //detach the device
    public boolean detachDevice(int userSessionID, int deviceID) {
        OnlineDevice onlineDevice = onlineDevicesTable.get(deviceID);
        OnlineUser onlineUser = onlineUserTable.get(userSessionID);
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
