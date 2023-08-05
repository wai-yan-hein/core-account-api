package core.acc.api.dao;


import core.acc.api.entity.TraderGroup;
import core.acc.api.entity.TraderGroupKey;

import java.util.List;

public interface TraderGroupDao {
    TraderGroup save(TraderGroup group);

    List<TraderGroup> getTraderGroup(String compCode);

    List<TraderGroup> unUpload();

    TraderGroup findById(TraderGroupKey key);


}
