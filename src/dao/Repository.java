package dao;

import java.sql.Connection;

/**
 * Created by huangzhengyue on 8/4/16.
 */
public abstract class Repository {

    protected Connection dbConnection=null;

    public Repository(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }
    //may be useless
    public void bindDBConnection(Connection dbConnection) {
        this.dbConnection=dbConnection;
    }

    //use in debug
    protected void assertConnectionValid(){
        if(this.dbConnection==null){
            throw new RuntimeException("代码错误,数据库未连接");
        }
    }

}
