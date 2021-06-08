package top.cfl.cflwork.controller.system.admin;

import cn.hutool.crypto.digest.DigestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.pojo.*;
import top.cfl.cflwork.pojo.admin.Admin;
import top.cfl.cflwork.pojo.admin.AdminSysRole;
import top.cfl.cflwork.pojo.validateClass.GroupTwo;
import top.cfl.cflwork.service.system.admin.AdminService;
import top.cfl.cflwork.service.system.adminSysRole.AdminSysRoleService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@RestController
@RequestMapping("/admin")
@Api(value = "/admin",description = "模块")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminSysRoleService adminSysRoleService;
    @GetMapping("/findAdminById/{id}")
    @ApiOperation(value = "通过id查找", notes = "返回响应对象")
    public ResponseJson findAdminById(
            @ApiParam(value = "需要用到的id", required = true)
            @PathVariable String id){
        Admin admin=adminService.findAdminById(id);
        return new ResponseJson(admin);
    }

    @PostMapping("/saveAdmin")
    @ApiOperation(value = "保存对象", notes = "返回响应对象")
    public ResponseJson saveAdmin(
            @ApiParam(value = "对象", required = true)
            @RequestBody Admin admin){
        Admin a = new Admin();
        a.setUsername(admin.getUsername());
        long cnt = adminService.findAdminCountByCondition(a);
        if(cnt==0){
            admin.setPassword(DigestUtil.sha1Hex(DigestUtil.md5Hex(Constant.DEFAULT_PWD)));
            adminService.saveAdmin(admin);
            admin.setPassword("*****");
            return new ResponseJson(admin);
        }else{
            return new ResponseJson(false,"该账号已存在，请不要重复添加");
        }

    }
    @PostMapping("/updateAdmin")
    @ApiOperation(value = "修改对象", notes = "返回响应对象")
    public ResponseJson updateAdmin(
            @ApiParam(value = "被修改的对象,对象属性不为空则修改", required = true)
            @RequestBody Admin admin){
        try{
            adminService.updateAdmin(admin);
            return new ResponseJson();
        }catch (Exception e){
            return new ResponseJson(false,"该账号已存在，请不要重复添加");
        }
    }

    @PostMapping("/findAdminsByCondition")
    @ApiOperation(value = "根据条件查找", notes = "返回响应对象")
    public ResponseJson findAdminsByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody Admin admin){
        admin.setIgnore("admin");
        List<Admin> data=adminService.findAdminListByCondition(admin);
        long count=adminService.findAdminCountByCondition(admin);
        return new ResponseJson(data,count);
    }
    @GetMapping("/deleteAdmin/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteAdmin(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        adminService.deleteAdmin(id);
        return new ResponseJson();
    }

    @GetMapping("/findSysRolesByAdminId/{id}")
    @ApiOperation(value = "根据adminId获取角色列表", notes = "返回响应对象")
    public ResponseJson findSysRolesByAdminId(@PathVariable("id") String id){
        return adminService.findSysRolesByAdminId(id);
    }

    @PostMapping("/saveAdminSysRoles")
    public ResponseJson saveAdminSysRoles(@RequestBody List<AdminSysRole> adminSysRoles){
        adminSysRoleService.batchSaveAdminSysRole(adminSysRoles);
        return new ResponseJson();
    }

    @PostMapping("/delsertAdminSysRoles")
    public ResponseJson delsertAdminSysRoles(@RequestBody Map<String,String> map){
        adminSysRoleService.delsertAdminSysRoles(map);
        return new ResponseJson();
    }

    /*gzw*/
    @GetMapping("/ignore/findMySelf")
    public ResponseJson findMySelf(){
        try{
            Admin admin = adminService.findAdminById(myId());
            admin.setPassword(null);
            return new ResponseJson(admin);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseJson(false,"获取数据有误");
        }
    }

    @PostMapping("/ignore/updateMySelf")
    public ResponseJson updateMySelf(@RequestBody Admin admin){
        admin.setId(myId());
        final Admin a = new Admin();
        a.setId(myId());
        a.setPortrait(admin.getPortrait());
        a.setRealName(admin.getRealName());
        a.setPhone(admin.getPhone());
        a.setEmail(admin.getEmail());
        Admin result = adminService.updateMySelf(a);
        return new ResponseJson(result);
    }
    @PostMapping("/ignore/updateMyPassword")
    public ResponseJson updateMyPassword(@Validated(value = GroupTwo.class) @RequestBody Admin admin){
        admin.setId(myId());
        Admin a = adminService.findAdminById(myId());
        String newPassword=DigestUtil.sha1Hex(admin.getNewPassword());
        String oldPassword=DigestUtil.sha1Hex(admin.getPassword());
        if(!a.getPassword().equals(oldPassword)){
            return new ResponseJson(false,"原密码错误");
        }
        a.setPassword(newPassword);
        adminService.updateMySelf(a);
        return new ResponseJson();
    }

    @PostMapping("/resetPwd")
    @ApiOperation(value = "修改对象", notes = "返回响应对象")
    public ResponseJson resetPwd(
            @ApiParam(value = "重置密码", required = true)
            @RequestBody Admin admin){
        try{
            admin.setPassword(DigestUtil.sha1Hex(admin.getPassword()));
            adminService.updateAdmin(admin);
            return new ResponseJson();
        }catch (Exception e){
            return new ResponseJson(false,"重置失败");
        }
    }

    @PostMapping("/batchResetPwd")
    @ApiOperation(value = "修改对象", notes = "返回响应对象")
    public ResponseJson batchResetPwd(
            @ApiParam(value = "重置密码", required = true)
            @RequestBody Admin admin){
        try{
            admin.setPassword(DigestUtil.sha1Hex(admin.getPassword()));
            adminService.batchUpdateAdmin(admin);
            return new ResponseJson();
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseJson(false,"批量重置失败");
        }
    }

    // 下载模板
    @PostMapping("/down")
    public void exportTemplate(HttpServletResponse response,@RequestBody Admin admin) {

        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=assetsSort.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = adminService.exportTemplate(admin);
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/exportAdminModule")
    @ApiOperation(value = "下载管理员表模板", notes = "返回响应对象,不包含总条数", response=Admin.class)
    public void exportAdminModule(HttpServletResponse response) {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=assetsSort.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = adminService.exportAdminModule();
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/importByExcel")
    @ApiOperation(value = "通过excel导入管理员表", notes = "返回导入结果")
    public ResponseJson importByExcel(MultipartFile file){
        ExcelInfo excelInfo = adminService.uploadAdmin(file);
        if(null == excelInfo){
            return new ResponseJson(false,"模板有误");
        }else{
            return new ResponseJson(excelInfo);
        }

    }
    @GetMapping("/getAdminTableField")
    @ApiOperation(value = "获取字段详情", notes = "返回响应对象,不包含总条数", response = Admin.class)
    public ResponseJson getAdminTableField() {
        return new ResponseJson(adminService.getAdminTableField());
    }

    @PostMapping("/exportAdminByField")
    @ApiOperation(value = "导出汇交登记管理，需要在此数据中，导出移交清单模板", notes = "返回响应对象,不包含总条数", response = Admin.class)
    public void exportAdminByField(HttpServletResponse response, @RequestBody ExcelParam<Admin> excelParam) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=Admin.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = adminService.exportAdminByField(excelParam.getExcelExportList(),excelParam.getData());
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
