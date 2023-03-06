package core.acc.api.service;

import core.acc.api.entity.COAKey;
import core.acc.api.entity.ChartOfAccount;

import java.util.Date;
import java.util.List;

public interface COAService {

    ChartOfAccount save(ChartOfAccount coa) throws Exception;

    ChartOfAccount save(ChartOfAccount coa, String opDate) throws Exception;

    ChartOfAccount findById(COAKey id);

    List<ChartOfAccount> getCOA(String compCode);

    List<ChartOfAccount> getCOA(String headCode, String compCode);

    int delete(String code, String compCode);

    List<ChartOfAccount> searchCOA(String str, Integer level, String compCode);

    List<ChartOfAccount> getCOATree(String compCode);

    List<ChartOfAccount> getCOAChild(String parentCode, String compCode);

    List<ChartOfAccount> getTraderCOA(String compCode);

    List<ChartOfAccount> search(String updatedDate);

    List<ChartOfAccount> unUpload();

    Date getMaxDate();
}
