package core.acc.api.service;

import core.acc.api.common.ReturnObject;
import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.entity.VGl;

import java.util.List;

public interface GlService {
    Gl save(Gl gl) throws Exception;

    ReturnObject save(List<Gl> gl) throws Exception;

    Gl findByCode(GlKey key);

    boolean delete(Gl gl);

    List<VGl> search(String fromDate, String toDate, String desp, String srcAcc,
                     String acc, String curCode, String reference, String dept,
                     String refNo, String compCode, String tranSource,
                     String glVouNo, String traderCode);
}
