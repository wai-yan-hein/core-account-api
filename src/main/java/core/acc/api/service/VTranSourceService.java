package core.acc.api.service;

import core.acc.api.entity.VTranSource;

import java.util.List;

public interface VTranSourceService {
    List<VTranSource> getTranSource(String compCode);
}
