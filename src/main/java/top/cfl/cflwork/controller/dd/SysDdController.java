package top.cfl.cflwork.controller.dd;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.cfl.cflwork.annotation.Log;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.dd.SysDd;
import top.cfl.cflwork.service.dd.SysDdService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/sysDd")
@Api(value = "/sysDd",description = "字典表模块")
public class SysDdController {
    @Autowired
    private SysDdService sysDdService;

    @Log("保存字典表对象")
    @PostMapping("/saveSysDd")
    @ApiOperation(value = "保存字典表对象", notes = "返回保存好的字典表对象", response=SysDd.class)
    public ResponseJson saveSysDd(
            @ApiParam(value = "字典表对象", required = true)
            @RequestBody SysDd sysDd){
        sysDdService.saveSysDd(sysDd);
        return new ResponseJson(sysDd);
    }

    @Log("去更新页面,通过id查找字典表")
    @GetMapping("/update/findSysDdById/{id}")
    @ApiOperation(value = "去更新页面,通过id查找字典表", notes = "返回响应对象", response=SysDd.class)
    public ResponseJson findSysDdById(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String id){
        SysDd sysDd=sysDdService.findSysDdById(id);
        return new ResponseJson(sysDd);
    }

    @Log("修改字典表对象")
    @PostMapping("/update/updateSysDd")
    @ApiOperation(value = "修改字典表对象", notes = "返回响应对象")
    public ResponseJson updateSysDd(
            @ApiParam(value = "被修改的字典表对象,对象属性不为空则修改", required = true)
            @RequestBody SysDd sysDd){
        sysDdService.updateSysDd(sysDd);
        return new ResponseJson();
    }

    @Log("去查看页面,通过id查找字典表")
    @GetMapping("/look/lookSysDdById/{id}")
    @ApiOperation(value = "去查看页面,通过id查找字典表", notes = "返回响应对象", response=SysDd.class)
    public ResponseJson lookSysDdById(
            @ApiParam(value = "去查看页面,需要用到的id", required = true)
            @PathVariable String id){
        SysDd sysDd=sysDdService.findSysDdById(id);
        return new ResponseJson(sysDd);
    }

    @Log("根据条件查找字典表")
    @PostMapping("/findSysDdsByCondition")
    @ApiOperation(value = "根据条件查找字典表", notes = "返回响应对象", response=SysDd.class)
    public ResponseJson findSysDdsByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDd sysDd){
        List<SysDd> data=sysDdService.findSysDdListByCondition(sysDd);
        long count=sysDdService.findSysDdCountByCondition(sysDd);
        return new ResponseJson(data,count);
    }

    @Log("根据条件查找单个字典表,结果必须为单条数据")
    @PostMapping("/findOneSysDdByCondition")
    @ApiOperation(value = "根据条件查找单个字典表,结果必须为单条数据", notes = "没有时返回空", response=SysDd.class)
    public ResponseJson findOneSysDdByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysDd sysDd){
        SysDd one=sysDdService.findOneSysDdByCondition(sysDd);
        return new ResponseJson(one);
    }

    @Log("根据id删除字典表")
    @GetMapping("/deleteSysDd/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysDd(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysDdService.deleteSysDd(id);
        return new ResponseJson();
    }

    @Log("根据条件查找字典表列表")
    @PostMapping("/findSysDdListByCondition")
    @ApiOperation(value = "根据条件查找字典表列表", notes = "返回响应对象,不包含总条数", response=SysDd.class)
    public ResponseJson findSysDdListByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDd sysDd){
        List<SysDd> data=sysDdService.findSysDdListByCondition(sysDd);
        return new ResponseJson(data);
    }

    @Log("根据id批量删除字典表")
    @PostMapping("/batchDeleteSysDd")
    @ApiOperation(value = "根据id批量删除", notes = "返回响应对象")
    public ResponseJson batchDeleteSysDd(
            @ApiParam(value = "被删除的对象", required = true)
            @RequestBody SysDd sysDd){
        sysDdService.batchDeleteSysDd(sysDd.getRowData());
        return new ResponseJson();
    }

    @Log("根据对象批量修改字典表")
    @PostMapping("/batchUpdateSysDd/{id}")
    @ApiOperation(value = "根据id批量修改", notes = "返回响应对象")
    public ResponseJson batchUpdateSysDd(
            @ApiParam(value = "被修改的对象", required = true)
            @RequestBody SysDd sysDd){
        sysDdService.batchUpdateSysDd(sysDd);
        return new ResponseJson();
    }

    @GetMapping("/exportSysDdModule")
    @ApiOperation(value = "下载字典表模板", notes = "返回响应对象,不包含总条数", response=SysDd.class)
    public void exportSysDdModule(HttpServletResponse response) {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=assetsSort.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysDdService.exportSysDdModule();
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportSysDd")
    @ApiOperation(value = "导出字典表模板", notes = "返回响应对象,不包含总条数", response=SysDd.class)
    public void exportSysDd(HttpServletResponse response,@RequestBody SysDd SysDd) {

        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysDd.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysDdService.exportSysDd(SysDd);
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Log("根据条件获取字典表的所有数据列表")
    @PostMapping("/findSysDdAllList")
    @ApiOperation(value = "根据条件获取字典表的所有数据列表", notes = "返回响应对象,不包含总条数", response=SysDd.class)
    public ResponseJson findSysDdAllList(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDd sysDd){
        List<SysDd> data=sysDdService.findSysDdAllList(sysDd);
        return new ResponseJson(data);
    }


}
