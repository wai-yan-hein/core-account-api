package core.acc.api.service;

import core.acc.api.entity.COAKey;
import core.acc.api.entity.ChartOfAccount;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface COAService {

    ChartOfAccount save(ChartOfAccount coa);

    ChartOfAccount save(ChartOfAccount coa, String opDate) throws Exception;

    ChartOfAccount findById(COAKey id);

    List<ChartOfAccount> getCOA(String compCode);

    List<ChartOfAccount> getCOAByGroup(String groupCode, String compCode);

    List<ChartOfAccount> getCOAByHead(String headCode, String compCode);    Boolean delete(COAKey key);

    List<ChartOfAccount> searchCOA(String str, Integer level, String compCode);

    List<ChartOfAccount> getCOATree(String compCode);

    List<ChartOfAccount> getCOAChild(String parentCode, String compCode);

    List<ChartOfAccount> getTraderCOA(String compCode);

    List<ChartOfAccount> getUpdatedCOA(LocalDateTime updatedDate);

    List<ChartOfAccount> unUpload();

    List<ChartOfAccount> findAllActive(String compCode);

    Date getMaxDate();

    List<ChartOfAccount> saveCOA(Integer busId, String compCode);
}
