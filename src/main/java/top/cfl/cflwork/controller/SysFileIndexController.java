package top.cfl.cflwork.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.cfl.cflwork.annotation.Log;
import top.cfl.cflwork.pojo.Pager;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.SysFile;
import top.cfl.cflwork.pojo.admin.SysRole;
import top.cfl.cflwork.service.SysFileService;
import top.cfl.cflwork.service.system.sysRole.SysRoleService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@RestController
@RequestMapping("/sysFileIndex")
@Api(value = "/sysFileIndex",description = "首页")
public class SysFileIndexController {
    //最近查看，修改，上传，拍摄
    //open_time   update_time create_time shooting_time
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysRoleService sysRoleService;

    @Log("根据条件查找文件管理列表")
    @PostMapping("/findSysFileListByCondition")
    @ApiOperation(value = "根据条件查找文件管理列表", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public ResponseJson findSysFileListByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFile sysFile){
        List<SysFile> data=sysFileService.findSysFileListByCondition(sysFile);
        return new ResponseJson(data);
    }

    @Log("最近上传文件数量")
    @GetMapping("/findSysFileCountByDate")
    @ApiOperation(value = "最近上传文件数量", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public ResponseJson findSysFileCountByDate(){
        long data=0l;
        List list=new ArrayList();
        Integer[] datas = new Integer[]{1,3,5,7,15,30};
        for (Integer integer : datas) {
            Map<String,Object> map=new HashMap<>();
            data=sysFileService.findSysFileCountByDate(integer);
            map.put("key",integer);
            map.put("value",data);
            list.add(map);
        }
        return new ResponseJson(list);
    }

    @Log("根据条件查找最新上传数据")
    @PostMapping("/recentlyuploaded")
    @ApiOperation(value = "根据条件查找最新上传数据", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public ResponseJson recentlyuploaded(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFile sysFile){

        //根据角色监听权限
//        List<SysRole> roleByAdminId = sysRoleService.findRoleByAdminId();
        Map<String, Object> map = roleMap();
        String roleName = (String)map.get("roleName");
        long count=0L;
        List<SysFile> data=new ArrayList<>();
        sysFile.setAdminId(myId());
        if (!roleName.contains("超级管理员")){
            data=sysFileService.findSysFileListByRole(sysFile);
            count = sysFileService.findSysFileCountByRole(sysFile);
        }else {
            data=sysFileService.findSysFileListByCondition(sysFile);
            count = sysFileService.findSysFileCountByCondition(sysFile);
        }
        List<String> pathList=new ArrayList<>();
        //按type重排
        data.sort(Comparator.comparing(SysFile::getType));
        data.stream().forEach(e->{
            if (!"0".equals(e.getType()))
                pathList.add(e.getPath());
        });
        return new ResponseJson(data,count,pathList);
    }

    @Log("月度上传统计")
    @PostMapping("/index/month")
    @ApiOperation(value = "月度上传统计", notes = "返回响应对象,不包含总条数")
    public ResponseJson userPlan() {
        List<Map<String, Object>> userPlanList = sysFileService.findSysFileCountByMonth();
        Collections.reverse(userPlanList);
        for (Map<String, Object> userPlanMap : userPlanList) {
            String year= ((String)userPlanMap.get("month")).split("-")[0];
            String month = ((String) userPlanMap.get("month")).split("-")[1];
            long count = (Long) userPlanMap.get("count");
//            userPlanMap.put("year", year);
//            userPlanMap.put("month", month);
            userPlanMap.put("month", userPlanMap.get("month"));
            userPlanMap.put("count", String.valueOf(count));
        }
        return new ResponseJson(userPlanList);
    }

    @Log("最近查看图片")
    @PostMapping("/recentlyOpen")
    @ApiOperation(value = "最近查看图片", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public ResponseJson recentlyOpen(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFile sysFile){
        //根据角色监听权限
//        SysRole roleByAdminId = sysRoleService.findRoleByAdminId();
        Map<String, Object> map = roleMap();
        String roleName = (String)map.get("roleName");
        long count=0L;
        List<SysFile> data=new ArrayList<>();
        @NotNull(message = "pager不能为空") @Valid Pager pager = sysFile.getPager();
        pager.setSortField("open_time,create_time");
        pager.setSortOrder("desc,desc");
        sysFile.setPager(pager);
        if (!roleName.contains("超级管理员")){
            data=sysFileService.findSysFileListByRole(sysFile);
            count = sysFileService.findSysFileCountByRole(sysFile);
        }else {
            data=sysFileService.findSysFileListByCondition(sysFile);
            count = sysFileService.findSysFileCountByCondition(sysFile);
        }
        List<String> pathList=new ArrayList<>();
        data.stream().forEach(e->{
            if (!"0".equals(e.getType()))
                pathList.add(e.getPath());
        });
        return new ResponseJson(data,count,pathList);
    }

    public Map<String,Object> roleMap(){
        Map map=new HashMap();
        List<SysRole> roleByList = sysRoleService.findRoleByAdminId();
        List<String> roleName=new ArrayList<>();
        List<String> roleID=new ArrayList<>();
        roleByList.stream().forEach(e->{
            roleName.add(e.getTitle());
            roleID.add(e.getId());
        });
        map.put("roleID", StringUtils.join(roleID,","));
        map.put("roleName", StringUtils.join(roleName,","));

        map.put("roleByList",roleByList);
        System.out.println(map);
        return map;
    }
}

