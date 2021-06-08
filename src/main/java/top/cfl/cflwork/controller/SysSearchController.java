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
import top.cfl.cflwork.pojo.SysSearch;
import top.cfl.cflwork.service.SysSearchService;
import top.cfl.cflwork.pojo.ExcelParam;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/sysSearch")
@Api(value = "/sysSearch",description = "模块")
public class SysSearchController {
    @Autowired
    private SysSearchService sysSearchService;

    @Log("保存对象")
    @PostMapping("/saveSysSearch")
    @ApiOperation(value = "保存对象", notes = "返回保存好的对象", response=SysSearch.class)
    public ResponseJson saveSysSearch(
            @ApiParam(value = "对象", required = true)
            @RequestBody SysSearch sysSearch){
        sysSearchService.saveSysSearch(sysSearch);
        return new ResponseJson(sysSearch);
    }

    @Log("去更新页面,通过id查找")
    @GetMapping("/update/findSysSearchById/{id}")
    @ApiOperation(value = "去更新页面,通过id查找", notes = "返回响应对象", response=SysSearch.class)
    public ResponseJson findSysSearchById(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String id){
        SysSearch sysSearch=sysSearchService.findSysSearchById(id);
        return new ResponseJson(sysSearch);
    }

    @Log("修改对象")
    @PostMapping("/update/updateSysSearch")
    @ApiOperation(value = "修改对象", notes = "返回响应对象")
    public ResponseJson updateSysSearch(
            @ApiParam(value = "被修改的对象,对象属性不为空则修改", required = true)
            @RequestBody SysSearch sysSearch){
        sysSearchService.updateSysSearch(sysSearch);
        return new ResponseJson();
    }

    @Log("去查看页面,通过id查找")
    @GetMapping("/look/lookSysSearchById/{id}")
    @ApiOperation(value = "去查看页面,通过id查找", notes = "返回响应对象", response=SysSearch.class)
    public ResponseJson lookSysSearchById(
            @ApiParam(value = "去查看页面,需要用到的id", required = true)
            @PathVariable String id){
        SysSearch sysSearch=sysSearchService.findSysSearchById(id);
        return new ResponseJson(sysSearch);
    }

    @Log("根据条件查找")
    @PostMapping("/findSysSearchsByCondition")
    @ApiOperation(value = "根据条件查找", notes = "返回响应对象", response=SysSearch.class)
    public ResponseJson findSysSearchsByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysSearch sysSearch){
        List<SysSearch> data=sysSearchService.findSysSearchListByCondition(sysSearch);
        long count=sysSearchService.findSysSearchCountByCondition(sysSearch);
        return new ResponseJson(data,count);
    }

    @Log("根据条件查找单个,结果必须为单条数据")
    @PostMapping("/findOneSysSearchByCondition")
    @ApiOperation(value = "根据条件查找单个,结果必须为单条数据", notes = "没有时返回空", response=SysSearch.class)
    public ResponseJson findOneSysSearchByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysSearch sysSearch){
        SysSearch one=sysSearchService.findOneSysSearchByCondition(sysSearch);
        return new ResponseJson(one);
    }

    @Log("根据id删除")
    @GetMapping("/deleteSysSearch/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysSearch(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysSearchService.deleteSysSearch(id);
        return new ResponseJson();
    }

    @Log("根据条件查找列表")
    @PostMapping("/findSysSearchListByCondition")
    @ApiOperation(value = "根据条件查找列表", notes = "返回响应对象,不包含总条数", response=SysSearch.class)
    public ResponseJson findSysSearchListByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysSearch sysSearch){
        List<SysSearch> data=sysSearchService.findSysSearchListByCondition(sysSearch);
        return new ResponseJson(data);
    }

    @Log("根据对象批量修改")
    @PostMapping("/batchUpdateSysSearch")
    @ApiOperation(value = "根据id批量修改", notes = "返回响应对象")
    public ResponseJson batchUpdateSysSearch(
            @ApiParam(value = "被修改的对象", required = true)
            @RequestBody SysSearch sysSearch){
        sysSearchService.batchUpdateSysSearch(sysSearch);
        return new ResponseJson();
    }

    @PostMapping("/batchDeleteSysSearch")
    @ApiOperation(value = "根据id批量删除", notes = "返回响应对象")
    public ResponseJson batchDeleteSysSearch(
            @ApiParam(value = "根据id批量删除", required = true)
            @RequestBody SysSearch sysSearch){
        try{
            sysSearchService.batchDeleteSysSearch(sysSearch.getRowData());
            return new ResponseJson();
        }catch (Exception e){
            return new ResponseJson(false,"删除失败");
        }

    }

    @GetMapping("/exportSysSearchModule")
    @ApiOperation(value = "下载模板", notes = "返回响应对象,不包含总条数", response=SysSearch.class)
    public void exportSysSearchModule(HttpServletResponse response) {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=assetsSort.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysSearchService.exportSysSearchModule();
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportSysSearch")
    @ApiOperation(value = "导出模板", notes = "返回响应对象,不包含总条数", response=SysSearch.class)
    public void exportSysSearch(HttpServletResponse response,@RequestBody SysSearch sysSearch) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysSearch.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysSearchService.exportSysSearch(sysSearch);
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/importByExcel")
    @ApiOperation(value = "通过excel导入", notes = "返回导入结果")
    public ResponseJson importByExcel(MultipartFile file){
        ExcelInfo excelInfo = sysSearchService.uploadSysSearch(file);
        if(null == excelInfo){
            return new ResponseJson(false,"模板有误");
        }else{
            return new ResponseJson(excelInfo);
        }

    }

    @Log("根据条件获取的所有数据列表")
    @PostMapping("/findSysSearchAllList")
    @ApiOperation(value = "根据条件获取的所有数据列表", notes = "返回响应对象,不包含总条数", response=SysSearch.class)
    public ResponseJson findSysSearchAllList(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysSearch sysSearch){
        List<SysSearch> data=sysSearchService.findSysSearchAllList(sysSearch);
        return new ResponseJson(data);
    }

    @GetMapping("/getSysSearchTableField")
    @ApiOperation(value = "获取字段详情", notes = "返回响应对象,不包含总条数", response = SysSearch.class)
    public ResponseJson getSysSearchTableField() {
        return new ResponseJson(sysSearchService.getSysSearchTableField());
    }

    @PostMapping("/exportSysSearchByField")
    @ApiOperation(value = "导出汇交登记管理，需要在此数据中，导出移交清单模板", notes = "返回响应对象,不包含总条数", response = SysSearch.class)
    public void exportSysSearchByField(HttpServletResponse response, @RequestBody ExcelParam<SysSearch> excelParam) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysSearch.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysSearchService.exportSysSearchByField(excelParam.getExcelExportList(),excelParam.getData());
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
