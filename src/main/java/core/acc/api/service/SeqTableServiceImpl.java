/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.acc.api.service;


import core.acc.api.dao.SeqTableDao;
import core.acc.api.entity.SeqKey;
import core.acc.api.entity.SeqTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class SeqTableServiceImpl implements SeqTableService{
    
    @Autowired
    private SeqTableDao dao;
    
    @Override
    public SeqTable save(SeqTable st){
        st = dao.save(st);
        return st;
    }
    
    @Override
    public SeqTable findById(SeqKey id){
        return dao.findById(id);
    }
    
    @Override
    public List<SeqTable> search(String option, String period, String compCode){
        return dao.search(option, period, compCode);
    }
    
    @Override
    public SeqTable getSeqTable(String option, String period, String compCode){
        return dao.getSeqTable(option, period, compCode);
    }
    
    @Override
    public int delete(Integer id){
        return dao.delete(id);
    }
    
    @Override
    public int getSequence(Integer macId,String option, String period, String compCode){
        return dao.getSequence(macId,option, period, compCode);
    }
    
    @Override
    public List<SeqTable> findAll() {
        return dao.findAll();
    }
}
