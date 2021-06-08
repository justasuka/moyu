package top.cfl.cflwork.controller;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.annotation.Log;
import top.cfl.cflwork.pojo.*;
import top.cfl.cflwork.pojo.admin.SysRole;
import top.cfl.cflwork.service.SysFileLogService;
import top.cfl.cflwork.service.SysFileService;
import top.cfl.cflwork.service.system.sysRole.SysRoleService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@RestController
@RequestMapping("/sysFileLog")
@Api(value = "/sysFileLog",description = "文件操作日志模块")
public class SysFileLogController {
    @Autowired
    private SysFileLogService sysFileLogService;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysRoleService sysRoleService;

    @Log("保存文件操作日志对象")
    @PostMapping("/saveSysFileLog")
    @ApiOperation(value = "保存文件操作日志对象", notes = "返回保存好的文件操作日志对象", response=SysFileLog.class)
    public ResponseJson saveSysFileLog(
            @ApiParam(value = "文件操作日志对象", required = true)
            @RequestBody SysFileLog sysFileLog){
        sysFileLogService.saveSysFileLog(sysFileLog);
        return new ResponseJson(sysFileLog);
    }

    @Log("去更新页面,通过id查找文件操作日志")
    @GetMapping("/update/findSysFileLogById/{id}")
    @ApiOperation(value = "去更新页面,通过id查找文件操作日志", notes = "返回响应对象", response=SysFileLog.class)
    public ResponseJson findSysFileLogById(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String id){
        SysFileLog sysFileLog=sysFileLogService.findSysFileLogById(id);
        return new ResponseJson(sysFileLog);
    }

    @Log("修改文件操作日志对象")
    @PostMapping("/update/updateSysFileLog")
    @ApiOperation(value = "修改文件操作日志对象", notes = "返回响应对象")
    public ResponseJson updateSysFileLog(
            @ApiParam(value = "被修改的文件操作日志对象,对象属性不为空则修改", required = true)
            @RequestBody SysFileLog sysFileLog){
        sysFileLogService.updateSysFileLog(sysFileLog);
        return new ResponseJson();
    }

    @Log("去查看页面,通过id查找文件操作日志")
    @GetMapping("/look/lookSysFileLogById/{id}")
    @ApiOperation(value = "去查看页面,通过id查找文件操作日志", notes = "返回响应对象", response=SysFileLog.class)
    public ResponseJson lookSysFileLogById(
            @ApiParam(value = "去查看页面,需要用到的id", required = true)
            @PathVariable String id){
        SysFileLog sysFileLog=sysFileLogService.findSysFileLogById(id);
        return new ResponseJson(sysFileLog);
    }

    @Log("根据条件查找文件操作日志")
    @PostMapping("/findSysFileLogsByCondition")
    @ApiOperation(value = "根据条件查找文件操作日志", notes = "返回响应对象", response=SysFileLog.class)
    public ResponseJson findSysFileLogsByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileLog sysFileLog){
        if("-1".equals(sysFileLog.getFileId())){
            sysFileLog.setFileId(null);
        }
        if (ObjectUtil.isNull(sysFileLog.getAdminId())){
            sysFileLog.setAdminId(myId());
        }
        List<SysFileLog> data=sysFileLogService.findSysFileLogListByCondition(sysFileLog);
        long count=sysFileLogService.findSysFileLogCountByCondition(sysFileLog);
        return new ResponseJson(data,count);
    }

    @Log("根据条件查找单个文件操作日志,结果必须为单条数据")
    @PostMapping("/findOneSysFileLogByCondition")
    @ApiOperation(value = "根据条件查找单个文件操作日志,结果必须为单条数据", notes = "没有时返回空", response=SysFileLog.class)
    public ResponseJson findOneSysFileLogByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysFileLog sysFileLog){
        SysFileLog one=sysFileLogService.findOneSysFileLogByCondition(sysFileLog);
        return new ResponseJson(one);
    }

    @Log("根据id删除文件操作日志")
    @GetMapping("/deleteSysFileLog/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysFileLog(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysFileLogService.deleteSysFileLog(id);
        return new ResponseJson();
    }

    @Log("根据条件查找文件操作日志列表")
    @PostMapping("/findSysFileLogListByCondition")
    @ApiOperation(value = "根据条件查找文件操作日志列表", notes = "返回响应对象,不包含总条数", response=SysFileLog.class)
    public ResponseJson findSysFileLogListByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileLog sysFileLog){
        List<SysFileLog> data=sysFileLogService.findSysFileLogListByCondition(sysFileLog);
        return new ResponseJson(data);
    }

    @Log("根据对象批量修改文件操作日志")
    @PostMapping("/batchUpdateSysFileLog")
    @ApiOperation(value = "根据id批量修改", notes = "返回响应对象")
    public ResponseJson batchUpdateSysFileLog(
            @ApiParam(value = "被修改的对象", required = true)
            @RequestBody SysFileLog sysFileLog){
        sysFileLogService.batchUpdateSysFileLog(sysFileLog);
        return new ResponseJson();
    }

    @PostMapping("/batchDeleteSysFileLog")
    @ApiOperation(value = "根据id批量删除文件操作日志", notes = "返回响应对象")
    public ResponseJson batchDeleteSysFileLog(
            @ApiParam(value = "根据id批量删除文件操作日志", required = true)
            @RequestBody SysFileLog sysFileLog){
        try{
            sysFileLogService.batchDeleteSysFileLog(sysFileLog.getRowData());
            return new ResponseJson();
        }catch (Exception e){
            return new ResponseJson(false,"删除失败");
        }

    }

    @GetMapping("/exportSysFileLogModule")
    @ApiOperation(value = "下载文件操作日志模板", notes = "返回响应对象,不包含总条数", response=SysFileLog.class)
    public void exportSysFileLogModule(HttpServletResponse response) {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=assetsSort.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileLogService.exportSysFileLogModule();
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportSysFileLog")
    @ApiOperation(value = "导出文件操作日志模板", notes = "返回响应对象,不包含总条数", response=SysFileLog.class)
    public void exportSysFileLog(HttpServletResponse response,@RequestBody SysFileLog sysFileLog) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysFileLog.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileLogService.exportSysFileLog(sysFileLog);
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/importByExcel")
    @ApiOperation(value = "通过excel导入文件操作日志", notes = "返回导入结果")
    public ResponseJson importByExcel(MultipartFile file){
        ExcelInfo excelInfo = sysFileLogService.uploadSysFileLog(file);
        if(null == excelInfo){
            return new ResponseJson(false,"模板有误");
        }else{
            return new ResponseJson(excelInfo);
        }

    }

    @Log("根据条件获取文件操作日志的所有数据列表")
    @PostMapping("/findSysFileLogAllList")
    @ApiOperation(value = "根据条件获取文件操作日志的所有数据列表", notes = "返回响应对象,不包含总条数", response=SysFileLog.class)
    public ResponseJson findSysFileLogAllList(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileLog sysFileLog){
        List<SysFileLog> data=sysFileLogService.findSysFileLogAllList(sysFileLog);
        return new ResponseJson(data);
    }

    @GetMapping("/getSysFileLogTableField")
    @ApiOperation(value = "获取字段详情", notes = "返回响应对象,不包含总条数", response = SysFileLog.class)
    public ResponseJson getSysFileLogTableField() {
        return new ResponseJson(sysFileLogService.getSysFileLogTableField());
    }

    @PostMapping("/exportSysFileLogByField")
    @ApiOperation(value = "导出汇交登记管理，需要在此数据中，导出移交清单模板", notes = "返回响应对象,不包含总条数", response = SysFileLog.class)
    public void exportSysFileLogByField(HttpServletResponse response, @RequestBody ExcelParam<SysFileLog> excelParam) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysFileLog.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileLogService.exportSysFileLogByField(excelParam.getExcelExportList(),excelParam.getData());
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Log("根据文件或文件夹id查找操作日志")
    @PostMapping("/findSysFileLogListBySysFileId")
    @ApiOperation(value = "根据文件或文件夹id查找操作日志", notes = "返回响应对象", response=SysFileLog.class)
    public ResponseJson findSysFileLogListBySysFileId(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileLog sysFileLog){
        List<SysFileLog> data=new ArrayList<>();
        long count=0L;
        if (sysFileLog.getFileId().equals("-1")){
            sysFileLog.setFileId(null);
            List<SysRole> roleByAdminId = sysRoleService.findRoleByAdminId();
            List<String> roleName=new ArrayList<>();
            roleByAdminId.stream().forEach(e->{
                roleName.add(e.getTitle());
            });
            if (!StringUtils.join(roleName,",").contains("超级管理员")){
                sysFileLog.setAdminId(myId());
            }
            data=sysFileLogService.findSysFileLogListByCondition(sysFileLog);
            count=sysFileLogService.findSysFileLogCountByCondition(sysFileLog);
        }else {
        SysFile sysFile = sysFileService.findSysFileById(sysFileLog.getFileId());
        if (ObjectUtil.isNotNull(sysFile)){
            if (sysFile.getType().equals("0")){
                sysFileLog.setFilePath(sysFile.getPath());
                data = sysFileLogService.findSysFileLogByFileId(sysFileLog);
                count = sysFileLogService.findCountSysFileLogByFileId(sysFileLog);
            }else {
                data=sysFileLogService.findSysFileLogListByCondition(sysFileLog);
                count=sysFileLogService.findSysFileLogCountByCondition(sysFileLog);
            }
        }
        }
        return new ResponseJson(data,count);
    }
}
