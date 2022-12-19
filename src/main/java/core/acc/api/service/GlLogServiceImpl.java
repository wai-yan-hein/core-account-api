package core.acc.api.service;

import core.acc.api.dao.GlLogDao;
import core.acc.api.entity.GlLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GlLogServiceImpl implements GlLogService {
    @Autowired
    private GlLogDao dao;

    @Override
    public GlLog save(GlLog log) {
        return dao.save(log);
    }
}
