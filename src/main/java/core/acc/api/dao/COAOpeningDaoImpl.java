package core.acc.api.dao;

import core.acc.api.entity.COAOpening;
import core.acc.api.entity.OpeningKey;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;

@Repository
public class COAOpeningDaoImpl extends AbstractDao<String, COAOpening> implements COAOpeningDao {
    @Override
    public ResultSet getResult(String sql) {
        return getResultSet(sql);
    }

    @Override
    public boolean delete(OpeningKey key) {
        String sql = "update coa_opening set deleted = 1 where coa_op_id ='" + key.getOpId() + "' and '" + key.getCompCode() + "'";
        executeSql(sql);
        return true;
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
