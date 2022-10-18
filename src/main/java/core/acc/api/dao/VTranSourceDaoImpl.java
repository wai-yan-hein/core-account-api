package core.acc.api.dao;

import core.acc.api.entity.VTranSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VTranSourceDaoImpl extends AbstractDao<String, VTranSource> implements VTranSourceDao {
    @Override
    public List<VTranSource> getTranSource(String compCode) {
        String hsql = "select o from VTranSource o where o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }
}
