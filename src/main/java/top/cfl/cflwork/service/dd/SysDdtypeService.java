package top.cfl.cflwork.service.dd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.cfl.cflwork.dao.dd.SysDdtypeDao;
import top.cfl.cflwork.pojo.dd.SysDdtype;
import top.cfl.cflwork.util.SequenceId;

import java.util.List;

@Service
public class SysDdtypeService {
    @Autowired
    private SysDdtypeDao sysDdtypeDao;
    @Autowired
    private SequenceId sequenceId;

    @Transactional(readOnly = true)
    public SysDdtype findSysDdtypeById(String id) {
        return sysDdtypeDao.findSysDdtypeById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysDdtype(SysDdtype sysDdtype) {
        sysDdtype.setId(sequenceId.nextId());
        sysDdtypeDao.saveSysDdtype(sysDdtype);
    }
    @Transactional(readOnly = true)
    public List<SysDdtype> findSysDdtypeListByCondition(SysDdtype sysDdtype) {
        return sysDdtypeDao.findSysDdtypeListByCondition(sysDdtype);
    }
    @Transactional(readOnly = true)
    public SysDdtype findOneSysDdtypeByCondition(SysDdtype sysDdtype) {
        return sysDdtypeDao.findOneSysDdtypeByCondition(sysDdtype);
    }
    @Transactional(readOnly = true)
    public long findSysDdtypeCountByCondition(SysDdtype sysDdtype) {
        return sysDdtypeDao.findSysDdtypeCountByCondition(sysDdtype);
    }
    @Transactional
    public void updateSysDdtype(SysDdtype sysDdtype) {
        sysDdtypeDao.updateSysDdtype(sysDdtype);
    }
    @Transactional
    public void deleteSysDdtype(String id) {
        sysDdtypeDao.deleteSysDdtype(id);
    }
    @Transactional
    public void deleteSysDdtypeByCondition(SysDdtype sysDdtype) {
        sysDdtypeDao.deleteSysDdtypeByCondition(sysDdtype);
    }
    @Transactional
    public void batchSaveSysDdtype(List<SysDdtype> sysDdtypes){
        sysDdtypes.forEach(sysDdtype -> sysDdtype.setId(sequenceId.nextId()));
        sysDdtypeDao.batchSaveSysDdtype(sysDdtypes);
    }

}
