package core.acc.api.service;

import core.acc.api.common.Util1;
import core.acc.api.dao.TraderGroupDao;
import core.acc.api.entity.TraderGroup;
import core.acc.api.entity.TraderGroupKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TraderGroupServiceImpl implements TraderGroupService {
    @Autowired
    private TraderGroupDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public TraderGroup save(TraderGroup group) {
        if (Util1.isNullOrEmpty(group.getKey().getGroupCode())) {
            group.getKey().setGroupCode(getGroupCode(group.getKey().getCompCode()));
        }
        return dao.save(group);
    }

    @Override
    public List<TraderGroup> getTraderGroup(String compCode) {
        return dao.getTraderGroup(compCode);
    }

    @Override
    public List<TraderGroup> unUpload() {
        return dao.unUpload();
    }

    @Override
    public TraderGroup findById(TraderGroupKey key) {
        return dao.findById(key);
    }

    private String getGroupCode(String compCode) {
        int seqNo = seqService.getSequence(0, "TraderGroup", "-", compCode);
        return String.format("%0" + 5 + "d", seqNo);
    }
}
