package top.cfl.cflwork.controller;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.annotation.Log;
import top.cfl.cflwork.dao.admin.sysRole.SysRoleDao;
import top.cfl.cflwork.interceptor.LoginInterceptor;
import top.cfl.cflwork.pojo.*;
import top.cfl.cflwork.pojo.admin.SysPerm;
import top.cfl.cflwork.pojo.admin.SysRole;
import top.cfl.cflwork.service.SysFileRoleService;
import top.cfl.cflwork.service.SysFileService;
import top.cfl.cflwork.service.system.sysRole.SysRoleService;
import top.cfl.cflwork.util.ObjectKit;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/sysFileRole")
@Api(value = "/sysFileRole",description = "角色与文件（夹）关系表模块")
public class SysFileRoleController {
    @Autowired
    private SysFileRoleService sysFileRoleService;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysRoleService sysRoleService;

    @Log("保存角色与文件（夹）关系表对象")
    @PostMapping("/saveSysFileRole")
    @ApiOperation(value = "保存角色与文件（夹）关系表对象", notes = "返回保存好的角色与文件（夹）关系表对象", response=SysFileRole.class)
    public ResponseJson saveSysFileRole(
            @ApiParam(value = "角色与文件（夹）关系表对象", required = true)
            @RequestBody SysFileRole sysFileRole){
        sysFileRoleService.saveSysFileRole(sysFileRole);
        return new ResponseJson(sysFileRole);
    }

    @Log("去更新页面,通过id查找角色与文件（夹）关系表")
    @GetMapping("/update/findSysFileRoleById/{id}")
    @ApiOperation(value = "去更新页面,通过id查找角色与文件（夹）关系表", notes = "返回响应对象", response=SysFileRole.class)
    public ResponseJson findSysFileRoleById(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String id){
        SysFileRole sysFileRole=sysFileRoleService.findSysFileRoleById(id);
        return new ResponseJson(sysFileRole);
    }

    @Log("修改角色与文件（夹）关系表对象")
    @PostMapping("/update/updateSysFileRole")
    @ApiOperation(value = "修改角色与文件（夹）关系表对象", notes = "返回响应对象")
    public ResponseJson updateSysFileRole(
            @ApiParam(value = "被修改的角色与文件（夹）关系表对象,对象属性不为空则修改", required = true)
            @RequestBody SysFileRole sysFileRole){
        sysFileRoleService.updateSysFileRole(sysFileRole);
        return new ResponseJson();
    }

    @Log("去查看页面,通过id查找角色与文件（夹）关系表")
    @GetMapping("/look/lookSysFileRoleById/{id}")
    @ApiOperation(value = "去查看页面,通过id查找角色与文件（夹）关系表", notes = "返回响应对象", response=SysFileRole.class)
    public ResponseJson lookSysFileRoleById(
            @ApiParam(value = "去查看页面,需要用到的id", required = true)
            @PathVariable String id){
        SysFileRole sysFileRole=sysFileRoleService.findSysFileRoleById(id);
        return new ResponseJson(sysFileRole);
    }

    @Log("根据条件查找角色与文件（夹）关系表")
    @PostMapping("/findSysFileRolesByCondition")
    @ApiOperation(value = "根据条件查找角色与文件（夹）关系表", notes = "返回响应对象", response=SysFileRole.class)
    public ResponseJson findSysFileRolesByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileRole sysFileRole){
        List<SysFileRole> data=sysFileRoleService.findSysFileRoleListByCondition(sysFileRole);
        long count=sysFileRoleService.findSysFileRoleCountByCondition(sysFileRole);
        return new ResponseJson(data,count);
    }

    @Log("根据条件查找单个角色与文件（夹）关系表,结果必须为单条数据")
    @PostMapping("/findOneSysFileRoleByCondition")
    @ApiOperation(value = "根据条件查找单个角色与文件（夹）关系表,结果必须为单条数据", notes = "没有时返回空", response=SysFileRole.class)
    public ResponseJson findOneSysFileRoleByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysFileRole sysFileRole){
        SysFileRole one=sysFileRoleService.findOneSysFileRoleByCondition(sysFileRole);
        return new ResponseJson(one);
    }

    @Log("根据id删除角色与文件（夹）关系表")
    @GetMapping("/deleteSysFileRole/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysFileRole(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysFileRoleService.deleteSysFileRole(id);
        return new ResponseJson();
    }

    @Log("根据条件查找角色与文件（夹）关系表列表")
    @PostMapping("/findSysFileRoleListByCondition")
    @ApiOperation(value = "根据条件查找角色与文件（夹）关系表列表", notes = "返回响应对象,不包含总条数", response=SysFileRole.class)
    public ResponseJson findSysFileRoleListByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileRole sysFileRole){
        List<SysFileRole> data=sysFileRoleService.findSysFileRoleListByCondition(sysFileRole);
        return new ResponseJson(data);
    }

    @Log("根据对象批量修改角色与文件（夹）关系表")
    @PostMapping("/batchUpdateSysFileRole")
    @ApiOperation(value = "根据id批量修改", notes = "返回响应对象")
    public ResponseJson batchUpdateSysFileRole(
            @ApiParam(value = "被修改的对象", required = true)
            @RequestBody SysFileRole sysFileRole){
        sysFileRoleService.batchUpdateSysFileRole(sysFileRole);
        return new ResponseJson();
    }

    @PostMapping("/batchDeleteSysFileRole")
    @ApiOperation(value = "根据id批量删除角色与文件（夹）关系表", notes = "返回响应对象")
    public ResponseJson batchDeleteSysFileRole(
            @ApiParam(value = "根据id批量删除角色与文件（夹）关系表", required = true)
            @RequestBody SysFileRole sysFileRole){
        try{
            sysFileRoleService.batchDeleteSysFileRole(sysFileRole.getRowData());
            return new ResponseJson();
        }catch (Exception e){
            return new ResponseJson(false,"删除失败");
        }

    }

    @GetMapping("/exportSysFileRoleModule")
    @ApiOperation(value = "下载角色与文件（夹）关系表模板", notes = "返回响应对象,不包含总条数", response=SysFileRole.class)
    public void exportSysFileRoleModule(HttpServletResponse response) {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=assetsSort.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileRoleService.exportSysFileRoleModule();
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportSysFileRole")
    @ApiOperation(value = "导出角色与文件（夹）关系表模板", notes = "返回响应对象,不包含总条数", response=SysFileRole.class)
    public void exportSysFileRole(HttpServletResponse response,@RequestBody SysFileRole sysFileRole) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysFileRole.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileRoleService.exportSysFileRole(sysFileRole);
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/importByExcel")
    @ApiOperation(value = "通过excel导入角色与文件（夹）关系表", notes = "返回导入结果")
    public ResponseJson importByExcel(MultipartFile file){
        ExcelInfo excelInfo = sysFileRoleService.uploadSysFileRole(file);
        if(null == excelInfo){
            return new ResponseJson(false,"模板有误");
        }else{
            return new ResponseJson(excelInfo);
        }

    }

    @Log("根据条件获取角色与文件（夹）关系表的所有数据列表")
    @PostMapping("/findSysFileRoleAllList")
    @ApiOperation(value = "根据条件获取角色与文件（夹）关系表的所有数据列表", notes = "返回响应对象,不包含总条数", response=SysFileRole.class)
    public ResponseJson findSysFileRoleAllList(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileRole sysFileRole){
        List<SysFileRole> data=sysFileRoleService.findSysFileRoleAllList(sysFileRole);
        return new ResponseJson(data);
    }

    @GetMapping("/getSysFileRoleTableField")
    @ApiOperation(value = "获取字段详情", notes = "返回响应对象,不包含总条数", response = SysFileRole.class)
    public ResponseJson getSysFileRoleTableField() {
        return new ResponseJson(sysFileRoleService.getSysFileRoleTableField());
    }

    @PostMapping("/exportSysFileRoleByField")
    @ApiOperation(value = "导出汇交登记管理，需要在此数据中，导出移交清单模板", notes = "返回响应对象,不包含总条数", response = SysFileRole.class)
    public void exportSysFileRoleByField(HttpServletResponse response, @RequestBody ExcelParam<SysFileRole> excelParam) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysFileRole.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileRoleService.exportSysFileRoleByField(excelParam.getExcelExportList(),excelParam.getData());
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/showFileTreePermDialog/{roleId}")
    @ApiOperation(value = "根据角色id获取权限树", notes = "返回响应对象")
    public ResponseJson showFileTreePermDialog(@PathVariable("roleId") String roleId){
        SysFile sysFile=new SysFile();
//        sysFile.setIsDelete("0");
        sysFile.setType("0");
        Pager pager = new Pager();
        pager.setPaging(false);
        sysFile.setPager(pager);
        List<SysFile> data=sysFileService.findSysFileListByCondition(sysFile);
        //树
        List<SysFile> sysFiles = ObjectKit.buildTree(data, "-1");
            SysFileRole sysFileRole = new SysFileRole();
            sysFileRole.setSysRoleId(roleId);
            //选中id
            List<SysFileRole> sysFileIdByAdminId = sysFileRoleService.findSysFileRoleListByCondition(sysFileRole);
            List<String> fileIds=new ArrayList<>();
            List<String> filePIds=new ArrayList<>();
            List<String> ids=new ArrayList<>();
            sysFileIdByAdminId.stream().forEach(e->{
                if (!("-1").equals(e.getSysFileParentId())){
                    fileIds.add(e.getSysFileId());
                    filePIds.add(e.getSysFileParentId());
                }
            });
        HashSet hs1 = new HashSet(fileIds);
        HashSet hs2 = new HashSet(filePIds);
        hs1.removeAll(hs2);
        ids.addAll(hs1);
        sysFiles.sort(Comparator.comparing(SysFile::getTitle));
        return new ResponseJson(sysFiles,hs1);
    }

    @PostMapping("/delsertFileRoles")
    @ApiOperation(value = "先删除角色权限,在增加角色权限", notes = "返回响应对象")
    public ResponseJson delsertFileRoles(@RequestBody Map<String,String> map){
        sysFileRoleService.delsertFileRoles(map);
        return new ResponseJson();
    }
}
