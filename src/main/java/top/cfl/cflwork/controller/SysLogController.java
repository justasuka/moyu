package top.cfl.cflwork.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.cfl.cflwork.annotation.Log;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.SysLog;
import top.cfl.cflwork.service.SysLogService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/sysLog")
@Api(value = "/sysLog",description = "系统日志模块")
public class SysLogController {
    @Autowired
    private SysLogService sysLogService;

    @Log("保存系统日志对象")
    @PostMapping("/saveSysLog")
    @ApiOperation(value = "保存系统日志对象", notes = "返回保存好的系统日志对象", response=SysLog.class)
    public ResponseJson saveSysLog(
            @ApiParam(value = "系统日志对象", required = true)
            @RequestBody SysLog sysLog){
        sysLogService.saveSysLog(sysLog);
        return new ResponseJson(sysLog);
    }

    @Log("去更新页面,通过id查找系统日志")
    @GetMapping("/update/findSysLogById/{id}")
    @ApiOperation(value = "去更新页面,通过id查找系统日志", notes = "返回响应对象", response=SysLog.class)
    public ResponseJson findSysLogById(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String id){
        SysLog sysLog=sysLogService.findSysLogById(id);
        return new ResponseJson(sysLog);
    }

    @Log("修改系统日志对象")
    @PostMapping("/update/updateSysLog")
    @ApiOperation(value = "修改系统日志对象", notes = "返回响应对象")
    public ResponseJson updateSysLog(
            @ApiParam(value = "被修改的系统日志对象,对象属性不为空则修改", required = true)
            @RequestBody SysLog sysLog){
        sysLogService.updateSysLog(sysLog);
        return new ResponseJson();
    }

    @Log("去查看页面,通过id查找系统日志")
    @GetMapping("/look/lookSysLogById/{id}")
    @ApiOperation(value = "去查看页面,通过id查找系统日志", notes = "返回响应对象", response=SysLog.class)
    public ResponseJson lookSysLogById(
            @ApiParam(value = "去查看页面,需要用到的id", required = true)
            @PathVariable String id){
        SysLog sysLog=sysLogService.findSysLogById(id);
        return new ResponseJson(sysLog);
    }

    @Log("根据条件查找系统日志")
    @PostMapping("/findSysLogsByCondition")
    @ApiOperation(value = "根据条件查找系统日志", notes = "返回响应对象", response=SysLog.class)
    public ResponseJson findSysLogsByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysLog sysLog){
        //按要求，只显三天内的日志
        List<SysLog> data=sysLogService.findSysLogListByCondition(sysLog);
        long count=sysLogService.findSysLogCountByCondition(sysLog);
        return new ResponseJson(data,count);
    }

    @Log("根据条件查找单个系统日志,结果必须为单条数据")
    @PostMapping("/findOneSysLogByCondition")
    @ApiOperation(value = "根据条件查找单个系统日志,结果必须为单条数据", notes = "没有时返回空", response=SysLog.class)
    public ResponseJson findOneSysLogByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysLog sysLog){
        SysLog one=sysLogService.findOneSysLogByCondition(sysLog);
        return new ResponseJson(one);
    }

    @Log("根据id删除系统日志")
    @GetMapping("/deleteSysLog/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysLog(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysLogService.deleteSysLog(id);
        return new ResponseJson();
    }

    @Log("根据条件查找系统日志列表")
    @PostMapping("/findSysLogListByCondition")
    @ApiOperation(value = "根据条件查找系统日志列表", notes = "返回响应对象,不包含总条数", response=SysLog.class)
    public ResponseJson findSysLogListByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysLog sysLog){
        List<SysLog> data=sysLogService.findSysLogListByCondition(sysLog);
        return new ResponseJson(data);
    }
    @PostMapping("/exportSysLog")
    @ApiOperation(value = "导出功能描述：系统日志模板", notes = "返回响应对象,不包含总条数", response=SysLog.class)
    public void exportSysLog(HttpServletResponse response,@RequestBody SysLog sysLog) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysLog.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysLogService.exportSysLog(sysLog);
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
