package core.acc.api.dao;

import core.acc.api.entity.VRef;

import java.util.List;

public interface VRefDao {
    List<VRef> getRef(String compCode);
}
