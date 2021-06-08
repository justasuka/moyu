package top.cfl.cflwork.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.annotation.Log;
import top.cfl.cflwork.pojo.ExcelInfo;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.SysDepartment;
import top.cfl.cflwork.pojo.admin.Admin;
import top.cfl.cflwork.service.SysDepartmentService;
import top.cfl.cflwork.service.system.admin.AdminService;
import top.cfl.cflwork.util.ObjectKit;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sysDepartment")
@Api(value = "/sysDepartment",description = "功能描述：部门表模块")
public class SysDepartmentController {
    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private AdminService adminService;

    @Log("保存功能描述：部门表对象")
    @PostMapping("/saveSysDepartment")
    @ApiOperation(value = "保存功能描述：部门表对象", notes = "返回保存好的功能描述：部门表对象", response=SysDepartment.class)
    public ResponseJson saveSysDepartment(
            @ApiParam(value = "功能描述：部门表对象", required = true)
            @RequestBody SysDepartment sysDepartment){
        sysDepartmentService.saveSysDepartment(sysDepartment);
        return new ResponseJson(sysDepartment);
    }

    @Log("去更新页面,通过id查找功能描述：部门表")
    @GetMapping("/update/findSysDepartmentById/{id}")
    @ApiOperation(value = "去更新页面,通过id查找功能描述：部门表", notes = "返回响应对象", response=SysDepartment.class)
    public ResponseJson findSysDepartmentById(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String id){
        SysDepartment sysDepartment=sysDepartmentService.findSysDepartmentById(id);
        return new ResponseJson(sysDepartment);
    }

    @Log("修改功能描述：部门表对象")
    @PostMapping("/update/updateSysDepartment")
    @ApiOperation(value = "修改功能描述：部门表对象", notes = "返回响应对象")
    public ResponseJson updateSysDepartment(
            @ApiParam(value = "被修改的功能描述：部门表对象,对象属性不为空则修改", required = true)
            @RequestBody SysDepartment sysDepartment){
        sysDepartmentService.updateSysDepartment(sysDepartment);
        return new ResponseJson();
    }

    @Log("去查看页面,通过id查找功能描述：部门表")
    @GetMapping("/look/lookSysDepartmentById/{id}")
    @ApiOperation(value = "去查看页面,通过id查找功能描述：部门表", notes = "返回响应对象", response=SysDepartment.class)
    public ResponseJson lookSysDepartmentById(
            @ApiParam(value = "去查看页面,需要用到的id", required = true)
            @PathVariable String id){
        SysDepartment sysDepartment=sysDepartmentService.findSysDepartmentById(id);
        return new ResponseJson(sysDepartment);
    }

    @Log("根据条件查找功能描述：部门表")
    @PostMapping("/findSysDepartmentsByCondition")
    @ApiOperation(value = "根据条件查找功能描述：部门表", notes = "返回响应对象", response=SysDepartment.class)
    public ResponseJson findSysDepartmentsByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDepartment sysDepartment){
        List<SysDepartment> data=sysDepartmentService.findSysDepartmentListByCondition(sysDepartment);
        long count=sysDepartmentService.findSysDepartmentCountByCondition(sysDepartment);
        return new ResponseJson(data,count);
    }

    @Log("根据条件查找单个功能描述：部门表,结果必须为单条数据")
    @PostMapping("/findOneSysDepartmentByCondition")
    @ApiOperation(value = "根据条件查找单个功能描述：部门表,结果必须为单条数据", notes = "没有时返回空", response=SysDepartment.class)
    public ResponseJson findOneSysDepartmentByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysDepartment sysDepartment){
        SysDepartment one=sysDepartmentService.findOneSysDepartmentByCondition(sysDepartment);
        return new ResponseJson(one);
    }

    @Log("根据id删除功能描述：部门表")
    @GetMapping("/deleteSysDepartment/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysDepartment(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysDepartmentService.deleteSysDepartment(id);
        return new ResponseJson();
    }

    @Log("根据条件查找功能描述：部门表列表")
    @PostMapping("/findSysDepartmentListByCondition")
    @ApiOperation(value = "根据条件查找功能描述：部门表列表", notes = "返回响应对象,不包含总条数", response=SysDepartment.class)
    public ResponseJson findSysDepartmentListByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDepartment sysDepartment){
        List<SysDepartment> data=sysDepartmentService.findSysDepartmentListByCondition(sysDepartment);
        return new ResponseJson(data);
    }

    @Log("根据对象批量修改功能描述：部门表")
    @PostMapping("/batchUpdateSysDepartment/{id}")
    @ApiOperation(value = "根据id批量修改", notes = "返回响应对象")
    public ResponseJson batchUpdateSysDepartment(
            @ApiParam(value = "被修改的对象", required = true)
            @RequestBody SysDepartment sysDepartment){
        sysDepartmentService.batchUpdateSysDepartment(sysDepartment);
        return new ResponseJson();
    }

    @PostMapping("/batchDeleteSysDepartment")
    @ApiOperation(value = "根据id批量删除功能描述：部门表", notes = "返回响应对象")
    public ResponseJson batchDeleteSysDepartment(
            @ApiParam(value = "根据id批量删除功能描述：部门表", required = true)
            @RequestBody SysDepartment sysDepartment){
        try{
            sysDepartmentService.batchDeleteSysDepartment(sysDepartment.getRowData());
            return new ResponseJson();
        }catch (Exception e){
            return new ResponseJson(false,"删除失败");
        }

    }

    @GetMapping("/exportSysDepartmentModule")
    @ApiOperation(value = "下载功能描述：部门表模板", notes = "返回响应对象,不包含总条数", response=SysDepartment.class)
    public void exportSysDepartmentModule(HttpServletResponse response) {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=assetsSort.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysDepartmentService.exportSysDepartmentModule();
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportSysDepartment")
    @ApiOperation(value = "导出功能描述：部门表模板", notes = "返回响应对象,不包含总条数", response=SysDepartment.class)
    public void exportSysDepartment(HttpServletResponse response,@RequestBody SysDepartment sysDepartment) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysDepartment.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysDepartmentService.exportSysDepartment(sysDepartment);
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/importByExcel")
    @ApiOperation(value = "通过excel导入功能描述：部门表", notes = "返回导入结果")
    public ResponseJson importByExcel(MultipartFile file){
        ExcelInfo excelInfo = sysDepartmentService.uploadSysDepartment(file);
        if(null == excelInfo){
            return new ResponseJson(false,"模板有误");
        }else{
            return new ResponseJson(excelInfo);
        }

    }

    @Log("根据条件获取功能描述：部门表的所有数据列表")
    @PostMapping("/findSysDepartmentAllList")
    @ApiOperation(value = "根据条件获取功能描述：部门表的所有数据列表", notes = "返回响应对象,不包含总条数", response=SysDepartment.class)
    public ResponseJson findSysDepartmentAllList(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDepartment sysDepartment){
//        List<SysDepartment> data=sysDepartmentService.findSysDepartmentAllListByDep(sysDepartment);
        List<SysDepartment> data=sysDepartmentService.findSysDepartmentAllList(sysDepartment);
        return new ResponseJson(ObjectKit.buildTree(data,"-1"));
    }
    @Log("根据条件获取功能描述：部门表的所有数据列表")
    @PostMapping("/findSysDepartmentAllListNoTree")
    @ApiOperation(value = "根据条件获取功能描述：部门表的所有数据列表", notes = "返回响应对象,不包含总条数", response=SysDepartment.class)
    public ResponseJson findSysDepartmentAllListNoTree(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDepartment sysDepartment){
        try{
            List<SysDepartment> data=sysDepartmentService.findSysDepartmentAllList(sysDepartment);
            return new ResponseJson(data);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseJson();
        }

    }

    @PostMapping("/findSysDepartmentAndAdminAllList")
    @ApiOperation(value = "根据条件获取功能描述：部门表的所有数据列表", notes = "返回响应对象,不包含总条数", response=SysDepartment.class)
    public ResponseJson findSysDepartmentAndAdminAllList(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDepartment sysDepartment){
        List<SysDepartment> data=sysDepartmentService.findSysDepartmentAllListByDep(sysDepartment);
        Admin admin = new Admin();
        List<Admin> adminList = adminService.findAdminListByCondition(admin);
        List<Map<String,String>> adminList2 = new ArrayList<>();
        try{

            for(Admin adm : adminList) {
                Map<String,String> newAdmin = new HashMap<String,String>();
                for(String fieldName : sysDepartment.getRowData()){
                    Field fld = adm.getClass().getDeclaredField(fieldName);
                    fld.setAccessible(true);
                    Object o = fld.get(adm);
                    //fld.set(newAdmin,o);
                    newAdmin.put(fieldName,String.valueOf(o));
                }
                adminList2.add(newAdmin);
            }
        }catch(Exception e){

        }

        List<SysDepartment> data2 = getAdminList(adminList2,data);

        return new ResponseJson(ObjectKit.buildTree(data2,"-1"));
    }

    public List<SysDepartment> getAdminList(List<Map<String,String>> adminList, List<SysDepartment> data){
        data.forEach(e->{
            List<Map<String,String>> subAdminList = new ArrayList<>();
            adminList.forEach((a)->{
                if(e.getId().equals(a.get("departmentId"))){
                    subAdminList.add(a);
                }

            });
            e.setAdminList(subAdminList);
        });
        return data;
    }

}
