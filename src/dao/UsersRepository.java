package dao;

import datastruct.dataobj.User;

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

    public void add(int userID,String password) throws SQLException{
        String sql="INSERT INTO users(user_id, password) VALUES (?,?)";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setInt(1,userID);
        preparedStatement.setString(2,password);
        preparedStatement.execute();
    }
    public void delete(int userID) throws SQLException{
        String sql="DELETE FROM users WHERE user_id=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setInt(1,userID);
        preparedStatement.execute();
    }
    public void update(int userID,String password) throws SQLException{
        String sql="UPDATE users SET password=? WHERE user_id=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setString(1,password);
        preparedStatement.setInt(2,userID);
        preparedStatement.execute();
    }
    public void update(User user) throws SQLException{
        update(user.getUserID(),user.getPassword());
    }
    public boolean queryExist(int userID,String password) throws SQLException{
        String sql="SELECT count(*) FROM users WHERE user_id=? AND password=?";
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setInt(1,userID);
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
