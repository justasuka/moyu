package top.cfl.cflwork.controller.dd;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.dd.SysDdtype;
import top.cfl.cflwork.service.dd.SysDdtypeService;

import java.util.List;

@RestController
@RequestMapping("/sysDdtype")
@Api(value = "/sysDdtype",description = "字典类型表模块")
public class SysDdtypeController {
    @Autowired
    private SysDdtypeService sysDdtypeService;

    @PostMapping("/saveSysDdtype")
    @ApiOperation(value = "保存字典类型表对象", notes = "返回保存好的字典类型表对象", response=SysDdtype.class)
    public ResponseJson saveSysDdtype(
            @ApiParam(value = "字典类型表对象", required = true)
            @RequestBody SysDdtype sysDdtype){
        sysDdtypeService.saveSysDdtype(sysDdtype);
        return new ResponseJson(sysDdtype);
    }

    @GetMapping("/update/findSysDdtypeById/{id}")
    @ApiOperation(value = "去更新页面,通过id查找字典类型表", notes = "返回响应对象", response=SysDdtype.class)
    public ResponseJson findSysDdtypeById(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String id){
        SysDdtype sysDdtype=sysDdtypeService.findSysDdtypeById(id);
        return new ResponseJson(sysDdtype);
    }

    @PostMapping("/update/updateSysDdtype")
    @ApiOperation(value = "修改字典类型表对象", notes = "返回响应对象")
    public ResponseJson updateSysDdtype(
            @ApiParam(value = "被修改的字典类型表对象,对象属性不为空则修改", required = true)
            @RequestBody SysDdtype sysDdtype){
        sysDdtypeService.updateSysDdtype(sysDdtype);
        return new ResponseJson();
    }

    @GetMapping("/look/lookSysDdtypeById/{id}")
    @ApiOperation(value = "去查看页面,通过id查找字典类型表", notes = "返回响应对象", response=SysDdtype.class)
    public ResponseJson lookSysDdtypeById(
            @ApiParam(value = "去查看页面,需要用到的id", required = true)
            @PathVariable String id){
        SysDdtype sysDdtype=sysDdtypeService.findSysDdtypeById(id);
        return new ResponseJson(sysDdtype);
    }

    @PostMapping("/findSysDdtypesByCondition")
    @ApiOperation(value = "根据条件查找字典类型表", notes = "返回响应对象", response=SysDdtype.class)
    public ResponseJson findSysDdtypesByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDdtype sysDdtype){
        List<SysDdtype> data=sysDdtypeService.findSysDdtypeListByCondition(sysDdtype);
        long count=sysDdtypeService.findSysDdtypeCountByCondition(sysDdtype);
        return new ResponseJson(data,count);
    }
    @PostMapping("/findOneSysDdtypeByCondition")
    @ApiOperation(value = "根据条件查找单个字典类型表,结果必须为单条数据", notes = "没有时返回空", response=SysDdtype.class)
    public ResponseJson findOneSysDdtypeByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysDdtype sysDdtype){
        SysDdtype one=sysDdtypeService.findOneSysDdtypeByCondition(sysDdtype);
        return new ResponseJson(one);
    }
    @GetMapping("/deleteSysDdtype/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysDdtype(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysDdtypeService.deleteSysDdtype(id);
        return new ResponseJson();
    }


    @PostMapping("/findSysDdtypeListByCondition")
    @ApiOperation(value = "根据条件查找字典类型表列表", notes = "返回响应对象,不包含总条数", response=SysDdtype.class)
    public ResponseJson findSysDdtypeListByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysDdtype sysDdtype){
        List<SysDdtype> data=sysDdtypeService.findSysDdtypeListByCondition(sysDdtype);
        return new ResponseJson(data);
    }



}
