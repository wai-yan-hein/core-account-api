package core.acc.api.service;

import core.acc.api.dao.COATemplateDao;
import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class COATemplateServiceImpl implements COATemplateService {
    @Autowired
    private COATemplateDao dao;

    @Override
    public COATemplate save(COATemplate obj) {
        return dao.save(obj);
    }

    @Override
    public List<COATemplate> getCOAChild(Integer busId, String coaCode) {
        return dao.getCOAChild(busId, coaCode);
    }

    @Override
    public List<COATemplate> getCOATemplateTree(Integer busId) {
        return dao.getCOATemplateTree(busId);
    }

    @Override
    public List<COATemplate> getCOATemplate(Integer busId) {
        return dao.getAllCOATemplate(busId);
    }

    @Override
    public COATemplate findById(COATemplateKey key) {
        return dao.findById(key);
    }
}
