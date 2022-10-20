package core.acc.api.service;

import core.acc.api.entity.VDescription;

import java.util.List;

public interface VDespService {
    List<VDescription> getDesp(String compCode);
}
