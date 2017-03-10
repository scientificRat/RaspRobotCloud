package dao;

import java.sql.Connection;

/**
 * Created by huangzhengyue on 2017/3/10.
 */
//TODO
public class CodeRepository extends Repository {
    public CodeRepository(Connection dbConnection) {
        super(dbConnection);
    }

    public void add(String userName, String deviceID, String code) {

    }

}
