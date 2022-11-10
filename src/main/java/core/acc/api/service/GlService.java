package core.acc.api.service;

import core.acc.api.common.ReturnObject;
import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;

import java.util.List;

public interface GlService {
    Gl save(Gl gl) throws Exception;

    ReturnObject save(List<Gl> gl) throws Exception;

    Gl findByCode(GlKey key);

    boolean delete(GlKey key);

}
