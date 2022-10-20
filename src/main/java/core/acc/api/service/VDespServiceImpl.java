package core.acc.api.service;

import core.acc.api.dao.VDespDao;
import core.acc.api.entity.VDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VDespServiceImpl implements VDespService {
    @Autowired
    private VDespDao dao;

    @Override
    public List<VDescription> getDesp(String compCode) {
        return dao.getDesp(compCode);
    }
}
