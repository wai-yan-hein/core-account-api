package core.acc.api.dao;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
@Repository
public class ReportDaoImpl extends AbstractDao<String, Object> implements ReportDao {
    @Override
    public void exeSql(String... strSql) {
        execSql(strSql);
    }

    @Override
    public ResultSet executeSql(String sql) {
        return getResultSet(sql);
    }
}
