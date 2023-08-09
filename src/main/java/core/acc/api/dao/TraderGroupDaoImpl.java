package core.acc.api.dao;

import core.acc.api.entity.TraderGroup;
import core.acc.api.entity.TraderGroupKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraderGroupDaoImpl extends AbstractDao<TraderGroupKey, TraderGroup> implements TraderGroupDao {
    @Override
    public TraderGroup save(TraderGroup group) {
        saveOrUpdate(group,group.getKey());
        return group;
    }

    @Override
    public List<TraderGroup> getTraderGroup(String compCode) {
        String hsql = "select o from TraderGroup o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<TraderGroup> unUpload() {
        String hsql = "select o from TraderGroup o where intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public TraderGroup findById(TraderGroupKey key) {
        return getByKey(key);
    }
}
