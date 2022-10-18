package core.acc.api.service;

import core.acc.api.dao.VRefDao;
import core.acc.api.entity.VRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VRefServiceImpl implements VRefService {
    @Autowired
    private VRefDao refDao;

    @Override
    public List<VRef> getRef(String compCode) {
        return refDao.getRef(compCode);
    }
}
