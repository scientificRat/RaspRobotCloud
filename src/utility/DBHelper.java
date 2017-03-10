package utility;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by huangzhengyue on 8/1/16.
 */
public class DBHelper {
    //default database
    private static final String databaseUrl="jdbc:postgresql://139.219.199.122:5432/rasp_robot_cloud";
    private static final String databaseUserName="postgres";
    private static final String databaseUserPassword="199606128";

    //default
    public static Connection getDBConnection(){
        return getDBConnection(databaseUrl,databaseUserName,databaseUserPassword);
    }

    public static Connection getDBConnection(String databaseUrl,String databaseUserName,String databaseUserPassword){
        Connection dbConnection=null;
        Driver driver=new org.postgresql.Driver();
        try {
            DriverManager.registerDriver(driver);
            dbConnection=DriverManager.getConnection(databaseUrl,databaseUserName,databaseUserPassword);

        } catch (SQLException e) {
            for (Throwable t: e) {
                t.printStackTrace();
            }
        }

        return dbConnection;
    }
    //debug
    public static void main(String[] args) {
        try {
            Connection connection1=getDBConnection();
            Connection connection2=getDBConnection();
            Connection connection3=getDBConnection();
            Connection connection4=getDBConnection();
            connection1.close();
            connection2.close();
            connection3.close();
            connection4.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
