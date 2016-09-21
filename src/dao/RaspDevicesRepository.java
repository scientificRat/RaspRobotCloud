package dao;

import datastruct.dataobj.Device;
import datastruct.dto.DeviceInfo;

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

    public void add(int deviceID,String password) throws SQLException {
        String sql="INSERT INTO devices(device_id, password) VALUES (?,?)";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setInt(1,deviceID);
        preparedStatement.setString(2,password);
        preparedStatement.execute();
    }
    public void delete(int deviceID) throws SQLException{
        String sql="DELETE FROM devices WHERE device_id=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setInt(1,deviceID);
        preparedStatement.execute();
    }
    public void update(Device device) throws SQLException{
        String sql;
        PreparedStatement preparedStatement;

        //isEmpty 检查 密码不能为空
        if(device.getPassword()!=null && !device.getPassword().isEmpty()){
            sql="UPDATE devices SET password=? WHERE device_id=?";
            preparedStatement= dbConnection.prepareStatement(sql);
            preparedStatement.setString(1,device.getPassword());
            preparedStatement.setInt(2,device.getDeviceID());
            preparedStatement.execute();
        }

        if(device.getHardwareDescription()!=null){
            sql="UPDATE devices SET hardware_description=? WHERE device_id=?";
            preparedStatement= dbConnection.prepareStatement(sql);
            preparedStatement.setString(1,device.getHardwareDescription());
            preparedStatement.setInt(2,device.getDeviceID());
            preparedStatement.execute();
        }
    }

    public DeviceInfo queryOne(int deviceID) throws SQLException{
        String sql="SELECT * FROM devices WHERE device_id=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setInt(1,deviceID);
        ResultSet resultSet = preparedStatement.executeQuery();
        DeviceInfo deviceInfo= new DeviceInfo();
        if(resultSet.next()){
            deviceInfo.setInfo(resultSet.getString("hardware_description"));
        }
        return deviceInfo;
    }

    public ArrayList<DeviceInfo> queryAll() throws SQLException{
        String sql="SELECT * FROM devices";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<DeviceInfo> deviceInfoArrayList = new ArrayList<>();

        while (resultSet.next()){
            DeviceInfo deviceInfo= new DeviceInfo();
            deviceInfo.setInfo(resultSet.getString("hardware_description"));
            deviceInfoArrayList.add(deviceInfo);
        }
        return deviceInfoArrayList;
    }


    public boolean queryExist(int deviceID,String password) throws SQLException{
        String sql="SELECT count(*) FROM devices WHERE device_id=? AND password=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setInt(1,deviceID);
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
