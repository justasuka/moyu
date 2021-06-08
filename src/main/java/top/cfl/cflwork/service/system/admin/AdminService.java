package top.cfl.cflwork.service.system.admin;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.dao.SysDepartmentDao;
import top.cfl.cflwork.dao.admin.admin.AdminDao;
import top.cfl.cflwork.dao.admin.adminSysRole.AdminSysRoleDao;
import top.cfl.cflwork.dao.admin.sysRole.SysRoleDao;
import top.cfl.cflwork.dao.dd.SysDdDao;
import top.cfl.cflwork.pojo.*;
import top.cfl.cflwork.pojo.admin.Admin;
import top.cfl.cflwork.pojo.admin.SysPerm;
import top.cfl.cflwork.pojo.admin.SysRole;
import top.cfl.cflwork.pojo.dd.SysDd;
import top.cfl.cflwork.util.ExcelStyleUtil;
import top.cfl.cflwork.util.SequenceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static top.cfl.cflwork.util.CommonUtil.camelCaseName;
import static top.cfl.cflwork.util.ObjectUtil.beanToMap;

@Service
public class AdminService {
    @Autowired
    private AdminDao adminDao;
    @Autowired
    private SequenceId sequenceId;
    @Autowired
    private AdminSysRoleDao adminSysRoleDao;
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysDepartmentDao sysDepartmentDao;
    @Autowired
    private SysDdDao sysDdDao;
    @Transactional(readOnly = true)
    public Admin findAdminById(String id) {
        return adminDao.findAdminById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveAdmin(Admin admin) {
        admin.setId(sequenceId.nextId());
        adminDao.saveAdmin(admin);
    }
    @Transactional(readOnly = true)
    public List<Admin> findAdminListByCondition(Admin admin) {
        return adminDao.findAdminListByCondition(admin);
    }
    @Transactional(readOnly = true)
    public long findAdminCountByCondition(Admin admin) {
        return adminDao.findAdminCountByCondition(admin);
    }
    @Transactional
    public void updateAdmin(Admin admin) {
        adminDao.updateAdmin(admin);
    }
    @Transactional
    public void deleteAdmin(String id) {
        adminDao.deleteAdmin(id);
        //同时删除admin和jw_role的中间表
        adminSysRoleDao.deleteAdminSysRoleByAdminId(id);
    }

    @Transactional(readOnly = true)
    public Admin adminLogin(Admin admin) {
        admin.setPassword(DigestUtil.sha1Hex(admin.getPassword()));
        List<Admin> admins = adminDao.findAdminListByCondition(admin);
        if(admins.size()==0)return null;
        return admins.get(0);
    }
    @Transactional(readOnly = true)
    public List<String> findCheckedRoloIdsByAdminId(String adminId) {
        return adminDao.findCheckedRoloIdsByAdminId(adminId);
    }
    @Transactional(readOnly = true)
    public List<SysPerm> findSysFuncPermsByAdminId(String adminId) {
        return adminDao.findSysFuncPermsByAdminId(adminId);
    }
    public ResponseJson findSysRolesByAdminId(String id) {
        List<SysRole> roles= sysRoleDao.findSysRoleListByCondition(new SysRole());
        List<String> checkedIds=adminDao.findCheckedRoloIdsByAdminId(id);
        return new ResponseJson(roles,checkedIds);
    }
    public Workbook exportTemplate(Admin admin){
        List<Admin> assetsSortList = adminDao.findAdminListByCondition(admin);
        ExportParams exportParams = new ExportParams();
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(new ExportParams("cflwork后端管理员账号","账号管理"),
                Admin.class, assetsSortList);
    }
    /*gzw*/
    public Admin updateMySelf(Admin admin) {
        adminDao.updateAdmin(admin);
        Admin admin1 = adminDao.findAdminById(admin.getId());
        admin1.setPassword(null);
        return admin1;
    }

    public void batchUpdateAdmin(Admin admin){
        adminDao.batchUpdateAdmin(admin) ;
    }

    public static void main(String[] args) {
        System.out.println(DigestUtil.sha1Hex("b1ca8c821453403725ed565ea18a075b"));
    }

    public List<Admin> findSysDepartmentList(){
        return adminDao.findSysDepartmentList() ;
    }

    @Transactional(readOnly = true)
    public List<Admin> findAdminListById(String id) {
        return adminDao.findAdminListById(id.split(","));
    }

    @Transactional(rollbackFor = Exception.class)
    public ExcelInfo uploadAdmin(MultipartFile file){
        ExcelInfo excelInfo = new ExcelInfo();
        List<Admin> adminList = new ArrayList<>();
        List<Admin> admins = new ArrayList<>();
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);
        Admin a = new Admin();
        try (InputStream is = file.getInputStream()){
            List<Admin> list = ExcelImportUtil.importExcel(is,Admin.class, params);
            int cnt = list.size();
            if(list.size()>0){
                list.stream().forEach(e->{
                    Admin admin = new Admin();
                    admin.setUsername(e.getUsername());
                    long count = adminDao.findAdminCountByCondition(admin);
                    if(count==0){
                        e.setId(sequenceId.nextId());
                        e.setPassword(DigestUtil.sha1Hex(DigestUtil.md5Hex(Constant.DEFAULT_PWD)));
                        if(StringUtils.isNotEmpty(e.getDepartmentName())){
                            SysDepartment sysDepartment = new SysDepartment();
                            sysDepartment.setName(e.getDepartmentName());
                            SysDepartment sd = sysDepartmentDao.findOneSysDepartmentByCondition(sysDepartment);
                            if(ObjectUtils.allNotNull(sd)){
                                e.setDepartmentId(sd.getId());
                            }
                        }
                        if(StringUtils.isNotEmpty(e.getJobName())){
                            SysDd sysDd = new SysDd();
                            sysDd.setName(e.getJobName());
                            sysDd.setTypeCode(Constant.DD.JOB_CODE);
                            SysDd sdd = sysDdDao.findOneSysDdByCondition(sysDd);
                            if(ObjectUtils.allNotNull(sdd)){
                                e.setJobId(sdd.getId());
                            }
                        }
                        e.setCreateTime(DateUtil.now());
                        admins.add(e);
                    }else{
                        e.setErrorMsg("用户已存在");
                        adminList.add(e);
                    }
                });
                if(admins.size()>0){
                    adminDao.batchSaveAdmin(admins);
                }
            }
            excelInfo.setSuccessCnt(cnt-excelInfo.getErrorNumber());
            excelInfo.setObjectList(adminList);
            excelInfo.setSumCnt(cnt);
            return excelInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportAdminModule(){
        List<Admin> adminList = new ArrayList<>();
        ExportParams exportParams = new ExportParams("管理员表导入模板","管理员表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, Admin.class, adminList);
    }
    @Transactional(readOnly = true)
    public List<ExcelExport> getAdminTableField() {
        return adminDao.getAdminTableField();
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportAdminByField(List<ExcelExport> excelExportList,Admin admin) {
        List<Admin> adminList = adminDao.findAdminListByCondition(admin);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        adminList.stream().forEach(e->{
            list.add(beanToMap(e));
        });
        ExportParams exportParams = new ExportParams("管理员表导出","管理员表列表");
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
}
