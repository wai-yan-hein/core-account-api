package core.acc.api.dao;

import java.sql.ResultSet;

public interface ReportDao {

    void execSQLRpt(String... strSql);

    ResultSet executeSql(String sql);

}
