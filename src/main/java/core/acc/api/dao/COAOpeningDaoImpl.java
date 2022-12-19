package core.acc.api.dao;

import core.acc.api.entity.COAOpening;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;

@Repository
public class COAOpeningDaoImpl extends AbstractDao<String, COAOpening> implements COAOpeningDao {
    @Override
    public ResultSet getResult(String sql) {
        return getResultSet(sql);
    }

    @Override
    public COAOpening save(COAOpening op) {
        persist(op);
        return op;
    }

    @Override
    public void executeSql(String... sql) {
        execSql(sql);
    }
}
