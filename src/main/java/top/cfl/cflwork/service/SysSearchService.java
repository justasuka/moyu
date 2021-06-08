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
import top.cfl.cflwork.dao.SysSearchDao;
import top.cfl.cflwork.pojo.ExcelInfo;
import top.cfl.cflwork.pojo.SysSearch;
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
public class SysSearchService {
    @Autowired
    private SysSearchDao sysSearchDao;
    @Autowired
    private SequenceId sequenceId;

    @Transactional(readOnly = true)
    public SysSearch findSysSearchById(String id) {
        return sysSearchDao.findSysSearchById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysSearch(SysSearch sysSearch) {
        sysSearch.setId(sequenceId.nextId());
        sysSearch.setAdminId(myId());
        sysSearch.setAdminName(currentAdmin().getRealName());
        sysSearch.setCount("1");
        sysSearchDao.saveSysSearch(sysSearch);
    }
    @Transactional(readOnly = true)
    public List<SysSearch> findSysSearchListByCondition(SysSearch sysSearch) {
        return sysSearchDao.findSysSearchListByCondition(sysSearch);
    }
    @Transactional(readOnly = true)
    public List<SysSearch> findSysSearchAllList(SysSearch sysSearch) {
        return sysSearchDao.findSysSearchAllList(sysSearch);
    }
    @Transactional(readOnly = true)
    public SysSearch findOneSysSearchByCondition(SysSearch sysSearch) {
        return sysSearchDao.findOneSysSearchByCondition(sysSearch);
    }
    @Transactional(readOnly = true)
    public long findSysSearchCountByCondition(SysSearch sysSearch) {
        return sysSearchDao.findSysSearchCountByCondition(sysSearch);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSysSearch(SysSearch sysSearch) {
        sysSearchDao.updateSysSearch(sysSearch);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysSearch(String id) {
        sysSearchDao.deleteSysSearch(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysSearchByCondition(SysSearch sysSearch) {
        sysSearchDao.deleteSysSearchByCondition(sysSearch);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveSysSearch(List<SysSearch> sysSearchs){
        sysSearchs.forEach(sysSearch -> sysSearch.setId(sequenceId.nextId()));
        sysSearchDao.batchSaveSysSearch(sysSearchs);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteSysSearch(String rowData[]){
        sysSearchDao.batchDeleteSysSearch(rowData);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSysSearch(SysSearch sysSearch){
        sysSearchDao.batchUpdateSysSearch(sysSearch);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysSearchModule(){
        List<SysSearch> sysSearchList = new ArrayList<>();
        ExportParams exportParams = new ExportParams("导入模板","");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysSearch.class, sysSearchList);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysSearch(SysSearch sysSearch){
        List<SysSearch> sysSearchList = sysSearchDao.findSysSearchListByCondition(sysSearch);
        ExportParams exportParams = new ExportParams("导出","列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysSearch.class, sysSearchList);
    }
    @Transactional(rollbackFor = Exception.class)
    public ExcelInfo uploadSysSearch(MultipartFile file){
        ExcelInfo excelInfo = new ExcelInfo();
        List<SysSearch> sysSearchList = new ArrayList<>();
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);
        SysSearch a = new SysSearch();
        a.setAdminId(myId());
        try (InputStream is = file.getInputStream()){
            List<SysSearch> list = ExcelImportUtil.importExcel(is,SysSearch.class, params);
            int cnt = list.size();
            if(list.size()>0){
                list.stream().forEach(e->{
                    e.setId(sequenceId.nextId());
                    e.setAdminId(myId());
                });
                sysSearchDao.batchSaveSysSearch(list);
            }
            excelInfo.setSuccessCnt(cnt-excelInfo.getErrorNumber());
            excelInfo.setObjectList(sysSearchList);
            excelInfo.setSumCnt(cnt);
            return excelInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Transactional(readOnly = true)
    public List<ExcelExport> getSysSearchTableField() {
        return sysSearchDao.getSysSearchTableField();
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysSearchByField(List<ExcelExport> excelExportList,SysSearch sysSearch) {
        List<SysSearch> sysSearchList = sysSearchDao.findSysSearchListByCondition(sysSearch);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        sysSearchList.stream().forEach(e->{
            list.add(beanToMap(e));
        });
        ExportParams exportParams = new ExportParams("导出","列表");
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
    public List<SysSearch> findSysSearchRecentlyList() {
        return sysSearchDao.findSysSearchRecentlyList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSysSearchByCondition(String condition) {
        SysSearch search = new SysSearch();
        search.setSearchContent(condition);
        search.setAdminId(myId());
        search.setAdminName(currentAdmin().getRealName());
        SysSearch oneByCondition = sysSearchDao.findOneSysSearchByCondition(search);
        if (ObjectUtil.isNotNull(oneByCondition)){
            oneByCondition.setCount(String.valueOf(Integer.valueOf(oneByCondition.getCount())+1));
            sysSearchDao.updateSysSearch(oneByCondition);
        }else {
            saveSysSearch(search);
        }
//        sysSearchDao.updateSysSearch(sysSearch);
    }

}
