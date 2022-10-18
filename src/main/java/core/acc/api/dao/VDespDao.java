package core.acc.api.dao;

import core.acc.api.entity.VDesp;

import java.util.List;

public interface VDespDao {
    List<VDesp> getDesp(String compCode);
}
