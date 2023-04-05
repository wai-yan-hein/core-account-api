package core.acc.api.dao;

import core.acc.api.entity.COAKey;
import core.acc.api.entity.ChartOfAccount;

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

    List<ChartOfAccount> getAllChild(String parent, String compCode);

    List<ChartOfAccount> getTraderCOA(String compCode);

    List<ChartOfAccount> search(String updatedDate);

    List<ChartOfAccount> unUpload();

    Date getMaxDate();

    List<ChartOfAccount> findAllActive(String compCode);

    List<ChartOfAccount> getCOA(String headCode, String compCode);
}
