package dao;

import datastruct.dataobj.UserInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by huangzhengyue on 9/20/16.
 */
public class UsersRepository extends Repository{
    public UsersRepository(Connection dbConnection) {
        super(dbConnection);
    }

    public void add(String userName,String password) throws SQLException{
        String sql="INSERT INTO users(user_name, password) VALUES (?,?)";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setString(1,userName);
        preparedStatement.setString(2,password);
        preparedStatement.execute();
    }
    public void delete(String userName) throws SQLException{
        String sql="DELETE FROM users WHERE user_name=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setString(1,userName);
        preparedStatement.execute();
    }
    public void update(String userName,String password) throws SQLException{
        String sql="UPDATE users SET password=? WHERE user_name=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setString(1,password);
        preparedStatement.setString(2,userName);
        preparedStatement.execute();
    }
    public void update(UserInfo userInfo) throws SQLException{
        update(userInfo.getUserName(), userInfo.getPassword());
    }
    public boolean queryExist(String userName,String password) throws SQLException{
        String sql="SELECT count(*) FROM users WHERE user_name=? AND password=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setString(1,userName);
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
