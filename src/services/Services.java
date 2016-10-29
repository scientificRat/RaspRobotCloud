package services;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import dao.RaspDevicesRepository;
import dao.UsersRepository;
import datastruct.ForwardingTableCols;
import datastruct.OnlineDeviceInfo;
import datastruct.OnlineUser;
import datastruct.dataobj.DeviceInfo;
import exceptions.TCPServicesException;
import tcp.DeviceConnection;
import tcp.TCPConnection;
import tcp.UserConnection;
import tcp.UserNonBrowserClientConnection;
import utility.DBHelper;
import utility.UniqueIdGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huangzhengyue on 9/16/16.
 */
public class Services {

    //singleton
    private static Services ourInstance = new Services();

    //在线设备信息表
    private ConcurrentHashMap<DeviceConnection, OnlineDeviceInfo> onlineDevicesTable = new ConcurrentHashMap<>();
    //在线用户表 sessionID as index
    private ConcurrentHashMap<String, OnlineUser> onlineUserTable = new ConcurrentHashMap<>();
    //用户--设备连接转发表
    private ConcurrentHashMap<UserConnection, ForwardingTableCols> userConnectionForwardingTable = new ConcurrentHashMap<>();

    public static Services getInstance() {
        return ourInstance;
    }

    //disable public constructor
    private Services() {
    }

    /***************
     *
     *   设备登录
     *   (设备登录不同于用户登录，设备登录会保持连接，而用户登录返回session id)
     *
     * */
    public void deviceLogin(String deviceID, String password, DeviceConnection deviceConnection) throws TCPServicesException{
        //验证密码
        Connection dbConnection= DBHelper.getDBConnection();
        RaspDevicesRepository raspDevicesRepository = new RaspDevicesRepository(dbConnection);
        boolean exist;
        try {
            exist = raspDevicesRepository.queryExist(deviceID,password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new TCPServicesException("sql exception"+e.toString());
        }
        if(!exist){
            throw new TCPServicesException("deviceID or password error");
        }
        //插入在线设备表
        onlineDevicesTable.put(deviceConnection,new OnlineDeviceInfo(deviceID));
    }
    /****************************
     *
     *   用户登录
     *   (设备登录不同于用户登录，设备登录会保持连接，而用户登录返回session id)
     *
     * */
    public String userLogin(String userName, String password) throws TCPServicesException {
        //验证密码
        Connection dbConnection= DBHelper.getDBConnection();
        UsersRepository usersRepository = new UsersRepository(dbConnection);
        boolean exist;
        try {
            exist = usersRepository.queryExist(userName, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new TCPServicesException("sql exception"+e.toString());
        }
        if(!exist){
            throw new TCPServicesException("userName or password error");
        }
        //generate sessionID
        String sessionID = UniqueIdGenerator.generate();
        //插入在线用户表
        onlineUserTable.put(sessionID,new OnlineUser(userName));
        return sessionID;
    }
    /************
     *
     *   设备下线
     *
     * */
    public void deviceLogout(DeviceConnection deviceConnection) throws TCPServicesException{
        try {
            //如果设备已连用户端接则断开用户连接
            ArrayList<UserConnection> connectionArrayList=onlineDevicesTable.get(deviceConnection).forwardingConnections;
            connectionArrayList.forEach(k->{
                k.closeConnection();
                userConnectionForwardingTable.remove(k);
            });
            onlineDevicesTable.remove(deviceConnection);
        }catch (NullPointerException e){
            throw new TCPServicesException("no such device(null pointer)\n"+e.toString());
        }
    }

    /***********
     *
     *   用户下线
     *
     * */
    public void userLogout(String sessionID) throws TCPServicesException{
        try {
            onlineUserTable.remove(sessionID);
            userConnectionForwardingTable.forEach((k, v)->{
                if(v.userSessionID.equals(sessionID)){
                    userConnectionForwardingTable.remove(k);
                }
            });
        }catch (NullPointerException e){
            throw new TCPServicesException("no such user(null pointer)\n"+e.toString());
        }
    }

    /**
     * 查询在线设备列表
     * @param userSessionID 用户session id
     * @return 设备信息列表
     */
    //query online devices
    public ArrayList<DeviceInfo> queryOnlineDevices(String userSessionID) {
        ArrayList<DeviceInfo> deviceInfoArrayList = new ArrayList<>();
        OnlineUser onlineUser = onlineUserTable.get(userSessionID);
        if (onlineUser == null) {
            return deviceInfoArrayList;
        }
        onlineDevicesTable.forEach((k, v) -> {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceID(v.deviceID);
            deviceInfoArrayList.add(deviceInfo);
        });

        return deviceInfoArrayList;
    }


    /**
     * 请求连接设备
     * @param userSessionID
     * @param deviceID
     * @param userConnection
     * @throws TCPServicesException
     */
    public void connectDevice(String userSessionID, String deviceID, UserNonBrowserClientConnection userConnection) throws TCPServicesException{
        //check online
        if(!onlineUserTable.containsKey(userSessionID)){
            throw new TCPServicesException("user not login");
        }
        OnlineDeviceInfo onlineDevice =null;
        DeviceConnection deviceConnection = null;
        for (DeviceConnection connection: onlineDevicesTable.keySet()) {
            if(onlineDevicesTable.get(connection).deviceID.equals(deviceID)){
                onlineDevice = onlineDevicesTable.get(connection);
                deviceConnection = connection;
                break;
            }
        }
        if(onlineDevice==null){
            throw new TCPServicesException(("device not online"));
        }
        try{
            //加入两个转发表
            onlineDevice.forwardingConnections.add(userConnection);
            userConnectionForwardingTable.put(userConnection,new ForwardingTableCols(deviceConnection,userSessionID));
        }catch (NullPointerException e){
            throw new TCPServicesException("device not online (null pointer exception)\n"+e.toString());
        }
        //告知设备发送视频
        deviceConnection.sendStringData("{\"action\":\"startVideo\"}");
    }

    /**
     * 断开用户设备连接
     * @param userSessionID
     * @param deviceID
     * @param userConnection
     * @throws TCPServicesException
     */
    //detach the device
    public void detachDevice(String userSessionID, String deviceID, UserNonBrowserClientConnection userConnection) throws TCPServicesException {
        //check online
        if(!onlineUserTable.containsKey(userSessionID)){
            throw new TCPServicesException("user not login");
        }
        OnlineDeviceInfo onlineDevice =null;
        for (DeviceConnection connection: onlineDevicesTable.keySet()) {
            if(onlineDevicesTable.get(connection).deviceID.equals(deviceID)){
                onlineDevice = onlineDevicesTable.get(connection);
                break;
            }
        }
        if(onlineDevice==null){
            throw new TCPServicesException(("device not online"));
        }
        try {
            onlineDevice.forwardingConnections.remove(userConnection);
            userConnectionForwardingTable.remove(userConnection);
        }catch (NullPointerException e){
            throw new TCPServicesException(e.toString());
        }
    }

    /***************
     *
     *    转发   用户--->设备
     * */

    public void userToDeviceForwarding(UserNonBrowserClientConnection userConnection, byte[] head, byte[] data){
        TCPConnection destinationConnection = userConnectionForwardingTable.get(userConnection).forwardingToConnection;
        byte[] sendData = new byte[head.length+data.length];
        System.arraycopy(head,0,sendData,0,head.length);
        System.arraycopy(data,0,sendData,head.length,data.length);
        destinationConnection.sendForwardingData(sendData);
    }

    /***************
     *
     *    转发   设备--->用户
     * */
    public void deviceToUserForwarding(DeviceConnection deviceConnection,byte[] head,byte[] data){
        byte[] sendData = new byte[head.length+data.length];
        System.arraycopy(head,0,sendData,0,head.length);
        System.arraycopy(data,0,sendData,head.length,data.length);
        ArrayList<UserConnection> destinationConnectionArrayList = onlineDevicesTable.get(deviceConnection).forwardingConnections;
        destinationConnectionArrayList.forEach(connection->{
            connection.sendForwardingData(sendData);
        });
    }

}
