package core.acc.api.service;

import core.acc.api.entity.TmpOpening;

import java.util.List;

public interface COAOpeningService {
    List<TmpOpening> getCOAOpening(String coaCode, String opDate,
                                   String clDate, int level, String curr,
                                   String compCode, List<String> department,
                                   Integer macId, String traderCode) throws Exception;
}
