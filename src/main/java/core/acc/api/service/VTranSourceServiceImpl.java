package core.acc.api.service;

import core.acc.api.dao.VTranSourceDao;
import core.acc.api.entity.VTranSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VTranSourceServiceImpl implements VTranSourceService {
    @Autowired
    private VTranSourceDao tranSourceDao;

    @Override
    public List<VTranSource> getTranSource(String compCode) {
        return tranSourceDao.getTranSource(compCode);
    }
}
