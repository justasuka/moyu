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
import top.cfl.cflwork.pojo.*;
import top.cfl.cflwork.service.SysFileCollectService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/sysFileCollect")
@Api(value = "/sysFileCollect",description = "用户收藏管理模块")
public class SysFileCollectController {
    @Autowired
    private SysFileCollectService sysFileCollectService;

    @Log("保存用户收藏管理对象")
    @PostMapping("/saveSysFileCollect")
    @ApiOperation(value = "保存用户收藏管理对象", notes = "返回保存好的用户收藏管理对象", response=SysFileCollect.class)
    public ResponseJson saveSysFileCollect(
            @ApiParam(value = "用户收藏管理对象", required = true)
            @RequestBody SysFileCollect sysFileCollect){
        sysFileCollectService.saveSysFileCollect(sysFileCollect);
        return new ResponseJson(sysFileCollect);
    }

    @Log("去更新页面,通过id查找用户收藏管理")
    @GetMapping("/update/findSysFileCollectById/{id}")
    @ApiOperation(value = "去更新页面,通过id查找用户收藏管理", notes = "返回响应对象", response=SysFileCollect.class)
    public ResponseJson findSysFileCollectById(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String id){
        SysFileCollect sysFileCollect=sysFileCollectService.findSysFileCollectById(id);
        return new ResponseJson(sysFileCollect);
    }

    @Log("修改用户收藏管理对象")
    @PostMapping("/update/updateSysFileCollect")
    @ApiOperation(value = "修改用户收藏管理对象", notes = "返回响应对象")
    public ResponseJson updateSysFileCollect(
            @ApiParam(value = "被修改的用户收藏管理对象,对象属性不为空则修改", required = true)
            @RequestBody SysFileCollect sysFileCollect){
        sysFileCollectService.updateSysFileCollect(sysFileCollect);
        return new ResponseJson();
    }

    @Log("去查看页面,通过id查找用户收藏管理")
    @GetMapping("/look/lookSysFileCollectById/{id}")
    @ApiOperation(value = "去查看页面,通过id查找用户收藏管理", notes = "返回响应对象", response=SysFileCollect.class)
    public ResponseJson lookSysFileCollectById(
            @ApiParam(value = "去查看页面,需要用到的id", required = true)
            @PathVariable String id){
        SysFileCollect sysFileCollect=sysFileCollectService.findSysFileCollectById(id);
        return new ResponseJson(sysFileCollect);
    }

    @Log("根据条件查找用户收藏管理")
    @PostMapping("/findSysFileCollectsByCondition")
    @ApiOperation(value = "根据条件查找用户收藏管理", notes = "返回响应对象", response=SysFileCollect.class)
    public ResponseJson findSysFileCollectsByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileCollect sysFileCollect){
        List<SysFileCollect> data=sysFileCollectService.findSysFileCollectListByCondition(sysFileCollect);
        long count=sysFileCollectService.findSysFileCollectCountByCondition(sysFileCollect);
        return new ResponseJson(data,count);
    }

    @Log("根据条件查找单个用户收藏管理,结果必须为单条数据")
    @PostMapping("/findOneSysFileCollectByCondition")
    @ApiOperation(value = "根据条件查找单个用户收藏管理,结果必须为单条数据", notes = "没有时返回空", response=SysFileCollect.class)
    public ResponseJson findOneSysFileCollectByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysFileCollect sysFileCollect){
        SysFileCollect one=sysFileCollectService.findOneSysFileCollectByCondition(sysFileCollect);
        return new ResponseJson(one);
    }

    @Log("根据id删除用户收藏管理")
    @GetMapping("/deleteSysFileCollect/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysFileCollect(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysFileCollectService.deleteSysFileCollect(id);
        return new ResponseJson();
    }

    @Log("根据条件查找用户收藏管理列表")
    @PostMapping("/findSysFileCollectListByCondition")
    @ApiOperation(value = "根据条件查找用户收藏管理列表", notes = "返回响应对象,不包含总条数", response=SysFileCollect.class)
    public ResponseJson findSysFileCollectListByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileCollect sysFileCollect){
        List<SysFileCollect> data=sysFileCollectService.findSysFileCollectListByCondition(sysFileCollect);
        return new ResponseJson(data);
    }

    @Log("根据对象批量修改用户收藏管理")
    @PostMapping("/batchUpdateSysFileCollect")
    @ApiOperation(value = "根据id批量修改", notes = "返回响应对象")
    public ResponseJson batchUpdateSysFileCollect(
            @ApiParam(value = "被修改的对象", required = true)
            @RequestBody SysFileCollect sysFileCollect){
        sysFileCollectService.batchUpdateSysFileCollect(sysFileCollect);
        return new ResponseJson();
    }

    @PostMapping("/batchDeleteSysFileCollect")
    @ApiOperation(value = "根据id批量删除用户收藏管理", notes = "返回响应对象")
    public ResponseJson batchDeleteSysFileCollect(
            @ApiParam(value = "根据id批量删除用户收藏管理", required = true)
            @RequestBody SysFileCollect sysFileCollect){
        try{
            sysFileCollectService.batchDeleteSysFileCollect(sysFileCollect.getRowData());
            return new ResponseJson();
        }catch (Exception e){
            return new ResponseJson(false,"删除失败");
        }

    }

    @GetMapping("/exportSysFileCollectModule")
    @ApiOperation(value = "下载用户收藏管理模板", notes = "返回响应对象,不包含总条数", response=SysFileCollect.class)
    public void exportSysFileCollectModule(HttpServletResponse response) {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=assetsSort.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileCollectService.exportSysFileCollectModule();
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportSysFileCollect")
    @ApiOperation(value = "导出用户收藏管理模板", notes = "返回响应对象,不包含总条数", response=SysFileCollect.class)
    public void exportSysFileCollect(HttpServletResponse response,@RequestBody SysFileCollect sysFileCollect) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysFileCollect.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileCollectService.exportSysFileCollect(sysFileCollect);
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/importByExcel")
    @ApiOperation(value = "通过excel导入用户收藏管理", notes = "返回导入结果")
    public ResponseJson importByExcel(MultipartFile file){
        ExcelInfo excelInfo = sysFileCollectService.uploadSysFileCollect(file);
        if(null == excelInfo){
            return new ResponseJson(false,"模板有误");
        }else{
            return new ResponseJson(excelInfo);
        }

    }

    @Log("根据条件获取用户收藏管理的所有数据列表")
    @PostMapping("/findSysFileCollectAllList")
    @ApiOperation(value = "根据条件获取用户收藏管理的所有数据列表", notes = "返回响应对象,不包含总条数", response=SysFileCollect.class)
    public ResponseJson findSysFileCollectAllList(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFileCollect sysFileCollect){
        List<SysFileCollect> data=sysFileCollectService.findSysFileCollectAllList(sysFileCollect);
        return new ResponseJson(data);
    }

    @GetMapping("/getSysFileCollectTableField")
    @ApiOperation(value = "获取字段详情", notes = "返回响应对象,不包含总条数", response = SysFileCollect.class)
    public ResponseJson getSysFileCollectTableField() {
        return new ResponseJson(sysFileCollectService.getSysFileCollectTableField());
    }

    @PostMapping("/exportSysFileCollectByField")
    @ApiOperation(value = "导出汇交登记管理，需要在此数据中，导出移交清单模板", notes = "返回响应对象,不包含总条数", response = SysFileCollect.class)
    public void exportSysFileCollectByField(HttpServletResponse response, @RequestBody ExcelParam<SysFileCollect> excelParam) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysFileCollect.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileCollectService.exportSysFileCollectByField(excelParam.getExcelExportList(),excelParam.getData());
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Log("初始化查找所有本人收藏")
    @PostMapping("/findSysFileCollectsCreate")
    @ApiOperation(value = "初始化查找所有本人收藏", notes = "返回响应对象", response=SysFileCollect.class)
    public ResponseJson findSysFileCollectsCreate(
            @ApiParam(value = "初始化查找所有本人收藏")
            @Validated
            @RequestBody SysFileCollect sysFileCollect){
        //初始化查找所有本人收藏
        List<SysFile> data = sysFileCollectService.findSysFileCollectsCreate(sysFileCollect);
        long count=sysFileCollectService.findSysFileCollectCountByCondition(sysFileCollect);
        return new ResponseJson(data,count);
    }
}
