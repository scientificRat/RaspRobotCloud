package dao;

import datastruct.dataobj.DeviceInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by huangzhengyue on 9/20/16.
 */
public class RaspDevicesRepository extends Repository {
    public RaspDevicesRepository(Connection dbConnection) {
        super(dbConnection);
    }

    public void add(String deviceID,String password) throws SQLException {
        String sql="INSERT INTO devices(device_id, password) VALUES (?,?)";
        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)){
            preparedStatement.setString(1,deviceID);
            preparedStatement.setString(2,password);
            preparedStatement.execute();
        }
    }
    public void delete(String deviceID) throws SQLException{
        String sql="DELETE FROM devices WHERE device_id=?";
        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)){
            preparedStatement.setString(1,deviceID);
            preparedStatement.execute();
        }
    }
    public void update(DeviceInfo deviceInfo) throws SQLException{
        String sql;
        PreparedStatement preparedStatement;

        //isEmpty 检查 密码不能为空
        if(deviceInfo.getPassword()!=null && !deviceInfo.getPassword().isEmpty()){
            sql="UPDATE devices SET password=? WHERE device_id=?";
            preparedStatement= dbConnection.prepareStatement(sql);
            preparedStatement.setString(1, deviceInfo.getPassword());
            preparedStatement.setString(2, deviceInfo.getDeviceID());
            preparedStatement.execute();
            preparedStatement.close();
        }

        if(deviceInfo.getHardwareDescription()!=null){
            sql="UPDATE devices SET hardware_description=? WHERE device_id=?";
            preparedStatement= dbConnection.prepareStatement(sql);
            preparedStatement.setString(1, deviceInfo.getHardwareDescription());
            preparedStatement.setString(2, deviceInfo.getDeviceID());
            preparedStatement.execute();
            preparedStatement.close();

        }
    }

    public DeviceInfo queryOne(String deviceID) throws SQLException{
        String sql="SELECT * FROM devices WHERE device_id=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setString(1,deviceID);
        ResultSet resultSet = preparedStatement.executeQuery();
        DeviceInfo deviceInfo= new DeviceInfo();
        if(resultSet.next()){
            deviceInfo.setHardwareDescription(resultSet.getString("hardware_description"));
        }
        preparedStatement.close();
        return deviceInfo;
    }

    public ArrayList<DeviceInfo> queryAll() throws SQLException{
        String sql="SELECT * FROM devices";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<DeviceInfo> deviceInfoArrayList = new ArrayList<>();

        while (resultSet.next()){
            DeviceInfo deviceInfo= new DeviceInfo();
            deviceInfo.setDeviceID(resultSet.getString("device_id"));
            deviceInfo.setHardwareDescription(resultSet.getString("hardware_description"));
            deviceInfoArrayList.add(deviceInfo);
        }
        preparedStatement.close();
        return deviceInfoArrayList;
    }


    public boolean queryExist(String deviceID,String password) throws SQLException{
        String sql="SELECT count(*) FROM devices WHERE device_id=? AND password=?";
        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)){
            preparedStatement.setString(1,deviceID);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getInt(1)==1){
                    return true;
                }
            }
            return false;
        }
    }
}
