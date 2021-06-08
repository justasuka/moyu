package top.cfl.cflwork.controller.system.sysRole;

import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.admin.SysPerm;
import top.cfl.cflwork.pojo.admin.SysRole;
import top.cfl.cflwork.service.system.sysRole.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sysRole")
@Api(value = "/sysRole",description = "系统角色模块")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/findSysRoleById/{id}")
    @ApiOperation(value = "通过id查找系统角色", notes = "返回响应对象")
    public ResponseJson findSysRoleById(
            @ApiParam(value = "需要用到的id", required = true)
            @PathVariable String id){
        SysRole sysRole=sysRoleService.findSysRoleById(id);
        return new ResponseJson(sysRole);
    }

    @PostMapping("/saveSysRole")
    @ApiOperation(value = "保存系统角色对象", notes = "返回响应对象")
    public ResponseJson saveSysRole(
            @ApiParam(value = "系统角色对象", required = true)
            @RequestBody SysRole sysRole){
        sysRoleService.saveSysRole(sysRole);
        return new ResponseJson(sysRole);
    }
    @PostMapping("/updateSysRole")
    @ApiOperation(value = "修改系统角色对象", notes = "返回响应对象")
    public ResponseJson updateSysRole(
            @ApiParam(value = "被修改的系统角色对象,对象属性不为空则修改", required = true)
            @RequestBody SysRole sysRole){
        sysRoleService.updateSysRole(sysRole);
        return new ResponseJson();
    }

    @PostMapping("/findSysRolesByCondition")
    @ApiOperation(value = "根据条件查找系统角色", notes = "返回响应对象")
    public ResponseJson findSysRolesByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysRole sysRole){
        List<SysRole> data=sysRoleService.findSysRoleListByCondition(sysRole);
        long count=sysRoleService.findSysRoleCountByCondition(sysRole);
        return new ResponseJson(data,count);
    }
    @GetMapping("/deleteSysRole/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysRole(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysRoleService.deleteSysRole(id);
        return new ResponseJson();
    }

    @GetMapping("/findSysPermsByRoleId/{roleId}")
    @ApiOperation(value = "根据角色id获取权限树", notes = "返回响应对象")
    public ResponseJson findSysPermsByRoleId(@PathVariable("roleId") String roleId){
        List<SysPerm> perms=sysRoleService.findAllSysPermTree();
        List<String> checked=sysRoleService.findSysPermChecked(roleId);
        return new ResponseJson(perms,checked);
    }

    @PostMapping("/delsertRolePerms")
    @ApiOperation(value = "先删除角色权限,在增加角色权限", notes = "返回响应对象")
    public ResponseJson delsertRolePerms(@RequestBody Map<String,String> map){
        sysRoleService.delsertRolePerms(map);
        return new ResponseJson();
    }


}
