package core.acc.api.dao;

import java.sql.ResultSet;

public interface ReportDao {

    void exeSql(String... strSql);

    ResultSet executeSql(String sql);

}
