package core.acc.api.service;

import core.acc.api.entity.ChartOfAccount;
import core.acc.api.entity.VCOALv3;

import java.util.List;

public interface COAService {

    ChartOfAccount save(ChartOfAccount coa) throws Exception;

    ChartOfAccount save(ChartOfAccount coa, String opDate) throws Exception;

    ChartOfAccount findById(String id);

    List<ChartOfAccount> getCOA(String compCode);

    int delete(String code, String compCode);

    List<VCOALv3> getVCOALv3(String compCode);

    List<ChartOfAccount> getCOATree(String compCode);

    List<ChartOfAccount> getCOAChild(String parentCode, String compCode);

    List<VCOALv3> getVCOACurrency(String compCode);

    VCOALv3 findByCode(String code);
}
