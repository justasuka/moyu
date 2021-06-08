package top.cfl.cflwork.service.dd;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.cfl.cflwork.dao.dd.SysDdDao;
import top.cfl.cflwork.pojo.dd.SysDd;
import top.cfl.cflwork.util.ExcelStyleUtil;
import top.cfl.cflwork.util.SequenceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SysDdService {
    @Autowired
    private SysDdDao sysDdDao;
    @Autowired
    private SequenceId sequenceId;

    @Transactional(readOnly = true)
    public SysDd findSysDdById(String id) {
        return sysDdDao.findSysDdById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysDd(SysDd sysDd) {
        sysDd.setId(sequenceId.nextId());
        sysDdDao.saveSysDd(sysDd);
    }
    @Transactional(readOnly = true)
    public List<SysDd> findSysDdListByCondition(SysDd sysDd) {
        return sysDdDao.findSysDdListByCondition(sysDd);
    }
    @Transactional(readOnly = true)
    public List<SysDd> findSysDdAllList(SysDd sysDd) {
        return sysDdDao.findSysDdAllList(sysDd);
    }
    @Transactional(readOnly = true)
    public SysDd findOneSysDdByCondition(SysDd sysDd) {
        return sysDdDao.findOneSysDdByCondition(sysDd);
    }
    @Transactional(readOnly = true)
    public long findSysDdCountByCondition(SysDd sysDd) {
        return sysDdDao.findSysDdCountByCondition(sysDd);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSysDd(SysDd sysDd) {
        sysDdDao.updateSysDd(sysDd);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysDd(String id) {
        sysDdDao.deleteSysDd(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysDdByCondition(SysDd sysDd) {
        sysDdDao.deleteSysDdByCondition(sysDd);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveSysDd(List<SysDd> sysDds){
        sysDds.forEach(sysDd -> sysDd.setId(sequenceId.nextId()));
        sysDdDao.batchSaveSysDd(sysDds);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteSysDd(String rowData[]){
        sysDdDao.batchDeleteSysDd(rowData);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSysDd(SysDd sysDd){
        sysDdDao.batchUpdateSysDd(sysDd);
    }

    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysDdModule(){
        List<SysDd> SysDdList = new ArrayList<>();
        ExportParams exportParams = new ExportParams("字典表导入模板","字典表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysDd.class, SysDdList);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysDd(SysDd SysDd){
        List<SysDd> SysDdList = sysDdDao.findSysDdListByCondition(SysDd);
        ExportParams exportParams = new ExportParams("字典表导出","字典表列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysDd.class, SysDdList);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<HashMap<String,String>> censysList(String code){
        return sysDdDao.censysList(code);
    }



}
