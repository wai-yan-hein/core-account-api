package core.acc.api.service;

import core.acc.api.entity.VDesp;
import core.acc.api.entity.VRef;

import java.util.List;

public interface VDespService {
    List<VDesp> getDesp(String compCode);
}
