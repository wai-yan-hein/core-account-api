package core.acc.api.dao;

import core.acc.api.entity.VDescription;

import java.util.List;

public interface VDespDao {
    List<VDescription> getDesp(String compCode);
}
