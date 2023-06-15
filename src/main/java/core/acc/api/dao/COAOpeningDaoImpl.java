package core.acc.api.dao;

import core.acc.api.entity.COAOpening;
import core.acc.api.entity.OpeningKey;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;

@Repository
public class COAOpeningDaoImpl extends AbstractDao<OpeningKey, COAOpening> implements COAOpeningDao {

    @Override
    public boolean delete(OpeningKey key) {
        String sql = "update coa_opening set deleted = true where coa_op_id ='" + key.getOpId() + "' and '" + key.getCompCode() + "'";
        executeAndResult(sql);
        return true;
    }

    @Override
    public COAOpening save(COAOpening op) {
        saveOrUpdate(op, op.getKey());
        return op;
    }

    @Override
    public void executeAndResult(String... sql) {
        execSql(sql);
    }
}
