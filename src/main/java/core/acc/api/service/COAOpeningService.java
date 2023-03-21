package core.acc.api.service;

import core.acc.api.entity.COAOpening;
import core.acc.api.entity.OpeningKey;
import core.acc.api.entity.TmpOpening;

import java.util.List;

public interface COAOpeningService {
    COAOpening save(COAOpening coaOpening);

    boolean delete(OpeningKey key);

    TmpOpening getCOAOpening(String coaCode, String opDate, String clDate, String curr, String compCode, Integer macId, String traderCode) throws Exception;

    List<COAOpening> searchOpening(String deptCode, String curCode, String traderType, String coaLv1, String coaLv2, String coaLv3, String compCode);
}
