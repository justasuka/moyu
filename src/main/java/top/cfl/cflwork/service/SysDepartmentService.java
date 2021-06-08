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
import top.cfl.cflwork.dao.SysDepartmentDao;
import top.cfl.cflwork.pojo.ExcelInfo;
import top.cfl.cflwork.pojo.SysDepartment;
import top.cfl.cflwork.util.ExcelStyleUtil;
import top.cfl.cflwork.util.SequenceId;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static top.cfl.cflwork.interceptor.LoginInterceptor.currentAdmin;
import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@Service
public class SysDepartmentService {
    @Autowired
    private SysDepartmentDao sysDepartmentDao;
    @Autowired
    private SequenceId sequenceId;

    @Transactional(readOnly = true)
    public SysDepartment findSysDepartmentById(String id) {
        return sysDepartmentDao.findSysDepartmentById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysDepartment(SysDepartment sysDepartment) {
        sysDepartment.setId(sequenceId.nextId());
        sysDepartment.setAdminId(myId());
        sysDepartment.setAdminName(currentAdmin().getRealName());
        sysDepartmentDao.saveSysDepartment(sysDepartment);
    }
    @Transactional(readOnly = true)
    public List<SysDepartment> findSysDepartmentListByCondition(SysDepartment sysDepartment) {
        return sysDepartmentDao.findSysDepartmentListByCondition(sysDepartment);
    }
    @Transactional(readOnly = true)
    public List<SysDepartment> findSysDepartmentAllList(SysDepartment sysDepartment) {
        return sysDepartmentDao.findSysDepartmentAllList(sysDepartment);
    }
    @Transactional(readOnly = true)
    public List<SysDepartment> findSysDepartmentAllListByDep(SysDepartment sysDepartment) {
        return sysDepartmentDao.findSysDepartmentAllListByDep(sysDepartment);
    }
    @Transactional(readOnly = true)
    public SysDepartment findOneSysDepartmentByCondition(SysDepartment sysDepartment) {
        return sysDepartmentDao.findOneSysDepartmentByCondition(sysDepartment);
    }
    @Transactional(readOnly = true)
    public long findSysDepartmentCountByCondition(SysDepartment sysDepartment) {
        return sysDepartmentDao.findSysDepartmentCountByCondition(sysDepartment);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSysDepartment(SysDepartment sysDepartment) {
        sysDepartmentDao.updateSysDepartment(sysDepartment);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysDepartment(String id) {
        sysDepartmentDao.deleteSysDepartment(id);
        deleteSysDepartmentByPId(id);
    }
    @Transactional
    public void deleteSysDepartmentByPId(String pId) {
        List<SysDepartment> sysDepartments = sysDepartmentDao.findSysDepartmentByPId(pId);
        for (SysDepartment sysDepartment : sysDepartments) {
            sysDepartmentDao.deleteSysDepartment(sysDepartment.getId());
            deleteSysDepartmentByPId(sysDepartment.getId());
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysDepartmentByCondition(SysDepartment sysDepartment) {
        sysDepartmentDao.deleteSysDepartmentByCondition(sysDepartment);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveSysDepartment(List<SysDepartment> sysDepartments){
        sysDepartments.forEach(sysDepartment -> sysDepartment.setId(sequenceId.nextId()));
        sysDepartmentDao.batchSaveSysDepartment(sysDepartments);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteSysDepartment(String rowData[]){
        //sysDepartmentDao.batchDeleteSysDepartment(rowData);
        for (int i = 0;i<rowData.length;i++){
            deleteSysDepartment(rowData[i]);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSysDepartment(SysDepartment sysDepartment){
        sysDepartmentDao.batchUpdateSysDepartment(sysDepartment);
    }

    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysDepartmentModule(){
        List<SysDepartment> sysDepartmentList = new ArrayList<>();
        ExportParams exportParams = new ExportParams("功能描述：部门表导入模板","功能描述：部门表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysDepartment.class, sysDepartmentList);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysDepartment(SysDepartment sysDepartment){
        List<SysDepartment> sysDepartmentList = sysDepartmentDao.findSysDepartmentAllList(sysDepartment);
        ExportParams exportParams = new ExportParams("功能描述：部门表导出","功能描述：部门表列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysDepartment.class, sysDepartmentList);
    }
    @Transactional(rollbackFor = Exception.class)
    public ExcelInfo uploadSysDepartment(MultipartFile file){
        ExcelInfo excelInfo = new ExcelInfo();
        List<SysDepartment> sysDepartmentList = new ArrayList<>();
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);
        SysDepartment a = new SysDepartment();
        a.setAdminId(myId());
        try (InputStream is = file.getInputStream()){
            List<SysDepartment> list = ExcelImportUtil.importExcel(is,SysDepartment.class, params);
            int cnt = list.size();
            if(list.size()>0){
                list.stream().forEach(e->{
                    e.setId(sequenceId.nextId());
                    e.setAdminId(myId());
                });
                sysDepartmentDao.batchSaveSysDepartment(list);
            }
            excelInfo.setSuccessCnt(cnt-excelInfo.getErrorNumber());
            excelInfo.setObjectList(sysDepartmentList);
            excelInfo.setSumCnt(cnt);
            return excelInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    @Transactional(readOnly = true)
    public List<SysDepartment> findSysDepartmentAllListByParentId(SysDepartment sysDepartment) {
        List<SysDepartment> result = sysDepartmentDao.findSysDepartmentAllList(sysDepartment);
        int length = result.size();
        for(int i = 0;i<length;i++){
            List<SysDepartment> list = findSysDepartmentByPId(result.get(i).getId());
            for (SysDepartment l :list){
                result.add(l);
            }
        }
        return result;
    }
    @Transactional
    public List<SysDepartment> findSysDepartmentByPId(String pId) {
        List<SysDepartment> list = new ArrayList<>();
        List<SysDepartment> sysDepartments = sysDepartmentDao.findSysDepartmentByPId(pId);
        for (SysDepartment sysDepartment : sysDepartments) {
            list.add(sysDepartment);
            findSysDepartmentByPId(sysDepartment.getId());
        }
        return list;
    }
    @Transactional(readOnly = true)
    public List<SysDepartment> findSysDepartmentNoParentAllList() {
        return sysDepartmentDao.findSysDepartmentNoParentAllList();
    }

}
