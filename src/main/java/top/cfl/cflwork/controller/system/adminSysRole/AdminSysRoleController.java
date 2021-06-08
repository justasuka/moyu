package top.cfl.cflwork.controller.system.adminSysRole;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.admin.AdminSysRole;
import top.cfl.cflwork.service.system.adminSysRole.AdminSysRoleService;

import java.util.List;

@RestController
@RequestMapping("/adminAdminSysRole")
@Api(value = "/adminAdminSysRole",description = "模块")
public class AdminSysRoleController {
    @Autowired
    private AdminSysRoleService adminAdminSysRoleService;

    @GetMapping("/findAdminSysRoleById/{id}")
    @ApiOperation(value = "通过id查找系统角色", notes = "返回响应对象")
    public ResponseJson findAdminSysRoleById(
            @ApiParam(value = "需要用到的id", required = true)
            @PathVariable String id){
        AdminSysRole adminAdminSysRole=adminAdminSysRoleService.findAdminSysRoleById(id);
        return new ResponseJson(adminAdminSysRole);
    }

    @PostMapping("/saveAdminSysRole")
    @ApiOperation(value = "保存系统角色对象", notes = "返回响应对象")
    public ResponseJson saveAdminSysRole(
            @ApiParam(value = "系统角色对象", required = true)
            @RequestBody AdminSysRole adminAdminSysRole){
        adminAdminSysRoleService.saveAdminSysRole(adminAdminSysRole);
        return new ResponseJson(adminAdminSysRole);
    }
    @PostMapping("/updateAdminSysRole")
    @ApiOperation(value = "修改系统角色对象", notes = "返回响应对象")
    public ResponseJson updateAdminSysRole(
            @ApiParam(value = "被修改的系统角色对象,对象属性不为空则修改", required = true)
            @RequestBody AdminSysRole adminAdminSysRole){
        adminAdminSysRoleService.updateAdminSysRole(adminAdminSysRole);
        return new ResponseJson();
    }

    @PostMapping("/findAdminSysRolesByCondition")
    @ApiOperation(value = "根据条件查找系统角色", notes = "返回响应对象")
    public ResponseJson findAdminSysRolesByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody AdminSysRole adminAdminSysRole){
        List<AdminSysRole> data=adminAdminSysRoleService.findAdminSysRoleListByCondition(adminAdminSysRole);
        long count=adminAdminSysRoleService.findAdminSysRoleCountByCondition(adminAdminSysRole);
        return new ResponseJson(data,count);
    }
    @GetMapping("/deleteAdminSysRole/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteAdminSysRole(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        adminAdminSysRoleService.deleteAdminSysRole(id);
        return new ResponseJson();
    }

}
