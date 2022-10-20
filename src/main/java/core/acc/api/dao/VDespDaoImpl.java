package core.acc.api.dao;

import core.acc.api.entity.VDescription;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VDespDaoImpl extends AbstractDao<String, VDescription> implements VDespDao {
    @Override
    public List<VDescription> getDesp(String compCode) {
        String hsql = "select o from VDesp o where o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }
}
