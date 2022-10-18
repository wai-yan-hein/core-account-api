package core.acc.api.dao;

import core.acc.api.entity.COAOpening;
import core.acc.api.entity.TmpOpening;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class COAOpeningDaoImpl extends AbstractDao<String, COAOpening> implements COAOpeningDao {
    @Override
    public ResultSet getResult(String sql) {
        return getResultSet(sql);
    }

    @Override
    public void executeSql(String... sql) {
        execSQL(sql);
    }

    @Override
    public List<TmpOpening> getOpening(String coaCode, Integer macId) {
        String hsql = "select o from TmpOpening o where o.key.coaCode = '" + coaCode + "' and o.key.macId = " + macId + " ";
        return getSession().createQuery(hsql, TmpOpening.class).list();
    }
}
