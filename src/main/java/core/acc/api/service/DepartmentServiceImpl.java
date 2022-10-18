package core.acc.api.service;

import core.acc.api.dao.DepartmentDao;
import core.acc.api.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public Department save(Department dept) {
        if (dept.getDeptCode() == null || dept.getDeptCode().isEmpty()) {
            Integer macId = dept.getMacId();
            String compCode = dept.getCompCode();
            String depCode = getDepCode(macId, compCode);
            dept.setDeptCode(depCode);
        }
        dept = dao.save(dept);
        return dept;
    }

    @Override
    public Department findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<Department> search(String code, String name, String compCode,
                                   String userCode, String parentId, boolean active) {
        return dao.search(code, name, compCode, userCode, parentId, active);
    }

    @Override
    public List<Department> getDepartmentTree(String compCode) {
        return dao.getDepartmentTree(compCode);
    }

    @Override
    public int delete(String code) {
        return dao.delete(code);
    }

    @Override
    public List<Department> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    private String getDepCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "DEP", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }
}
