package services;

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
import utility.GeneralJsonBuilder;
import utility.UniqueIdGenerator;

import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huangzhengyue on 9/16/16.
 */

/**
 * 注意， 设备是必须一直保持连接  用户不一定，只要使用登录时获取的sessionID 就可以永久操作
 */
public class Services {

    //singleton
    private static Services ourInstance = new Services();

    //在线设备信息表 包含设备应该的转发信息
    private ConcurrentHashMap<DeviceConnection, OnlineDeviceInfo> onlineDevicesTable = new ConcurrentHashMap<>();
    //在线用户表 sessionID as index
    private ConcurrentHashMap<String, OnlineUser> onlineUserTable = new ConcurrentHashMap<>();
    //用户当前连接-->设备连接转发表, 包含用户session id
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
     *   (设备登录不同于用户登录，设备登录会保持连接，用户登录不会保持连接（这个函数不会体现这些操作，但其它地方处理要注意）
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

    /**
     * 用户登录
     *
     * (设备登录不同于用户登录，设备登录会保持连接，用户登录不会保持连接（这个函数不会体现这些操作，但其它地方处理要注意）
     *
     * @param userName 用户名
     * @param password 用户密码
     * @return 该连接的sessionID
     * @throws TCPServicesException
     */

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

    /**
     * 用户名直接登录,仅用于可信用户（比如已经通过http登录的用户）
     * @param userName 用户名
     * @return 该连接的sessionID
     */
    public String userLogin(String userName) {
        //generate sessionID
        String sessionID = UniqueIdGenerator.generate();
        //插入在线用户表
        onlineUserTable.put(sessionID,new OnlineUser(userName));
        return sessionID;
    }


    /**
     * 设备下线
     * @param deviceConnection 设备连接
     * @throws TCPServicesException
     */
    public void deviceLogout(DeviceConnection deviceConnection) throws TCPServicesException{
        try {
            //如果设备已连用户端接则断开用户连接
            ArrayList<UserConnection> connectionArrayList=onlineDevicesTable.get(deviceConnection).forwardingConnections;
            connectionArrayList.forEach(userConnection->{
                userConnection.closeConnection();
                userConnectionForwardingTable.remove(userConnection);
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
            //删除 用户在线表
            onlineUserTable.remove(sessionID);
            userConnectionForwardingTable.forEach(((userConnection, forwardingTableCols) -> {
                if(forwardingTableCols.userSessionID.equals(sessionID)){
                    //删除 设备-->用户转发规则
                    onlineDevicesTable.forEach(((deviceConnection, onlineDeviceInfo) -> {
                        onlineDeviceInfo.forwardingConnections.removeIf(userConnection1 -> {
                            if(userConnection==userConnection1){
                                return true;
                            }
                            else {
                                return false;
                            }
                        });
                    }));
                    //删除 用户-->设备转发规则
                    userConnectionForwardingTable.remove(userConnection);
                }
            }));
        }catch (NullPointerException e){
            throw new TCPServicesException("no such user(null pointer)\n"+e.toString());
        }
    }

    /**
     * 停止某个用户转发 （用于处理用户连接突然断开，但是没有退出登录）
     * @param userConnection
     * @throws TCPServicesException
     */
    public void stopUserForwarding(UserConnection userConnection) throws TCPServicesException{
        try {
            //删除 设备-->用户转发规则
            onlineDevicesTable.forEach((deviceConnection, onlineDeviceInfo) -> {
                onlineDeviceInfo.forwardingConnections.removeIf(userConnection1 -> {
                    if(userConnection==userConnection1){
                        return true;
                    }
                    else {
                        return false;
                    }
                });
            });

            //删除 用户-->设备转发规则
            userConnectionForwardingTable.remove(userConnection);


        }catch (NullPointerException e){
            throw new TCPServicesException("no such user(null pointer)\n"+e.toString());
        }
    }

    /**
     * 查询在线设备列表
     * @param userSessionID 用户session id 用于验证
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
     * 查询所有设备列表
     * @param userSessionID 用户session id 用于验证
     * @return 所有设备信息列表，包含其是否在线
     */
    public ArrayList<DeviceInfo> queryAllDevices(String userSessionID) throws SQLException {
        ArrayList<DeviceInfo> onlineDevices = queryOnlineDevices(userSessionID);
        //查询数据库(all devices)
        Connection dbConnection = DBHelper.getDBConnection();
        RaspDevicesRepository raspDevicesRepository = new RaspDevicesRepository(dbConnection);
        ArrayList<DeviceInfo> deviceInfoArrayList=raspDevicesRepository.queryAll();
        //设置设备是否在线信息
        deviceInfoArrayList.forEach(deviceInfo -> {
            boolean contained =false;
            for (int i = 0; i < onlineDevices.size(); i++) {
                if (onlineDevices.get(i).getDeviceID().equals(deviceInfo.getDeviceID())){
                    contained =true;
                    break;
                }
            }
            if(contained){
                deviceInfo.setOnline(true);
            }
            else {
                deviceInfo.setOnline(false);
            }
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
    public void connectDevice(String userSessionID, String deviceID, UserConnection userConnection) throws TCPServicesException{
        //check online
        if(!onlineUserTable.containsKey(userSessionID) && !userSessionID.equals("http")){
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
     * 用户断开设备连接
     * @param userSessionID
     * @param deviceID
     * @param userConnection
     * @throws TCPServicesException
     */
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

    public void userToDeviceForwarding(UserConnection userConnection, byte[] head, byte[] data) throws SocketException{
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
    public void deviceToUserForwarding(DeviceConnection deviceConnection,byte[] head,byte[] data) throws SocketException{
        byte[] sendData = new byte[head.length+data.length];
        System.arraycopy(head,0,sendData,0,head.length);
        System.arraycopy(data,0,sendData,head.length,data.length);
        ArrayList<UserConnection> destinationConnectionArrayList = onlineDevicesTable.get(deviceConnection).forwardingConnections;
        for (int i = 0; i < destinationConnectionArrayList.size(); i++) {
            destinationConnectionArrayList.get(i).sendForwardingData(sendData);
        }
    }

    /**
     * 服务器直接向设备发送数据，不管当前是谁操作
     * 【危险操作】谨慎使用
     * @param deviceID 设备号
     * @param data 数据
     * @throws TCPServicesException 异常
     */
    public void sendDeviceRawData(String deviceID,byte[] data) throws TCPServicesException{
        if(onlineDevicesTable.size()==0){
            throw new TCPServicesException("no such device");
        }
        DeviceConnection selectedDeviceConnection =null;
        for (Map.Entry<DeviceConnection,OnlineDeviceInfo> pair:onlineDevicesTable.entrySet()) {
            if(pair.getValue().deviceID .equals(deviceID)){
                selectedDeviceConnection = pair.getKey();
                break;
            }
        }
        try {
            selectedDeviceConnection.sendForwardingData(data);
        }catch (NullPointerException ne){
            throw new TCPServicesException("connection break");
        }
    }

    /**
     * 服务器直接向设备发送控制运动数据，不管当前是谁操作
     * 【危险操作】谨慎使用
     * @param deviceID 控制设备号
     * @param offsetX x偏移
     * @param offsetY y偏移
     * @throws TCPServicesException 异常
     */
    public void sendDeviceMovementCommand(String deviceID,float offsetX,float offsetY) throws TCPServicesException {
        ByteBuffer buf =ByteBuffer.allocate(13);
        // 一律按小端序发送
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte type ='c';
        buf.put(type);
        buf.putInt(8);
        buf.putFloat(offsetX);
        buf.putFloat(offsetY);
        byte[] sendData = buf.array();
        sendDeviceRawData(deviceID,sendData);
    }

}
