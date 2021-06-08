package top.cfl.cflwork.service;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.dao.SysFileDao;
import top.cfl.cflwork.dao.SysFileLogDao;
import top.cfl.cflwork.pojo.ExcelInfo;
import top.cfl.cflwork.pojo.SysFileLog;
import top.cfl.cflwork.util.ExcelStyleUtil;
import top.cfl.cflwork.util.SequenceId;
import top.cfl.cflwork.pojo.ExcelExport;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;
import java.util.Map;
import static top.cfl.cflwork.util.ObjectUtil.beanToMap;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import org.apache.commons.lang3.StringUtils;
import static top.cfl.cflwork.util.CommonUtil.camelCaseName;
@Service
public class SysFileLogService {
    @Autowired
    private SysFileLogDao sysFileLogDao;
    @Autowired
    private SequenceId sequenceId;
    @Autowired
    private SysFileDao sysFileDao;

    @Transactional(readOnly = true)
    public SysFileLog findSysFileLogById(String id) {
        return sysFileLogDao.findSysFileLogById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysFileLog(SysFileLog sysFileLog) {
        sysFileLog.setId(sequenceId.nextId());
        sysFileLogDao.saveSysFileLog(sysFileLog);
    }
    @Transactional(readOnly = true)
    public List<SysFileLog> findSysFileLogListByCondition(SysFileLog sysFileLog) {
        return sysFileLogDao.findSysFileLogListByCondition(sysFileLog);
    }
    @Transactional(readOnly = true)
    public List<SysFileLog> findSysFileLogAllList(SysFileLog sysFileLog) {
        return sysFileLogDao.findSysFileLogAllList(sysFileLog);
    }
    @Transactional(readOnly = true)
    public SysFileLog findOneSysFileLogByCondition(SysFileLog sysFileLog) {
        return sysFileLogDao.findOneSysFileLogByCondition(sysFileLog);
    }
    @Transactional(readOnly = true)
    public long findSysFileLogCountByCondition(SysFileLog sysFileLog) {
        return sysFileLogDao.findSysFileLogCountByCondition(sysFileLog);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSysFileLog(SysFileLog sysFileLog) {
        sysFileLogDao.updateSysFileLog(sysFileLog);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysFileLog(String id) {
        sysFileLogDao.deleteSysFileLog(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysFileLogByCondition(SysFileLog sysFileLog) {
        sysFileLogDao.deleteSysFileLogByCondition(sysFileLog);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveSysFileLog(List<SysFileLog> sysFileLogs){
        sysFileLogs.forEach(sysFileLog -> sysFileLog.setId(sequenceId.nextId()));
        sysFileLogDao.batchSaveSysFileLog(sysFileLogs);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteSysFileLog(String rowData[]){
        sysFileLogDao.batchDeleteSysFileLog(rowData);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSysFileLog(SysFileLog sysFileLog){
        sysFileLogDao.batchUpdateSysFileLog(sysFileLog);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileLogModule(){
        List<SysFileLog> sysFileLogList = new ArrayList<>();
        ExportParams exportParams = new ExportParams("文件操作日志导入模板","文件操作日志");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysFileLog.class, sysFileLogList);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileLog(SysFileLog sysFileLog){
        List<SysFileLog> sysFileLogList = sysFileLogDao.findSysFileLogListByCondition(sysFileLog);
        ExportParams exportParams = new ExportParams("文件操作日志导出","文件操作日志列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysFileLog.class, sysFileLogList);
    }
    @Transactional(rollbackFor = Exception.class)
    public ExcelInfo uploadSysFileLog(MultipartFile file){
        ExcelInfo excelInfo = new ExcelInfo();
        List<SysFileLog> sysFileLogList = new ArrayList<>();
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);
        SysFileLog a = new SysFileLog();
        a.setAdminId(myId());
        try (InputStream is = file.getInputStream()){
            List<SysFileLog> list = ExcelImportUtil.importExcel(is,SysFileLog.class, params);
            int cnt = list.size();
            if(list.size()>0){
                list.stream().forEach(e->{
                    e.setId(sequenceId.nextId());
                    e.setAdminId(myId());
                });
                sysFileLogDao.batchSaveSysFileLog(list);
            }
            excelInfo.setSuccessCnt(cnt-excelInfo.getErrorNumber());
            excelInfo.setObjectList(sysFileLogList);
            excelInfo.setSumCnt(cnt);
            return excelInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Transactional(readOnly = true)
    public List<ExcelExport> getSysFileLogTableField() {
        return sysFileLogDao.getSysFileLogTableField();
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileLogByField(List<ExcelExport> excelExportList,SysFileLog sysFileLog) {
        List<SysFileLog> sysFileLogList = sysFileLogDao.findSysFileLogListByCondition(sysFileLog);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        sysFileLogList.stream().forEach(e->{
            list.add(beanToMap(e));
        });
        ExportParams exportParams = new ExportParams("文件操作日志导出","文件操作日志列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        List<ExcelExportEntity> beanList = new ArrayList<ExcelExportEntity>();
        excelExportList.stream().forEach(e->{
            if(StringUtils.isNotEmpty(e.getNewName())){
                beanList.add(new ExcelExportEntity(e.getNewName(), camelCaseName(e.getKey())));
            }else{
                beanList.add(new ExcelExportEntity(e.getName(), camelCaseName(e.getKey())));
            }
        });
        return ExcelExportUtil.exportExcel(exportParams, beanList, list);
    }

    @Transactional(readOnly = true)
    public List<SysFileLog> findSysFileLogByFileId(SysFileLog sysFileLog) {
        return sysFileLogDao.findSysFileLogByFileId(sysFileLog);
    }

    @Transactional(readOnly = true)
    public long findCountSysFileLogByFileId(SysFileLog sysFileLog) {
        return sysFileLogDao.findCountSysFileLogByFileId(sysFileLog);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delfileLogWhitoutID() {
        sysFileLogDao.delfileLogWhitoutID();
    }
}
