package core.acc.api.service;

import core.acc.api.entity.COAKey;
import core.acc.api.entity.ChartOfAccount;

import java.util.List;

public interface COAService {

    ChartOfAccount save(ChartOfAccount coa) throws Exception;

    ChartOfAccount save(ChartOfAccount coa, String opDate) throws Exception;

    ChartOfAccount findById(COAKey id);

    List<ChartOfAccount> getCOA(String compCode);

    int delete(String code, String compCode);

    List<ChartOfAccount> searchCOA3(String str, String compCode);

    List<ChartOfAccount> getCOATree(String compCode);

    List<ChartOfAccount> getCOAChild(String parentCode, String compCode);

}
