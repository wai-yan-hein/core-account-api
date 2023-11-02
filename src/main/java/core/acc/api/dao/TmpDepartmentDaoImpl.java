package core.acc.api.dao;

import core.acc.api.entity.TmpDepartment;
import core.acc.api.entity.TmpDepartmentKey;
import org.springframework.stereotype.Repository;

@Repository
public class TmpDepartmentDaoImpl extends AbstractDao<TmpDepartmentKey, TmpDepartment> implements TmpDepartmentDao{
    @Override
    public TmpDepartment save(TmpDepartment tmp) {
        saveOrUpdate(tmp,tmp.getKey());
        return tmp;
    }
}
