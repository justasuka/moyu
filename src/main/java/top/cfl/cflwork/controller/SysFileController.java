package top.cfl.cflwork.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.annotation.Log;
import top.cfl.cflwork.pojo.*;
import top.cfl.cflwork.service.SysFileRoleService;
import top.cfl.cflwork.service.SysFileService;
import top.cfl.cflwork.service.SysSearchService;
import top.cfl.cflwork.service.system.sysRole.SysRoleService;
import top.cfl.cflwork.util.FileUtil;
import top.cfl.cflwork.util.ObjectKit;
import top.cfl.cflwork.pojo.SysFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@RestController
@RequestMapping("/sysFile")
@Api(value = "/sysFile",description = "文件管理模块")
public class SysFileController {
    @Value("${file.filePath}")
    private  String filePath;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private SysFileRoleService sysFileRoleService;
    @Autowired
    private SysSearchService sysSearchService;

    @Log("保存文件管理对象")
    @PostMapping("/saveSysFile")
    @ApiOperation(value = "保存文件管理对象", notes = "返回保存好的文件管理对象", response=SysFile.class)
    public ResponseJson saveSysFile(
            @ApiParam(value = "文件管理对象", required = true)
            @RequestBody SysFile sysFile){
        SysFile sysFile1 = new SysFile();
        sysFile1.setType("0");
        String title = sysFile.getTitle();
        if (title.contains("\\")||title.contains("/")){
            return new ResponseJson(false,"存在非法字符，创建文件夹失败");
        }
        sysFile1.setTitle(title);
        sysFile1.setParentId(sysFile.getId());
        if (sysFile.getId().equals("-1")){
            //根节点
            sysFile1.setPath("/"+title);
            sysFile1.setAddress("/"+title);
        }else {
            SysFile fileById = sysFileService.findSysFileById(sysFile.getId());
            sysFile1.setPath(fileById.getPath()+"/"+title);
            sysFile1.setAddress(fileById.getPath());
        }
        SysFile fileByCondition = sysFileService.findOneSysFileByCondition(sysFile1);
        if (ObjectUtil.isNotNull(fileByCondition)){
          if (fileByCondition.getIsDelete().equals("0")||fileByCondition.getIsDelete().equals("9")) {
            return new ResponseJson(false,"文件夹已存在");//重名
          }else {
              fileByCondition.setIsDelete("9");
              sysFileService.updateSysFile(fileByCondition);
              return new ResponseJson();
          }
        }
        sysFileService.saveSysFile(sysFile1);
        return new ResponseJson(sysFile);
    }

    @Log("去更新页面,通过id查找文件管理")
    @GetMapping("/update/findSysFileById/{id}")
    @ApiOperation(value = "去更新页面,通过id查找文件管理", notes = "返回响应对象", response=SysFile.class)
    public ResponseJson findSysFileById(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String id){
        SysFile sysFile=sysFileService.findSysFileById(id);
        return new ResponseJson(sysFile);
    }

    @Log("修改文件管理对象")
    @PostMapping("/update/updateSysFile")
    @ApiOperation(value = "修改文件管理对象", notes = "返回响应对象")
    public ResponseJson updateSysFile(
            @ApiParam(value = "被修改的文件管理对象,对象属性不为空则修改", required = true)
            @RequestBody SysFile sysFile){
        sysFileService.updateSysFileDelOrRollback(sysFile);
        return new ResponseJson();
    }

    @Log("去查看页面,通过id查找文件管理")
    @GetMapping("/look/lookSysFileById/{id}")
    @ApiOperation(value = "去查看页面,通过id查找文件管理", notes = "返回响应对象", response=SysFile.class)
    public ResponseJson lookSysFileById(
            @ApiParam(value = "去查看页面,需要用到的id", required = true)
            @PathVariable String id){
        SysFile sysFile=sysFileService.findSysFileById(id);
        return new ResponseJson(sysFile);
    }

    @Log("根据条件查找文件管理")
    @PostMapping("/findSysFilesByCondition")
    @ApiOperation(value = "根据条件查找文件管理", notes = "返回响应对象", response=SysFile.class)
    public ResponseJson findSysFilesByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
//            @Validated
            @RequestBody SysFile sysFile){
        String parentId = sysFile.getParentId();
        if (ObjectUtil.isNull(parentId)||("").equals(parentId)){
            sysFile.setParentId("-1");
        }
        if (sysFile.getParentId().equals("-99")){
            sysFile.setParentId(null);
        }
        //根据角色监听权限
        Map<String, Object> map = sysFileService.roleMap();
        String roleName = (String) map.get("roleName");
        String roleID = (String) map.get("roleID");
        //上传文件与文件夹pid无关联 根据address匹配上级目录
        if (ObjectUtil.isNotNull(sysFile.getParentId())&&!sysFile.getParentId().equals("-1")&&!sysFile.getParentId().equals("")){
            SysFile sysFileById = sysFileService.findSysFileById(sysFile.getParentId());
            sysFile.setParentId(null);
            sysFile.setAddress(sysFileById.getPath());
        }
        if(("/").equals(sysFile.getAddress())){
            sysFile.setAddress(null);
            sysFile.setParentId("-1");
        }
        List<SysFile>  data=sysFileService.findSysFileListByCondition(sysFile);
        if (ObjectUtil.isNotNull(sysFile.getRowData())){
           data=sysFileService.findSysFileListByINStatus(sysFile);
        }
        if (!roleName.contains("超级管理员")){
            SysFileRole fileRole = new SysFileRole();
//            fileRole.setSysRoleId(roleId);
            String[] split = roleID.split(",");
            List<SysFileRole> sysFileRoleByRoleId=new ArrayList<>();
            Arrays.stream(split).forEach(e->{
                fileRole.setSysRoleId(e);
                List<SysFileRole> sysFileRoleListByCondition = sysFileRoleService.findSysFileRoleListByCondition(fileRole);
                sysFileRoleByRoleId.addAll(sysFileRoleListByCondition);
            });
            List<String> list=new ArrayList<>();
            sysFileRoleByRoleId.stream().forEach(f->{
                list.add(f.getSysFileId());
            });
            for (int i = data.size()-1; i >= 0; i--) {
                if (data.get(i).getType().equals("0"))
                if (!list.contains(data.get(i).getId())){
                    data.remove(i);
                }
            }
        }
        Map<String,Object> maps=new HashMap<>();
        Map<String,Integer> tagMap=new HashMap<>();
        Map<String,Integer> suffixMap=new HashMap<>();
        Map<String,Integer> shapeMap=new HashMap<>();
        List<Map<String,Object>> shapeMapList =new ArrayList<>();
        List<Map<String,Object>> tagMapList=new ArrayList<>();
        List<Map<String,Object>> suffixMapList=new ArrayList<>();
        List<String> pathList=new ArrayList<>();
        for (SysFile sysFile1 : data) {
            if (!"0".equals(sysFile1.getType()))
                pathList.add(sysFile1.getPath());
            if (ObjectUtil.isNotNull(sysFile1.getTag())&&!sysFile1.getTag().equals(""))
                tagMap = strToMap(tagMap, sysFile1.getTag(), true);
            if (ObjectUtil.isNotNull(sysFile1.getShape())&&!sysFile1.getShape().equals(""))
                shapeMap=strToMap(shapeMap,sysFile1.getShape(),false);
            if (ObjectUtil.isNotNull(sysFile1.getSuffix())&&!sysFile1.getSuffix().equals(""))
                suffixMap=strToMap(suffixMap,sysFile1.getSuffix(),false);
        }
        tagMapList=mapToList(tagMap,tagMapList);
        shapeMapList = mapToList(shapeMap, shapeMapList);
        suffixMapList=mapToList(suffixMap,suffixMapList);

        maps.put("tag",tagMapList);
        maps.put("suffix",suffixMapList);
        maps.put("shape",shapeMapList);
        long count=sysFileService.findSysFileCountByCondition(sysFile);
        if (ObjectUtil.isNotNull(sysFile.getRowData())){
            count=sysFileService.findSysFileCountByINStatus(sysFile);
        }
        if (ObjectUtil.isNotNull(sysFile.getTitle())){
            sysSearchService.updateSysSearchByCondition(sysFile.getTitle());
        }

        return new ResponseJson(data,count,maps,pathList);
    }

    @Log("根据条件查找单个文件管理,结果必须为单条数据")
    @PostMapping("/findOneSysFileByCondition")
    @ApiOperation(value = "根据条件查找单个文件管理,结果必须为单条数据", notes = "没有时返回空", response=SysFile.class)
    public ResponseJson findOneSysFileByCondition(
            @ApiParam(value = "属性不为空则作为条件查询")
            @RequestBody SysFile sysFile){
        SysFile one=sysFileService.findOneSysFileByCondition(sysFile);
        return new ResponseJson(one);
    }

    @Log("根据id删除文件管理")
    @GetMapping("/deleteSysFile/{id}")
    @ApiOperation(value = "根据id删除", notes = "返回响应对象")
    public ResponseJson deleteSysFile(
            @ApiParam(value = "被删除记录的id", required = true)
            @PathVariable String id){
        sysFileService.deleteSysFile(id);
        return new ResponseJson();
    }

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

    @Log("根据对象批量修改文件管理")
    @PostMapping("/batchUpdateSysFile")
    @ApiOperation(value = "根据id批量修改", notes = "返回响应对象")
    public ResponseJson batchUpdateSysFile(
            @ApiParam(value = "被修改的对象", required = true)
            @RequestBody SysFile sysFile){
        sysFileService.batchUpdateSysFile(sysFile);
        return new ResponseJson();
    }

    @PostMapping("/batchDeleteSysFile")
    @ApiOperation(value = "根据id批量删除文件管理", notes = "返回响应对象")
    public ResponseJson batchDeleteSysFile(
            @ApiParam(value = "根据id批量删除文件管理", required = true)
            @RequestBody SysFile sysFile){
        try{
            sysFileService.batchDeleteSysFile(sysFile.getRowData());
            return new ResponseJson();
        }catch (Exception e){
            return new ResponseJson(false,"删除失败");
        }

    }

    @GetMapping("/exportSysFileModule")
    @ApiOperation(value = "下载文件管理模板", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public void exportSysFileModule(HttpServletResponse response) {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=assetsSort.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileService.exportSysFileModule();
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportSysFile")
    @ApiOperation(value = "导出文件管理模板", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public void exportSysFile(HttpServletResponse response,@RequestBody SysFile sysFile) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysFile.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileService.exportSysFile(sysFile);
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/importByExcel")
    @ApiOperation(value = "通过excel导入文件管理", notes = "返回导入结果")
    public ResponseJson importByExcel(MultipartFile file){
        ExcelInfo excelInfo = sysFileService.uploadSysFile(file);
        if(null == excelInfo){
            return new ResponseJson(false,"模板有误");
        }else{
            return new ResponseJson(excelInfo);
        }

    }

    @Log("根据条件获取文件管理的所有数据列表")
    @PostMapping("/findSysFileAllList")
    @ApiOperation(value = "根据条件获取文件管理的所有数据列表", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public ResponseJson findSysFileAllList(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFile sysFile){
        List<SysFile> data=sysFileService.findSysFileAllList(sysFile);
        return new ResponseJson(data);
    }

    @GetMapping("/getSysFileTableField")
    @ApiOperation(value = "获取字段详情", notes = "返回响应对象,不包含总条数", response = SysFile.class)
    public ResponseJson getSysFileTableField() {
        return new ResponseJson(sysFileService.getSysFileTableField());
    }

    @PostMapping("/exportSysFileByField")
    @ApiOperation(value = "导出汇交登记管理，需要在此数据中，导出移交清单模板", notes = "返回响应对象,不包含总条数", response = SysFile.class)
    public void exportSysFileByField(HttpServletResponse response, @RequestBody ExcelParam<SysFile> excelParam) {
        //Workbook w = teacherService.exportTeacher();
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=SysFile.xls");
        try (ServletOutputStream s = response.getOutputStream()) {
            Workbook workbook = sysFileService.exportSysFileByField(excelParam.getExcelExportList(),excelParam.getData());
            workbook.write(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Log("根据条件查找文件管理列表树")
    @GetMapping("/findSysFileListTree")
    @ApiOperation(value = "根据条件查找文件管理列表", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public ResponseJson findSysFileListTree(){
        SysFile sysFile=new SysFile();
//        sysFile.setIsDelete("0");
        sysFile.setType("0");
        List<SysFile> data=sysFileService.findSysFileListByCondition(sysFile);
        return new ResponseJson(ObjectKit.buildTree(data,"-1"));
    }

    @Log("合并去除重复文件列表树")
//    @PostMapping("/reDelRePath")
//    @RepeatSubmit //3秒内阻止重复提交
    @ApiOperation(value = "合并去除重复文件列表树", notes = "返回响应对象,不包含总条数")
    public void reDelRePath(
            @ApiParam(value = "合并去除重复文件列表树", required = true)
            @RequestBody SysFile sysFile){
        sysFileService.reDelRePath(sysFile.getRefilePath());
    }

//    @Log("根据条件查找文件管理")
//    @PostMapping("/findSysFilesByConditions")
//    @ApiOperation(value = "根据条件查找文件管理", notes = "返回响应对象", response=SysFile.class)
//    public ResponseJson findSysFilesByConditions(
//            @ApiParam(value = "属性不为空则作为条件查询")
//            @Validated
//            @RequestBody SysFile sysFile){
//        if (ObjectUtil.isNotNull(sysFile.getParentId())&&!sysFile.getParentId().equals("-1")&&!sysFile.getParentId().equals("")){
//            SysFile sysFileById = sysFileService.findSysFileById(sysFile.getParentId());
//            sysFile.setParentId(null);
//            sysFile.setAddress(sysFileById.getPath());
//        }
//        List<SysFile> data=sysFileService.findSysFileListByCondition(sysFile);
//        long count=sysFileService.findSysFileCountByCondition(sysFile);
//        return new ResponseJson(data,count);
//    }

//    @Log("查找当前id及其下级所有id")
////    @GetMapping("/getSysFileChildrenList/{id}")
//    @ApiOperation(value = "查找当前id及其下级所有id", notes = "返回响应对象", response=SysFile.class)
//    public ResponseJson getSysFileChildrenList(
//            @ApiParam(value = "去更新页面,需要用到的id", required = true)
//            @PathVariable String id){
//        String sysFileIds=sysFileService.getSysFileChildrenList(id);
//        return new ResponseJson(sysFileIds);
//    }


//    @Log("扫描录入系统")
//    @GetMapping("/scanSysFilepath")
//    @ApiOperation(value = "扫描录入系统", notes = "返回响应对象", response=SysFile.class)
//    public ResponseJson scanSysFilepath(
//            @ApiParam(value = "去更新页面,需要用到的id", required = true) MultipartFile file,String parentId,String relativePath){

//    }

    @Log("完成上传后回调函数，整理文件夹对应关系")
    @GetMapping("/reloadDirByparentId/{parentId}")
    @ApiOperation(value = "完成上传后回调函数，整理文件夹对应关系", notes = "返回响应对象", response=SysFile.class)
    public ResponseJson reloadDirByparentId(
            @ApiParam(value = "去更新页面,需要用到的id", required = true)
            @PathVariable String parentId){
        sysFileService.reloadDirByparentId(parentId);
        return new ResponseJson();
    }

    @Log("录入数据库")
    @PostMapping("/buildSysFiles")
    @ApiOperation(value = "录入数据库", notes = "返回响应对象", response=SysFile.class)
    public ResponseJson buildSysFiles(
            @ApiParam(value = "录入数据库", required = true)
            @RequestBody SysFile sysFile){
//        boolean flag=false;
        if (ObjectUtil.isNotNull(sysFile.getPath())){
            return sysFileService.buildSysFiles(filePath+sysFile.getPath().substring(1));
        }
        else {//根目录扫描
           return sysFileService.buildSysFiles(filePath);
        }
//        if (!flag){
//        }
//        sysFileService.uploaderFiles("D:/picture","-1");
//        return new ResponseJson(sysFile);
    }

    @Log("保存文件")
    @PostMapping("/uploadSysFiles")
    @ApiOperation(value = "查找当前id及其下级所有id", notes = "返回响应对象", response=SysFile.class)
    public ResponseJson uploadSysFiles(@ApiParam(value = "上传文件到本地", required = true) MultipartFile file,String parentId,String relativePath,boolean isReplace){
        fileUtil.uploadFiles(file,relativePath);
        return new ResponseJson();
    }

    @Log("根据条件查找文件管理列表树")
    @PostMapping("/findSysFileListTreeByRole")
    @ApiOperation(value = "根据条件查找文件管理列表", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public ResponseJson findSysFileListTreeByRole(){
        List<SysFile> data=new ArrayList<>();
        Map<String, Object> map = sysFileService.roleMap();
        String roleName = (String) map.get("roleName");
        String roleID = (String) map.get("roleID");
        if (!roleName.contains("超级管理员")){
            String[] split = roleID.split(",");
            List<SysFileRole> sysFileRoleByRoleId = sysFileRoleService.findSysFileRoleByRoleId(split);
            for (SysFileRole fileRole : sysFileRoleByRoleId) {
                SysFile fileById = sysFileService.findSysFileById(fileRole.getSysFileId());
                if (ObjectUtil.isNotNull(fileById))
                    if (fileById.getIsDelete().equals("0")||"9".equals(fileById.getIsDelete()))
                        data.add(fileById);
            }
           data= data.stream().distinct().collect(Collectors.toList());
        }else {
            SysFile sysFile=new SysFile();
            sysFile.setIsDelete("0");
            sysFile.setType("0");
            data=sysFileService.findSysFileListByCondition(sysFile);
            sysFile.setIsDelete("9");
            List<SysFile> sysFileListByCondition = sysFileService.findSysFileListByCondition(sysFile);
            for (SysFile file : sysFileListByCondition) {
                data.add(file);
            }
        }
        List<SysFile> data1 = ObjectKit.buildTree(data, "-1");
        data1.sort(Comparator.comparing(SysFile::getTitle));
        return new ResponseJson(data1);
    }
    public  List<Map<String,Object>>  mapToList(Map<String,Integer> map, List<Map<String,Object>>  maps){
        Set<String> strList = map.keySet();
        strList.stream().forEach(e->{
            Map<String,Object> tagMap=new HashMap<>();
            tagMap.put("name",e);
            tagMap.put("value",map.get(e));
            maps.add(tagMap);
        });
        return maps;
    }
    public Map<String,Integer> strToMap(Map<String,Integer> map,String str,boolean flag){
        if (flag){
            String[] split = str.split(",");
            for (String s : split) {
                Integer integer = map.get(s);
                if (ObjectUtil.isNull(integer)){
                    map.put(s,1);
                }else {
                    map.put(s,integer+1);
                }
            }
        }else {
            Integer integer = map.get(str);
            if (ObjectUtil.isNull(integer)){
                map.put(str,1);
            }else {
                map.put(str,integer+1);
            }
        }
        return map;
    }


    @Log("搜索界面根据条件查找文件管理列表")
    @PostMapping("/findSysFileListByMy")
    @ApiOperation(value = "搜索界面根据条件查找文件管理列表", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public ResponseJson findSysFileListByMy(
            @ApiParam(value = "属性不为空则作为条件查询")
            @Validated
            @RequestBody SysFile sysFile
           ){
//        System.out.println(pager1);
        //根据角色监听权限
//        SysRole roleByAdminId = sysRoleService.findRoleByAdminId();
        Map<String, Object> map = sysFileService.roleMap();
        String roleName = (String) map.get("roleName");
        String roleID = (String) map.get("roleID");
        if (!roleName.contains("超级管理员")){
            List<SysFileRole> sysFileRoleByRoleId = sysFileRoleService.findSysFileRoleByRoleId(            roleID.split(",")
            );
            if (sysFileRoleByRoleId.size()==0){
                return new ResponseJson();
            }
        }
        if (sysFile.getAddress().equals("/")){
            sysFile.setAddress(null);
        }
        if (sysFile.getParentId().equals("-99")){
            sysFile.setParentId(null);
        }
        List<String> pathList=new ArrayList<>();
        long count=0L;
        if (sysFile.getNameType().equals(0)){//我上传
            if (!roleName.contains("超级管理员")){
                sysFile.setAdminId(myId());
            }
        }else if (sysFile.getNameType().equals(2)){//最新上传
            @NotNull(message = "pager不能为空") @Valid Pager pager = sysFile.getPager();
            pager.setSortField("create_time");
            pager.setSortOrder("desc");
            sysFile.setPager(pager);
        }else {//推荐
            List<SysFile> data=new ArrayList<>();
            Pager pager = sysFile.getPager();
            pager.setPageSize(4);
            sysFile.setPager(pager);
            List<SysSearch> sysSearchRecentlyList = sysSearchService.findSysSearchRecentlyList();
            if (roleName.contains("超级管理员")){
                if (ObjectUtil.isNull(sysFile.getAddress())||"/".equals(sysFile.getAddress())){
                    if(ObjectUtil.isNull(sysFile.getTitle())){
                    //最多搜索3条
                    for (SysSearch e : sysSearchRecentlyList) {
                        sysFile.setTitle(e.getSearchContent());
                        //取4条数据
                        List<SysFile> sysFileListByCondition = sysFileService.findSysFileListByINStatus(sysFile);
                        data.addAll(sysFileListByCondition);
                    }

                        //增加最近浏览10条
                        List<SysFile> sysFileOpenTime = sysFileService.findSysFileOpenTime();
                        data.addAll(sysFileOpenTime);
                        sysFile.setTitle(null);
                    }else {
                        pager.setPageSize(30);
                        sysFile.setPager(pager);
                        data=sysFileService.findSysFileListByINStatus(sysFile);
                        count = sysFileService.findSysFileCountByINStatus(sysFile);
                    }
                }else {
                    pager.setPageSize(30);
                    sysFile.setPager(pager);
                    data=sysFileService.findSysFileListByINStatus(sysFile);
                    count = sysFileService.findSysFileCountByINStatus(sysFile);
                }
            }else {
                if(ObjectUtil.isNull(sysFile.getTitle())) {
                    for (SysSearch e : sysSearchRecentlyList) {
                    sysFile.setTitle(e.getSearchContent());
                    //取4条数据
                    List<SysFile> sysFileListByCondition = sysFileService.findSysFileListByRole(sysFile);
                    data.addAll(sysFileListByCondition);
                    }
                }else {
                    pager.setPageSize(30);
                    sysFile.setPager(pager);
                    data=sysFileService.findSysFileListByRole(sysFile);
                    count = sysFileService.findSysFileCountByRole(sysFile);
                }
            }
            //按type重排
            data.sort(Comparator.comparing(SysFile::getType));
            data.stream().forEach(e->{
                if (!"0".equals(e.getType()))
                    pathList.add(e.getPath());
            });
            if (ObjectUtil.isNotNull(sysFile.getTitle())){
                sysSearchService.updateSysSearchByCondition(sysFile.getTitle());
            }
            if (count==0L){
                return new ResponseJson(data,data.size(),pathList);
            }
            return new ResponseJson(data,count,pathList);
        }
        List<SysFile> data=new ArrayList<>();
        if (!roleName.contains("超级管理员")){
            data=sysFileService.findSysFileListByRole(sysFile);
            count = sysFileService.findSysFileCountByRole(sysFile);
        }else {
            data=sysFileService.findSysFileListByINStatus(sysFile);
            count = sysFileService.findSysFileCountByINStatus(sysFile);
        }
        //按type重排
        data.sort(Comparator.comparing(SysFile::getType));
        data.stream().forEach(e->{
            if (!"0".equals(e.getType()))
                pathList.add(e.getPath());
        });
        if (ObjectUtil.isNotNull(sysFile.getTitle())){
            sysSearchService.updateSysSearchByCondition(sysFile.getTitle());
        }
        return new ResponseJson(data,count,pathList);
    }

    @Log("修改打开时间")
    @GetMapping("/updateOpenTime/{id}")
    @ApiOperation(value = "修改文件管理对象", notes = "返回响应对象")
    public ResponseJson updateOpenTime(
            @ApiParam(value = "被修改的文件管理对象,对象属性不为空则修改", required = true)
            @PathVariable String id){
        SysFile fileById = sysFileService.findSysFileById(id);
        fileById.setOpenTime(DateUtil.now());
        sysFileService.updateSysFileWithOutUpdatetime(fileById);
        return new ResponseJson();
    }

    @Log("修改文件管理对象")
    @PostMapping("/update/updateSysFileByConent")
    @ApiOperation(value = "修改文件管理对象", notes = "返回响应对象")
    public ResponseJson updateSysFileByConent(
            @ApiParam(value = "被修改的文件管理对象,对象属性不为空则修改", required = true)
            @RequestBody SysFile sysFile){
        String title = sysFile.getTitle();
        if (title.contains("\\")||title.contains("/")){
            return new ResponseJson(false,"存在非法字符，修改失败");
        }
        if (ObjectUtil.isNull(title)||title.equals("")){
            return new ResponseJson(false,"文件或文件夹名称不能为空");
        }
        if (!sysFile.getType().equals("0")){
            String substring = title.substring(title.lastIndexOf(".")+1);
            if (!substring.toLowerCase().equals(sysFile.getSuffix().toLowerCase())){
                return new ResponseJson(false,"不支持修改文件后缀");
            }
        }
        SysFile fileById = sysFileService.findSysFileById(sysFile.getId());
        if(fileById.getTitle().equals(title)){
            return new ResponseJson();
        }
            File file = new File(filePath + sysFile.getAddress() + "/"+title);
            if (file.exists()){
                return new ResponseJson(false,"文件或文件夹已存在，不可重复命名");
            }
            //没更新前需要修改下级path和address
            sysFileService.updateFilePath(fileById,title);
        return new ResponseJson();
    }

    @Log("获取当前路径")
    @GetMapping("/look/lookSysFileAbPath/{id}")
    @ApiOperation(value = "获取当前路径", notes = "返回响应对象", response=SysFile.class)
    public ResponseJson lookSysFileAbPath(
            @ApiParam(value = "获取当前路径,需要用到的id", required = true)
            @PathVariable String id){
        SysFile sysFile=sysFileService.findSysFileById(id);
        if (ObjectUtil.isNull(sysFile))
            return new ResponseJson(filePath.substring(0,filePath.length()-1));
        return new ResponseJson(filePath+sysFile.getPath().substring(1));
    }

    @Log("查询回收站数据")
    @PostMapping("/findSysFileListByConditionRecycleBin")
    @ApiOperation(value = "查询回收站数据", notes = "返回响应对象,不包含总条数", response=SysFile.class)
    public ResponseJson findSysFileListByConditionRecycleBin(
            @ApiParam(value = "查询回收站数据")
            @Validated
            @RequestBody SysFile sysFile){
        Map map = sysFileService.roleMap();
        String roleName = (String) map.get("roleName");
        String roleID = (String) map.get("roleID");
        List<String> rolePath=new ArrayList<>();//权限路径集合
        List<String> rolePPath=new ArrayList<>();//权限父路径集合
        if (!roleName.contains("超级管理员")){
            String[] split = roleID.split(",");
            List<SysFileRole> sysFileRoleByRoleId = sysFileRoleService.findSysFileRoleByRoleId(split);
            if (sysFileRoleByRoleId.size()==0){
                return new ResponseJson();
            }
            sysFileRoleByRoleId.stream().forEach(e->{
                    SysFile fileById = sysFileService.findSysFileById(e.getSysFileId());
                    if (ObjectUtil.isNotNull(fileById)){
                        rolePath.add(fileById.getPath());
                        if ("-1".equals(fileById.getParentId())){
                            rolePPath.add(fileById.getPath());
                        }
                    }
            });
            String[] strings = rolePath.toArray(new String[rolePath.size()]);
            String[] string = rolePPath.toArray(new String[rolePPath.size()]);
            sysFile.setRolePath(strings);
            sysFile.setRowData(string);
//            if (rolePath.size()==0)
//                sysFile.setRolePath(null);
//            if (rolePPath.size()==0)
//                sysFile.setRowData(null);
            List<SysFile> data=sysFileService.findSysFileListByRecycleBinRole(sysFile);
            long count = sysFileService.findCountByRecycleBinRole(sysFile);
            return new ResponseJson(data,count);
        }
        List<SysFile> data=sysFileService.findSysFileListByConditionRecycleBin(sysFile);
        long count = sysFileService.findCountByConditionRecycleBin(sysFile);
        return new ResponseJson(data,count);
    }
}
