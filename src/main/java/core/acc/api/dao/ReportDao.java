package core.acc.api.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface ReportDao {

    void exeSql(String... strSql);

    ResultSet executeAndResult(String sql);
    ResultSet executeAndResult(String sql,Object... param);

}
