package core.acc.api.dao;

import core.acc.api.entity.GlLog;
import core.acc.api.entity.GlLogKey;
import org.springframework.stereotype.Repository;

@Repository
public class GlLogDaoImpl extends AbstractDao<GlLogKey,GlLog> implements GlLogDao{
    @Override
    public GlLog save(GlLog log) {
        saveOrUpdate(log,log.getKey());
        return log;
    }
}
