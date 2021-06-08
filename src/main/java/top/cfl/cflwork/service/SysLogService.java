package top.cfl.cflwork.service;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.cfl.cflwork.dao.SysLogDao;
import top.cfl.cflwork.pojo.SysLog;
import top.cfl.cflwork.util.ExcelStyleUtil;
import top.cfl.cflwork.util.SequenceId;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class SysLogService {
    @Autowired
    private SysLogDao sysLogDao;
    @Autowired
    private SequenceId sequenceId;

    @Transactional(readOnly = true)
    public SysLog findSysLogById(String id) {
        return sysLogDao.findSysLogById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysLog(SysLog sysLog) {
        sysLog.setId(sequenceId.nextId());
        sysLogDao.saveSysLog(sysLog);
    }
    @Transactional(readOnly = true)
    public List<SysLog> findSysLogListByCondition(SysLog sysLog) {
        return sysLogDao.findSysLogListByCondition(sysLog);
    }
    @Transactional(readOnly = true)
    public SysLog findOneSysLogByCondition(SysLog sysLog) {
        return sysLogDao.findOneSysLogByCondition(sysLog);
    }
    @Transactional(readOnly = true)
    public long findSysLogCountByCondition(SysLog sysLog) {
        return sysLogDao.findSysLogCountByCondition(sysLog);
    }
    @Transactional
    public void updateSysLog(SysLog sysLog) {
        sysLogDao.updateSysLog(sysLog);
    }
    @Transactional
    public void deleteSysLog(String id) {
        sysLogDao.deleteSysLog(id);
    }
    @Transactional
    public void deleteSysLogByCondition(SysLog sysLog) {
        sysLogDao.deleteSysLogByCondition(sysLog);
    }
    @Transactional
    public void batchSaveSysLog(List<SysLog> sysLogs){
        sysLogs.forEach(sysLog -> sysLog.setId(sequenceId.nextId()));
        sysLogDao.batchSaveSysLog(sysLogs);
    }
    @Transactional(isolation= Isolation.DEFAULT,propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public void deleteManySysLog(){
        sysLogDao.deleteManySysLog();
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysLog(SysLog sysLog){
        List<SysLog> sysLogList = sysLogDao.findSysLogListByCondition(sysLog);
        ExportParams exportParams = new ExportParams("功能描述：系统日志导出","功能描述：系统日志列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysLog.class, sysLogList);
    }
}
