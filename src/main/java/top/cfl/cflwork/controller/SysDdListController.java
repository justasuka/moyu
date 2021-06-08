package top.cfl.cflwork.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.cfl.cflwork.pojo.*;
import top.cfl.cflwork.pojo.admin.Admin;
import top.cfl.cflwork.pojo.dd.SysDd;
import top.cfl.cflwork.pojo.dd.SysDdtype;
import top.cfl.cflwork.service.*;
import top.cfl.cflwork.service.dd.SysDdService;
import top.cfl.cflwork.service.dd.SysDdtypeService;
import top.cfl.cflwork.service.system.admin.AdminService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static top.cfl.cflwork.interceptor.LoginInterceptor.currentAdmin;
import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@RestController
@RequestMapping("/sysDdList")
@Api(value = "/sysDdList",description = "字典表模块")
public class SysDdListController {
    @Autowired
    private SysDdService sysDdService;
    @Autowired
    private SysDepartmentService sysDepartmentService;
    @GetMapping("/findDepartMentsListByCondition")
    @ApiOperation(value = "获取部门列表", notes = "返回响应对象,不包含总条数", response=SysDd.class)
    public ResponseJson findDepartMentsListByCondition(){
        SysDepartment sysDepartment = new SysDepartment();
        sysDepartment.setPager(new Pager().setPaging(false));
        return new ResponseJson(sysDepartmentService.findSysDepartmentListByCondition(sysDepartment));
    }
    @GetMapping("/findJobListByCondition")
    @ApiOperation(value = "获取职务列表", notes = "返回响应对象,不包含总条数", response=SysDd.class)
    public ResponseJson findJobListByCondition(){
        return new ResponseJson(getList(Constant.DD.JOB_CODE));
    }
    public List<SysDd> getList(String code){
        SysDd sysDd = new SysDd();
        sysDd.setTypeCode(code);
        sysDd.setPager(new Pager().setPaging(false));
        List<SysDd> data=sysDdService.findSysDdListByCondition(sysDd);
        return data;
    }
}
