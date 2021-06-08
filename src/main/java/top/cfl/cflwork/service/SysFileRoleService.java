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
import top.cfl.cflwork.dao.SysFileRoleDao;
import top.cfl.cflwork.pojo.ExcelInfo;
import top.cfl.cflwork.pojo.SysFile;
import top.cfl.cflwork.pojo.SysFileRole;
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
public class SysFileRoleService {
    @Autowired
    private SysFileRoleDao sysFileRoleDao;
    @Autowired
    private SequenceId sequenceId;
    @Autowired
    private SysFileDao sysFileDao;

    @Transactional(readOnly = true)
    public SysFileRole findSysFileRoleById(String id) {
        return sysFileRoleDao.findSysFileRoleById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysFileRole(SysFileRole sysFileRole) {
        sysFileRole.setId(sequenceId.nextId());
        sysFileRoleDao.saveSysFileRole(sysFileRole);
    }
    @Transactional(readOnly = true)
    public List<SysFileRole> findSysFileRoleListByCondition(SysFileRole sysFileRole) {
        return sysFileRoleDao.findSysFileRoleListByCondition(sysFileRole);
    }
    @Transactional(readOnly = true)
    public List<SysFileRole> findSysFileRoleAllList(SysFileRole sysFileRole) {
        return sysFileRoleDao.findSysFileRoleAllList(sysFileRole);
    }
    @Transactional(readOnly = true)
    public SysFileRole findOneSysFileRoleByCondition(SysFileRole sysFileRole) {
        return sysFileRoleDao.findOneSysFileRoleByCondition(sysFileRole);
    }
    @Transactional(readOnly = true)
    public long findSysFileRoleCountByCondition(SysFileRole sysFileRole) {
        return sysFileRoleDao.findSysFileRoleCountByCondition(sysFileRole);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSysFileRole(SysFileRole sysFileRole) {
        sysFileRoleDao.updateSysFileRole(sysFileRole);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysFileRole(String id) {
        sysFileRoleDao.deleteSysFileRole(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysFileRoleByCondition(SysFileRole sysFileRole) {
        sysFileRoleDao.deleteSysFileRoleByCondition(sysFileRole);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveSysFileRole(List<SysFileRole> sysFileRoles){
        sysFileRoles.forEach(sysFileRole -> sysFileRole.setId(sequenceId.nextId()));
        sysFileRoleDao.batchSaveSysFileRole(sysFileRoles);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteSysFileRole(String rowData[]){
        sysFileRoleDao.batchDeleteSysFileRole(rowData);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSysFileRole(SysFileRole sysFileRole){
        sysFileRoleDao.batchUpdateSysFileRole(sysFileRole);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileRoleModule(){
        List<SysFileRole> sysFileRoleList = new ArrayList<>();
        ExportParams exportParams = new ExportParams("角色与文件（夹）关系表导入模板","角色与文件（夹）关系表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysFileRole.class, sysFileRoleList);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileRole(SysFileRole sysFileRole){
        List<SysFileRole> sysFileRoleList = sysFileRoleDao.findSysFileRoleListByCondition(sysFileRole);
        ExportParams exportParams = new ExportParams("角色与文件（夹）关系表导出","角色与文件（夹）关系表列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysFileRole.class, sysFileRoleList);
    }
    @Transactional(rollbackFor = Exception.class)
    public ExcelInfo uploadSysFileRole(MultipartFile file){
        ExcelInfo excelInfo = new ExcelInfo();
        List<SysFileRole> sysFileRoleList = new ArrayList<>();
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);
        SysFileRole a = new SysFileRole();
        try (InputStream is = file.getInputStream()){
            List<SysFileRole> list = ExcelImportUtil.importExcel(is,SysFileRole.class, params);
            int cnt = list.size();
            if(list.size()>0){
                list.stream().forEach(e->{
                    e.setId(sequenceId.nextId());
                });
                sysFileRoleDao.batchSaveSysFileRole(list);
            }
            excelInfo.setSuccessCnt(cnt-excelInfo.getErrorNumber());
            excelInfo.setObjectList(sysFileRoleList);
            excelInfo.setSumCnt(cnt);
            return excelInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Transactional(readOnly = true)
    public List<ExcelExport> getSysFileRoleTableField() {
        return sysFileRoleDao.getSysFileRoleTableField();
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileRoleByField(List<ExcelExport> excelExportList,SysFileRole sysFileRole) {
        List<SysFileRole> sysFileRoleList = sysFileRoleDao.findSysFileRoleListByCondition(sysFileRole);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        sysFileRoleList.stream().forEach(e->{
            list.add(beanToMap(e));
        });
        ExportParams exportParams = new ExportParams("角色与文件（夹）关系表导出","角色与文件（夹）关系表列表");
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

    @Transactional
    public void delsertFileRoles(Map<String, String> map) {
        String roleId = map.get("roleId");
        String permIds = map.get("permIds");
        sysFileRoleDao.deleteRoleByRoleId(roleId);
        if(permIds!=null){
            String[] permArr = permIds.split(",");
            if(permArr.length>0){
                List<SysFileRole> fileRoles = new ArrayList<>();
                for (String fileId : permArr) {
                    SysFileRole fileRole = new SysFileRole();
                    fileRole.setId(sequenceId.nextId());
                    fileRole.setSysRoleId(roleId);
                    fileRole.setSysFileId(fileId);
                    SysFile fileById = sysFileDao.findSysFileById(fileId);
                    fileRole.setSysFileParentId(fileById.getParentId());
                    fileRoles.add(fileRole);
                }
                sysFileRoleDao.batchSaveSysFileRole(fileRoles);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<SysFileRole> findSysFileRoleByRoleId(String rowData[]) {
        return sysFileRoleDao.findSysFileRoleByRoleId(rowData);
    }

}
