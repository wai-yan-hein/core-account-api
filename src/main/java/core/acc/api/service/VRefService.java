package core.acc.api.service;

import core.acc.api.entity.VRef;

import java.util.List;

public interface VRefService {
    List<VRef> getRef(String compCode);
}
