package top.cfl.cflwork.service;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.util.ObjectUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.dao.SysFileCollectDao;
import top.cfl.cflwork.dao.SysFileDao;
import top.cfl.cflwork.pojo.ExcelInfo;
import top.cfl.cflwork.pojo.SysFile;
import top.cfl.cflwork.pojo.SysFileCollect;
import top.cfl.cflwork.util.ExcelStyleUtil;
import top.cfl.cflwork.util.SequenceId;
import top.cfl.cflwork.pojo.ExcelExport;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static top.cfl.cflwork.interceptor.LoginInterceptor.currentAdmin;
import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;
import java.util.Map;
import static top.cfl.cflwork.util.ObjectUtil.beanToMap;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import org.apache.commons.lang3.StringUtils;
import static top.cfl.cflwork.util.CommonUtil.camelCaseName;
@Service
public class SysFileCollectService {
    @Autowired
    private SysFileCollectDao sysFileCollectDao;
    @Autowired
    private SequenceId sequenceId;
    @Autowired
    private SysFileDao sysFileDao;

    @Transactional(readOnly = true)
    public SysFileCollect findSysFileCollectById(String id) {
        return sysFileCollectDao.findSysFileCollectById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysFileCollect(SysFileCollect sysFileCollect) {
        sysFileCollect.setId(sequenceId.nextId());
        //保存加入信息
        sysFileCollect.setAdminId(myId());
        sysFileCollect.setAdminName(currentAdmin().getRealName());
        sysFileCollect.setIsCollect("1");
        sysFileCollectDao.saveSysFileCollect(sysFileCollect);
    }
    @Transactional(readOnly = true)
    public List<SysFileCollect> findSysFileCollectListByCondition(SysFileCollect sysFileCollect) {
        return sysFileCollectDao.findSysFileCollectListByCondition(sysFileCollect);
    }
    @Transactional(readOnly = true)
    public List<SysFileCollect> findSysFileCollectAllList(SysFileCollect sysFileCollect) {
        return sysFileCollectDao.findSysFileCollectAllList(sysFileCollect);
    }
    @Transactional(readOnly = true)
    public SysFileCollect findOneSysFileCollectByCondition(SysFileCollect sysFileCollect) {
        return sysFileCollectDao.findOneSysFileCollectByCondition(sysFileCollect);
    }
    @Transactional(readOnly = true)
    public long findSysFileCollectCountByCondition(SysFileCollect sysFileCollect) {
        return sysFileCollectDao.findSysFileCollectCountByCondition(sysFileCollect);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSysFileCollect(SysFileCollect sysFileCollect) {
        sysFileCollectDao.updateSysFileCollect(sysFileCollect);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysFileCollect(String id) {
        sysFileCollectDao.deleteSysFileCollect(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysFileCollectByCondition(SysFileCollect sysFileCollect) {
        sysFileCollectDao.deleteSysFileCollectByCondition(sysFileCollect);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveSysFileCollect(List<SysFileCollect> sysFileCollects){
        sysFileCollects.forEach(sysFileCollect -> sysFileCollect.setId(sequenceId.nextId()));
        sysFileCollectDao.batchSaveSysFileCollect(sysFileCollects);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteSysFileCollect(String rowData[]){
        sysFileCollectDao.batchDeleteSysFileCollect(rowData);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSysFileCollect(SysFileCollect sysFileCollect){
        sysFileCollectDao.batchUpdateSysFileCollect(sysFileCollect);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileCollectModule(){
        List<SysFileCollect> sysFileCollectList = new ArrayList<>();
        ExportParams exportParams = new ExportParams("用户收藏管理导入模板","用户收藏管理");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysFileCollect.class, sysFileCollectList);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileCollect(SysFileCollect sysFileCollect){
        List<SysFileCollect> sysFileCollectList = sysFileCollectDao.findSysFileCollectListByCondition(sysFileCollect);
        ExportParams exportParams = new ExportParams("用户收藏管理导出","用户收藏管理列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysFileCollect.class, sysFileCollectList);
    }
    @Transactional(rollbackFor = Exception.class)
    public ExcelInfo uploadSysFileCollect(MultipartFile file){
        ExcelInfo excelInfo = new ExcelInfo();
        List<SysFileCollect> sysFileCollectList = new ArrayList<>();
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);
        SysFileCollect a = new SysFileCollect();
        a.setAdminId(myId());
        try (InputStream is = file.getInputStream()){
            List<SysFileCollect> list = ExcelImportUtil.importExcel(is,SysFileCollect.class, params);
            int cnt = list.size();
            if(list.size()>0){
                list.stream().forEach(e->{
                    e.setId(sequenceId.nextId());
                    e.setAdminId(myId());
                });
                sysFileCollectDao.batchSaveSysFileCollect(list);
            }
            excelInfo.setSuccessCnt(cnt-excelInfo.getErrorNumber());
            excelInfo.setObjectList(sysFileCollectList);
            excelInfo.setSumCnt(cnt);
            return excelInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Transactional(readOnly = true)
    public List<ExcelExport> getSysFileCollectTableField() {
        return sysFileCollectDao.getSysFileCollectTableField();
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileCollectByField(List<ExcelExport> excelExportList,SysFileCollect sysFileCollect) {
        List<SysFileCollect> sysFileCollectList = sysFileCollectDao.findSysFileCollectListByCondition(sysFileCollect);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        sysFileCollectList.stream().forEach(e->{
            list.add(beanToMap(e));
        });
        ExportParams exportParams = new ExportParams("用户收藏管理导出","用户收藏管理列表");
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
    public List<SysFile> findSysFileCollectsCreate(SysFileCollect sysFileCollect) {
        List<SysFile> list=new ArrayList<>();
        List<SysFileCollect> sysFileCollectList = sysFileCollectDao.findSysFileCollectListByCondition(sysFileCollect);
        sysFileCollectList.stream().forEach(e->{
            SysFile sysFile = sysFileDao.findSysFileById(e.getFileId());
            if (ObjectUtil.isNotNull(sysFile)){
                //是否删除
                if (sysFile.getIsDelete().equals("0"))
                list.add(sysFile);
            }
        });
        return list;
    }
    @Transactional(readOnly = true)
    public Long findSysFileCollectsCreateCount(SysFileCollect sysFileCollect){
        return sysFileCollectDao.findSysFileCollectsCreateCount(sysFileCollect);
    }
}
