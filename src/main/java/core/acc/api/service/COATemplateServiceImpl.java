package core.acc.api.service;

import core.acc.api.dao.COATemplateDao;
import core.acc.api.entity.COATemplate;
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
    public List<COATemplate> getChild(Integer busId, String coaCode) {
        return dao.getChild(busId, coaCode);
    }
}