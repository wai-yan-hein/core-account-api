package core.acc.api.dao;

import core.acc.api.entity.VRef;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VRefDaoImpl extends AbstractDao<String, VRef> implements VRefDao {
    @Override
    public List<VRef> getRef(String compCode) {
        String hsql = "select o from VRef o where o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }
}
