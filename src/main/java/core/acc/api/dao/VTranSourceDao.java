package core.acc.api.dao;

import core.acc.api.entity.VTranSource;

import java.util.List;

public interface VTranSourceDao {
    List<VTranSource> getTranSource(String compCode);
}
