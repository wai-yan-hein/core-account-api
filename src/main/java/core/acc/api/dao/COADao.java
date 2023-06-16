package core.acc.api.dao;

import core.acc.api.entity.COAKey;
import core.acc.api.entity.ChartOfAccount;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface COADao {
    ChartOfAccount save(ChartOfAccount coa);

    ChartOfAccount findById(COAKey key);

    List<ChartOfAccount> getCOA(String compCode);

    Boolean delete(COAKey key);

    List<ChartOfAccount> getUnusedCOA(String compCode);

    List<ChartOfAccount> getCOAChild(String parentCode, String compCode);

    List<ChartOfAccount> searchCOA(String str, Integer level, String compCode);

    List<ChartOfAccount> getCOATree(String compCode);

    List<ChartOfAccount> getTraderCOA(String compCode);

    List<ChartOfAccount> getUpdatedCOA(LocalDateTime updatedDate);

    List<ChartOfAccount> unUpload();

    Date getMaxDate();

    List<ChartOfAccount> findAllActive(String compCode);

    List<ChartOfAccount> getCOAByGroup(String groupCode, String compCode);

    List<ChartOfAccount> getCOAByHead(String headCode, String compCode);
}
