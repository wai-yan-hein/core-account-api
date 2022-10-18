package core.acc.api.dao;

import core.acc.api.entity.ChartOfAccount;
import core.acc.api.entity.VCOALv3;

import java.util.List;

public interface COADao {
    ChartOfAccount save(ChartOfAccount coa);

    ChartOfAccount findById(String id);

    List<ChartOfAccount> getCOA(String compCode);

    int delete(String code, String compCode);

    List<ChartOfAccount> getUnusedCOA(String compCode);

    List<ChartOfAccount> getCOAChild(String parentCode, String compCode);

    List<VCOALv3> getVCOALv3(String compCode);

    List<VCOALv3> getVCOACurrency(String compCode);

    VCOALv3 findByCode(String code);

    List<ChartOfAccount> getCOATree(String compCode);

    List<ChartOfAccount> getAllChild(String parent, String compCode);

}
