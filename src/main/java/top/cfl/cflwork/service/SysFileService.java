package top.cfl.cflwork.service;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.dao.SysFileDao;
import top.cfl.cflwork.dao.SysFileLogDao;
import top.cfl.cflwork.dao.SysFileRoleDao;
import top.cfl.cflwork.dao.admin.sysRole.SysRoleDao;
import top.cfl.cflwork.pojo.*;
import top.cfl.cflwork.pojo.admin.SysRole;
import top.cfl.cflwork.util.ExcelStyleUtil;
import top.cfl.cflwork.util.FileTypeUtil;
import top.cfl.cflwork.util.SequenceId;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static top.cfl.cflwork.interceptor.LoginInterceptor.currentAdmin;
import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;
import static top.cfl.cflwork.util.ObjectUtil.beanToMap;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import org.apache.commons.lang3.StringUtils;
import static top.cfl.cflwork.util.CommonUtil.camelCaseName;
@Service
@Component
public class SysFileService {
    @Value("${file.filePath}")
    private  String filePath;
    @Autowired
    private SysFileDao sysFileDao;
    @Autowired
    private SequenceId sequenceId;
    @Autowired
    private FileTypeUtil fileTypeUtil;
    @Autowired
    private SysFileLogDao sysFileLogDao;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysFileRoleDao sysFileRoleDao;

    @Transactional(readOnly = true)
    public SysFile findSysFileById(String id) {
        return sysFileDao.findSysFileById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysFile(SysFile sysFile) {
        sysFile.setId(sequenceId.nextId());
        sysFile.setIsDelete("0");
        sysFile.setAdminId(myId());
        sysFile.setAdminName(currentAdmin().getRealName());
        SysFileLog sysFileLog = creatSysFileLog(sysFile);
            sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.ADD);
            sysFileLog.setComment(""+currentAdmin().getRealName()+"创建了 "+sysFile.getPath()+" 目录");
        sysFileLogDao.saveSysFileLog(sysFileLog);
        //如果新增人员不是超级管理员，增加文件权限
        if ("0".equals(sysFile.getType())){//新增文件夹
            Map<String, Object> map = roleMap();
            String roleName = (String) map.get("roleName");
            if (!roleName.contains("超级管理员")){
               addRoleWithSysFile(sysFile);
            }
        }
        File file=new File(filePath+sysFile.getPath().substring(1));
        if (!file.exists()){
            file.mkdir();
        }
        sysFileDao.saveSysFile(sysFile);
    }
    @Transactional(readOnly = true)
    public List<SysFile> findSysFileListByCondition(SysFile sysFile) {
        return sysFileDao.findSysFileListByCondition(sysFile);
    }
    @Transactional(readOnly = true)
    public List<SysFile> findSysFileAllList(SysFile sysFile) {
        return sysFileDao.findSysFileAllList(sysFile);
    }
    @Transactional(readOnly = true)
    public SysFile findOneSysFileByCondition(SysFile sysFile) {
        return sysFileDao.findOneSysFileByCondition(sysFile);
    }
    @Transactional(readOnly = true)
    public long findSysFileCountByCondition(SysFile sysFile) {
        return sysFileDao.findSysFileCountByCondition(sysFile);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSysFile(SysFile sysFile) {
        sysFileDao.updateSysFile(sysFile);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSysFileDelOrRollback(SysFile sysFile) {
        if (ObjectUtil.isNotNull(sysFile.getIsDelete())){
            SysFile sysFileById = sysFileDao.findSysFileById(sysFile.getId());
            if (ObjectUtil.isNotNull(sysFileById)){
            if (!sysFile.getIsDelete().equals(sysFileById.getIsDelete())){
                SysFileLog sysFileLog = creatSysFileLog(sysFileById);
                if (sysFile.getIsDelete().equals("1")){
                    sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.DELETE);
                    sysFileLog.setComment(""+currentAdmin().getRealName()+"将 "+sysFileById.getAddress()+"/"+sysFileById.getTitle()+" 放入回收站");
                    if (sysFileById.getType().equals("0")){
                        //将下级isDelete改为2
                       delOrRollbackIsDelete(sysFileById,"2");
                    }
                }else {
                    sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.Recover);
                    sysFileLog.setComment(""+currentAdmin().getRealName()+"将 "+sysFileById.getAddress()+"/"+sysFileById.getTitle()+" 从回收站还原");
                    if (sysFileById.getType().equals("0")){
                        //将下级isDelete改为0
                        delOrRollbackIsDelete(sysFileById,"0");
                    }
                    if (!sysFileById.getParentId().equals("-1")){
                        //将上级isDelete不为0改为9
                        String address = sysFileById.getAddress();
                        List<String> addList = pathList(address);
                        SysFile sysFileP=new SysFile();
                        sysFileP.setType("0");
                        addList.stream().forEach(e->{
                            sysFileP.setPath(e);
                            SysFile fileByCondition = sysFileDao.findOneSysFileByCondition(sysFileP);
                            if (!"0".equals(fileByCondition.getIsDelete())){
                                fileByCondition.setIsDelete("9");
                                sysFileDao.updateSysFileWithOutUpdatetime(fileByCondition);
                            }
                        });
                    }
                }
                sysFileLogDao.saveSysFileLog(sysFileLog);
            }
            }
        }
        sysFileDao.updateSysFile(sysFile);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysFile(String id) {
        SysFile sysFileById = sysFileDao.findSysFileById(id);
        List<String> stringList=new ArrayList<>();
        //清空日志
        SysFileLog sysFileLog = new SysFileLog();
        sysFileLog.setFilePath(sysFileById.getPath());
        List<SysFileLog> sysFileLogListByCondition = sysFileLogDao.findSysFileLogByFileId(sysFileLog);
        if (sysFileLogListByCondition.size()>0){
            sysFileLogListByCondition.stream().forEach(e->{
                stringList.add(e.getId());
            });
            String[] strings = stringList.toArray(new String[stringList.size()]);
            sysFileLogDao.batchDeleteSysFileLog(strings);//清空文件或文件夹下所有日志日志
        }
        //新增彻底删除日志
        SysFileLog sysFileLog1 = creatSysFileLog(sysFileById);
        sysFileLog1.setOperation(Constant.FILE_CHANGE_STUTUS.Shift_Delete);
        String address = sysFileById.getAddress();
        if (ObjectUtil.isNull(address)){
            address="";
        }
        sysFileLog1.setComment(""+currentAdmin().getRealName()+" 从回收站移除了 "+address+"/"+sysFileById.getTitle());
        sysFileLogDao.saveSysFileLog(sysFileLog1);
        //删除数据库数据
        File file = new File(filePath+sysFileById.getPath().substring(1));
        if (file.isDirectory()){
            List<String> chidrenIds=new ArrayList<>();//下级id
            List<SysFileLog> logList=new ArrayList<>();//需增加彻底删除日志
            //所有需要删除下级
            List<SysFile> sysFileAllWithoutStatus = sysFileDao.findSysFileAllWithoutStatus(sysFileById.getPath());
//             List<File> fileList=new ArrayList<>();
            sysFileAllWithoutStatus.stream().forEach(e->{
//                File file1 = new File(filePath + e.getPath());
//                fileList.add(file1);
                chidrenIds.add(e.getId());
                SysFileLog sysFileLog2 = creatSysFileLog(e);
                sysFileLog2.setOperation(Constant.FILE_CHANGE_STUTUS.Shift_Delete);
                sysFileLog2.setComment(""+currentAdmin().getRealName()+" 从回收站移除了 "+e.getAddress()+"/"+e.getTitle());
                sysFileLog2.setCreateTime(DateUtil.now());
                logList.add(sysFileLog2);
            });
            //这是所有下级id
            String[] rowData = chidrenIds.toArray(new String[chidrenIds.size()]);
            if(logList.size()>0)
                sysFileLogDao.batchSaveSysFileLog(logList);
            if (rowData.length>0)
                sysFileDao.batchDeleteSysFile(rowData);//删除数据库中file数据
//            fileList.stream().forEach(d->{
//                if (d.isFile()){
//                    //只删除数据库中文件，不对其他文件做处理
//                    d.delete();
//                }
//            });
        }
        sysFileDao.deleteSysFile(id);
        //彻底删除文件夹
        deleteFile(file);//ok
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysFileByCondition(SysFile sysFile) {
        sysFileDao.deleteSysFileByCondition(sysFile);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveSysFile(List<SysFile> sysFiles){
        sysFiles.forEach(sysFile -> sysFile.setId(sequenceId.nextId()));
        sysFileDao.batchSaveSysFile(sysFiles);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteSysFile(String rowData[]){
        Arrays.stream(rowData).forEach(e->{
            deleteSysFile(e);
        });
//        sysFileDao.batchDeleteSysFile(rowData);
    }
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSysFile(SysFile sysFile){
        sysFileDao.batchUpdateSysFile(sysFile);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileModule(){
        List<SysFile> sysFileList = new ArrayList<>();
        ExportParams exportParams = new ExportParams("文件管理导入模板","文件管理");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysFile.class, sysFileList);
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFile(SysFile sysFile){
        List<SysFile> sysFileList = sysFileDao.findSysFileListByCondition(sysFile);
        ExportParams exportParams = new ExportParams("文件管理导出","文件管理列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        return ExcelExportUtil.exportExcel(exportParams, SysFile.class, sysFileList);
    }
    @Transactional(rollbackFor = Exception.class)
    public ExcelInfo uploadSysFile(MultipartFile file){
        ExcelInfo excelInfo = new ExcelInfo();
        List<SysFile> sysFileList = new ArrayList<>();
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);
        SysFile a = new SysFile();
        a.setAdminId(myId());
        try (InputStream is = file.getInputStream()){
            List<SysFile> list = ExcelImportUtil.importExcel(is,SysFile.class, params);
            int cnt = list.size();
            if(list.size()>0){
                list.stream().forEach(e->{
                    e.setId(sequenceId.nextId());
                    e.setAdminId(myId());
                });
                sysFileDao.batchSaveSysFile(list);
            }
            excelInfo.setSuccessCnt(cnt-excelInfo.getErrorNumber());
            excelInfo.setObjectList(sysFileList);
            excelInfo.setSumCnt(cnt);
            return excelInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Transactional(readOnly = true)
    public List<ExcelExport> getSysFileTableField() {
        return sysFileDao.getSysFileTableField();
    }
    @Transactional(rollbackFor = Exception.class)
    public Workbook exportSysFileByField(List<ExcelExport> excelExportList,SysFile sysFile) {
        List<SysFile> sysFileList = sysFileDao.findSysFileListByCondition(sysFile);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        sysFileList.stream().forEach(e->{
            list.add(beanToMap(e));
        });
        ExportParams exportParams = new ExportParams("文件管理导出","文件管理列表");
        exportParams.setStyle(ExcelStyleUtil.class);
        List<ExcelExportEntity> beanList = new ArrayList<ExcelExportEntity>();
        excelExportList.stream().forEach(e->{
            if(StringUtils.isNotEmpty(e.getNewName())){
                beanList.add(new ExcelExportEntity(e.getNewName(), camelCaseName(e.getKey())));
            }else{
                beanList.add(new ExcelExportEntity(e.getName(), camelCaseName(e.getKey())));
            }
        });
        return ExcelExportUtil.exportExcel(exportParams, beanList, list);
    }
//    @Transactional(rollbackFor = Exception.class)
//    public void saveSysByPath(String parentId) { //测试根据路径上传完成后扫描入库
//        String path="";
//        if (!"-1".equals(parentId)){
//            SysFile fileById = sysFileDao.findSysFileById(parentId);
//            path=fileById.getPath();
//        }
//        buildSysFiles(filePath.substring(0,filePath.lastIndexOf("/")-1)+path);
//    }
    //保存文件同时保存文件属性

    public void  saveSysFile(File file,String parentId,String relativePath){
        SysFile sysFile=new SysFile();
        sysFile.setId(sequenceId.nextId());
        sysFile.setAdminId(myId());
        sysFile.setAdminName(currentAdmin().getRealName());
        sysFile.setTitle(file.getName());
        String fileSize = checkFileSize(file.length());
        sysFile.setSize(fileSize);
        sysFile.setSuffix(file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase());
        sysFile.setType(String.valueOf(fileTypeUtil.setResouceType(sysFile.getSuffix())));
        sysFile.setIsDelete("0");
        //path  address
        if ("-1".equals(parentId)){
            sysFile.setPath(relativePath);
            sysFile.setAddress(relativePath.substring(0,relativePath.lastIndexOf("/")));
        }else {
            SysFile fileById = sysFileDao.findSysFileById(parentId);
            sysFile.setAddress(fileById.getPath());
            sysFile.setPath(fileById.getPath()+"/"+file.getName());
        }
        sysFileDao.saveSysFile(sysFile);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveSysFileByMultipartFile(MultipartFile file,String parentId,String relativePath) {
//        System.out.println(parentId);
        List<SysFile> dirList=new ArrayList<>();
        SysFile sysFile=new SysFile();
        sysFile.setId(sequenceId.nextId());
        sysFile.setAdminId(myId());
        sysFile.setAdminName(currentAdmin().getRealName());
        sysFile.setIsDelete("0");
//        sysFile.setCreateTime(DateUtil.now());
        String fileSize = checkFileSize(file.getSize());
//        sysFile.setSize(String.valueOf(file.getSize()));
        sysFile.setSize(fileSize);
        sysFile.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase());
        //文件所在路径
        sysFile.setPath(relativePath);
        String address =relativePath.substring(0,relativePath.lastIndexOf("/"));
        //文件所在目录
        sysFile.setAddress(address);
//        String refileTitle = refileTitle(file.getOriginalFilename(), address);
        sysFile.setTitle(sysFile.getPath().replace(address,"").substring(1));
        //上级目录集合   //多线程造成异步锁
        List<String> list = pathList(address);
            SysFile sysFile1 = new SysFile();
            sysFile1.setType("0");
        for (String s : list) {
            sysFile1.setPath(s);
            SysFile fileByCondition = sysFileDao.findOneSysFileByCondition(sysFile1);
            if (ObjectUtil.isNotNull(fileByCondition)){
                //如果状态为2或者1   上级需要更改其isDel 为9
                updateIsDelWithAction(fileByCondition);
                dirList.add(fileByCondition);
            }else {
                sysFile1.setIsDelete("0");
                sysFile1.setId(sequenceId.nextId());
                sysFile1.setAdminId(myId());
                sysFile1.setAdminName(currentAdmin().getRealName());
                sysFile1.setTitle(s.substring(s.lastIndexOf("/")+1));
                //设置pid
                if (s.split("/").length<3){
                    sysFile1.setParentId("-1");
                }else {
                    sysFile1.setAddress(s.substring(0,s.lastIndexOf("/")));
                    SysFile sysFile2 = new SysFile();
                    sysFile2.setType("0");
//                    sysFile2.setIsDelete("0");
                    sysFile2.setPath(s.substring(0,s.lastIndexOf("/")));
                    SysFile fileParent = sysFileDao.findOneSysFileByCondition(sysFile2);
                    sysFile1.setParentId(fileParent.getId());
                }
//                synchronized (this) {
                try{
                    sysFileDao.saveSysFile(sysFile1);
                }catch (Exception e){
//                    System.out.println(sysFile1);
                }finally {
                    SysFile fileByCondition1 = sysFileDao.findOneSysFileByCondition(sysFile1);
                    if (ObjectUtil.isNull(fileByCondition1))
                        sysFileDao.saveSysFile(sysFile1);
                }
//                }
                SysFileLog sysFileLog = creatSysFileLog(sysFile1);
                addRoleWithSysFile(sysFile1);
                sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.ADD);
                sysFileLog.setComment(sysFileLog.getAdminName()+"创建了 "+sysFile1.getPath()+"目录");
                sysFileLogDao.saveSysFileLog(sysFileLog);
                dirList.add(sysFile1);
            }
        }
        if (dirList.size()>0){
            sysFile.setParentId(dirList.get(dirList.size()-1).getId());
        }else {
            sysFile.setParentId("-1");
        }
        //文件名后缀
        int suffixInt = fileTypeUtil.setResouceType(sysFile.getSuffix());
        sysFile.setType(String.valueOf(suffixInt));
        SysFileLog sysFileLog = creatSysFileLog(sysFile);
        sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.ADD);
        sysFileLog.setComment(sysFileLog.getAdminName()+"上传了 "+sysFile.getAddress()+"/"+sysFile.getTitle()+"");
        sysFileLogDao.saveSysFileLog(sysFileLog);
        sysFileDao.saveSysFile(sysFile);
        //图片
        if(suffixInt==2){
            asyncService.updateImgHandW(sysFile);
        }
    }
    //遗漏数据重新建立树状关系
    public synchronized  void reDelRePath(String path){
            String substring;
            if (new File(filePath+path).isFile()){
                substring = path.substring(0, path.lastIndexOf("/"));
                reloadDir(substring);
                //重新建立文件和文件夹依赖一直出错，
                SysFile sysFile = new SysFile();
    //            sysFile.setType("1");
                //文件名后缀
                String suffix = path.substring(path.lastIndexOf(".") + 1);
                sysFile.setType(String.valueOf(fileTypeUtil.setResouceType(suffix.toLowerCase())));
                sysFile.setPath(path);
//                sysFile.setIsDelete("0");
                SysFile children = sysFileDao.findOneSysFileByCondition(sysFile);
                if (ObjectUtil.isNotNull(children)){
                    String parentId = children.getParentId();
                    if (ObjectUtil.isNull(sysFileDao.findSysFileById(parentId))){
                        sysFile.setType("0");
                        sysFile.setPath(substring);
//                        sysFile.setIsDelete("0");
                        SysFile parentSysFile = sysFileDao.findOneSysFileByCondition(sysFile);
                        children.setParentId(parentSysFile.getId());
                        sysFileDao.updateSysFileWithOutUpdatetime(children);
                    }
                }
            } else {
                substring=path;
                reloadDir(substring);
            }
    }
    public  void reloadDir(String path){
        List<String> list = pathList(path);
        if (list.size()>1){

        }     //从目录结构下往上修改pid
        Collections.reverse(list);
        for (String s : list) {
            SysFile sysFile = new SysFile();
            sysFile.setType("0");

            String parentPath = s.substring(0, s.lastIndexOf("/"));
            sysFile.setPath(s);
            SysFile fileByCondition = sysFileDao.findOneSysFileByCondition(sysFile);
            //            if ("0".equals(fileByCondition.getIsDelete()))
            if (!parentPath.equals("")){
                sysFile.setPath(parentPath);
                SysFile parenthSysFile = sysFileDao.findOneSysFileByCondition(sysFile);
                //测试中有脏数据
                if ("0".equals(parenthSysFile.getIsDelete()))
                    if (ObjectUtil.isNotNull(parenthSysFile))
                        if (ObjectUtil.isNull(fileByCondition.getParentId())||!fileByCondition.getParentId().equals(parenthSysFile.getId())){
                            fileByCondition.setParentId(parenthSysFile.getId());
                            synchronized(this){
                                sysFileDao.updateSysFileWithOutUpdatetime(fileByCondition);
                            }
                        }
            }
        }
    }
    ///safety/upload/center/2021-03-29 转成 /safety /safety/upload /safety/upload/center /safety/upload/center/2021-03-29
    public  List<String> pathList(String path){
        ArrayList<String> list = new ArrayList<>();
        String[] split = path.split("/");
        if (split.length>1){
            for (int i = 1; i <split.length; i++) {
                String s=("/"+split[i]);
                if (list.size()==0){
                    list.add(s);
                }else {
                    String s1= list.get(i - 2) + s;
                    list.add(s1);
                }
            }
        }
        return list;
    }
    public SysFileLog creatSysFileLog(SysFile sysFile){
        SysFileLog sysFileLog=new SysFileLog();
        sysFileLog.setId(sequenceId.nextId());
        sysFileLog.setAdminId(myId());
        sysFileLog.setAdminName(currentAdmin().getRealName());
        sysFileLog.setFileId(sysFile.getId());
        return sysFileLog;
    }
    //查找当前id及其下级所有id
    @Transactional(readOnly = true)
    public String getSysFileChildrenList(String id) {
        return sysFileDao.getSysFileChildrenList(id);
    }

    /*
     * 获取某个文件夹下的所有文件
     */
    public static ArrayList<File> getPathAllFiles(File file, ArrayList<File> list){
        if(file.isFile()){//如果是文件，直接装载
            list.add(file);
        }else{//文件夹
            File[] files = file.listFiles();
            for(File f : files){//递归
                if(f.isDirectory()){
                    getPathAllFiles(f,list);
                }else{
                    list.add(f);
                }
            }
        }
        return list;
    }

    public SysFile creatSysFile(SysFile sysFile){
        String path = sysFile.getPath();
        sysFile.setId(sequenceId.nextId());
        sysFile.setIsDelete("0");
        sysFile.setAdminId(currentAdmin().getId());
        sysFile.setAdminName(currentAdmin().getRealName());
        sysFile.setTitle(path.substring(path.lastIndexOf("/")+1));
        sysFile.setAddress(path.substring(0,path.lastIndexOf("/")));
        return sysFile;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseJson buildSysFiles(String url){
        String parentId="-1";
        //  /2021-04-14/code/1
        url=url.replace("\\", "/");
        String path ="/"+ url.replace(filePath, "");
        SysFile sysFile=new SysFile();
        sysFile.setType("0");
        //上级文件夹路径 -----如果上级不存在 /2021-04-14/code
        String address = path.substring(0, path.lastIndexOf("/"));
        if (ObjectUtil.isNotNull(address)&&!address.equals("")){
            sysFile.setPath(path);
            SysFile fileByCondition = sysFileDao.findOneSysFileByCondition(sysFile);
            if (ObjectUtil.isNotNull(fileByCondition)){
//                updateIsDelWithAction(fileByCondition);
                parentId=fileByCondition.getId();
            }else {
                List<String> list = pathList(path);
                //    /2021-04-14
                for (int i = 1; i < list.size(); i++) {
                    sysFile.setPath(list.get(i));
                    SysFile fileByCondition1 = sysFileDao.findOneSysFileByCondition(sysFile);
                    if (ObjectUtil.isNull(fileByCondition1)){
                        SysFile sysFile1=new SysFile();
                        sysFile1.setType("0");
                        sysFile1.setPath(sysFile.getPath());
                        creatSysFile(sysFile1);
                        if (ObjectUtil.isNull(sysFile1.getAddress())||sysFile1.getAddress().equals("")){
                            sysFile1.setParentId("-1");
                        }else {
                            sysFile.setPath(list.get(i).substring(0,list.get(i).lastIndexOf("/")));
                            SysFile fileByCondition2 = sysFileDao.findOneSysFileByCondition(sysFile);
                            sysFile1.setParentId(fileByCondition2.getId());
                        }
                        SysFileLog sysFileLog = creatSysFileLog(sysFile1);
                            addRoleWithSysFile(sysFile1);
                            sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.Scann);
                            sysFileLog.setComment(""+currentAdmin().getRealName()+"创建了 "+sysFile1.getPath()+" 目录");
                            sysFileLogDao.saveSysFileLog(sysFileLog);
                        sysFileDao.saveSysFile(sysFile1);
                    }
                }
                sysFile.setPath(list.get(list.size()-1));
                SysFile condition = sysFileDao.findOneSysFileByCondition(sysFile);
                parentId=condition.getId();
            }
        }
        //扫描数据文件入库
        List<SysFile> sysTransferFileList = new ArrayList<>();
        List<SysFileLog> sysFileLogList = new ArrayList<>();
        File file = new File(url);
        if (!file.exists())
            return new ResponseJson(false,"文件夹不存在，请确认正确路径");
        File listFiles[] = file.listFiles();
        if(Objects.isNull(listFiles) || listFiles.length<=0){
            return new ResponseJson(false,"路径下暂无图片");
        }else{
                Map<String,List> map= saveSysTransferFileFile(url,sysTransferFileList,parentId,sysFileLogList);
                sysTransferFileList = map.get("fileList");
                sysFileLogList = map.get("logList");
                if (sysTransferFileList.size()>0){
                    sysFileDao.batchSaveSysFile(sysTransferFileList);
                    asyncService.updateImgHandW(sysTransferFileList);
                }
                if (sysFileLogList.size()>0)
                sysFileLogDao.batchSaveSysFileLog(sysFileLogList);
                reloadDirByparentId(parentId);
                return new ResponseJson(true,"扫描完成");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String,List> saveSysTransferFileFile(String path, List<SysFile> sysTransferFileList,String parentId,  List<SysFileLog> sysFileLogList ) {
        File listFiles[] = new File(path).listFiles();
        path = path.replace("\\", "/");
        String fileAddress="/"+path.replace(filePath,"");
        for (int i = 0; i < listFiles.length; i++) {
            String itemId = sequenceId.nextId();
            SysFile sysTransferFile = new SysFile();
            sysTransferFile.setAdminId(currentAdmin().getId());
            sysTransferFile.setAdminName(currentAdmin().getRealName());
            sysTransferFile.setCreateTime(DateUtil.now());
            sysTransferFile.setIsDelete("0");
            sysTransferFile.setAddress(fileAddress);
            sysTransferFile.setId(sequenceId.nextId());
            sysTransferFile.setTitle(listFiles[i].getName());
            //校验上级文件夹是否存在
            SysFile sysFile = new SysFile();
            sysFile.setId(parentId);
            sysFile.setType("0");
            if (!("/").equals(fileAddress)&&ObjectUtil.isNotNull(fileAddress)&&!"".equals(fileAddress)){
                sysFile.setPath(fileAddress);
            }else {
                fileAddress="";
                sysFile.setPath("/"+sysTransferFile.getTitle());
            }
            checkPSysFile(sysFile,fileAddress,parentId,itemId);
            if (!listFiles[i].isDirectory()) {
                sysFile.setId(null);
                //这里查询的是上级文件夹
                SysFile fileByCondition = sysFileDao.findOneSysFileByCondition(sysFile);
                if (parentId.equals("-1")){
                    if (ObjectUtil.isNotNull(fileByCondition)){
                        updateIsDelWithAction(fileByCondition);
                        parentId=fileByCondition.getId();
                    }
                }
                String filename = listFiles[i].getName();
                sysFile.setPath(fileAddress+"/"+filename);
                sysFile.setType(String.valueOf(fileTypeUtil.setResouceType(filename.substring( filename.lastIndexOf(".")+1).toLowerCase())));
                SysFile findCondition = sysFileDao.findOneSysFileByCondition(sysFile);
                if (ObjectUtil.isNull(findCondition)) {//文件是否存在
                    //
                    sysTransferFile.setAddress(fileAddress);
                    sysTransferFile.setPath(fileAddress+"/"+filename);
                    //后缀
                    sysTransferFile.setSuffix( filename.substring(  filename.lastIndexOf(".")+1).toLowerCase());
                    //大小
                    String fileSize = checkFileSize(listFiles[i].length());
                    sysTransferFile.setSize(fileSize);
                    int suffixInt = fileTypeUtil.setResouceType(sysTransferFile.getSuffix().toLowerCase());
                    sysTransferFile.setType(String.valueOf(suffixInt));
                    sysTransferFile.setParentId(parentId);
    //                日志，LOCK
                        SysFileLog sysFileLog = creatSysFileLog(sysTransferFile);
                        sysFileLog.setCreateTime(DateUtil.now());
                        sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.Scann);
                        sysFileLog.setComment(sysFileLog.getAdminName()+"扫描加入 "+sysTransferFile.getAddress()+"/"+sysTransferFile.getTitle()+"");
                        sysFileLogList.add(sysFileLog);
                    sysTransferFileList.add(sysTransferFile);
                }
            }else{
                saveSysTransferFileFile(listFiles[i].getPath(),sysTransferFileList,itemId,sysFileLogList);
            }
        }
        Map<String,List> map=new HashMap<>();
        map.put("fileList",sysTransferFileList);
        map.put("logList",sysFileLogList);
//        map.put("file",);
        return map;
    }
    //  校验上级文件夹是否存在
    private void  checkPSysFile(SysFile sysFile,String address,String parentId,String itemId){
        sysFile.setId(null);
        if (ObjectUtil.isNotNull(address)&&!"".equals(address)){
                SysFile fileByCondition = sysFileDao.findOneSysFileByCondition(sysFile);
                if (ObjectUtil.isNull(fileByCondition)){
                    sysFile.setAdminId(currentAdmin().getId());
                    sysFile.setAdminName(currentAdmin().getRealName());
                    sysFile.setIsDelete("0");
                    sysFile.setTitle(address.substring(address.lastIndexOf("/")+1));
                    sysFile.setAddress(address.substring(0,address.lastIndexOf("/")));
                    sysFile.setId(parentId);
//                    sysFile.setCreateTime(DateUtil.now());
                    addRoleWithSysFile(sysFile);
                    sysFileDao.saveSysFile(sysFile);
                    //上级文件夹不存在存在设置pid
                    //                    reDelRePath(sysFile.getPath());
                    reloadDir(sysFile.getPath());
                }
        }else {
            SysFile fileByCondition = sysFileDao.findOneSysFileByCondition(sysFile);
            if (ObjectUtil.isNull(fileByCondition)){
                sysFile.setAdminId(currentAdmin().getId());
                sysFile.setAdminName(currentAdmin().getRealName());
                sysFile.setId(itemId);
                sysFile.setParentId("-1");
                sysFile.setIsDelete("0");
                sysFile.setTitle(sysFile.getPath().substring(1));
    //            sysFile.setCreateTime(DateUtil.now());
                addRoleWithSysFile(sysFile);
                sysFileDao.saveSysFile(sysFile);
            }
        }
    }

    private String checkFileSize(double len) {
        //单位为byte
        if (len >= 0 && len < 1024) {
            return len + "b";
        } else if (len >= 1024) {
            len = len / 1024;
            if (len >= 1024) {
                return String.format("%.2f", len / 1024) + "M";
            } else {
                return String.format("%.2f", len) + "kb";
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void reloadDirByparentId(String parentId){
        //不选择目录直接上传到根目录下，需要重排所有目录结构？
        if (!parentId.equals("-1")){
            reloadDirWithParentId(parentId);
        }else {
            SysFile sysFile = new SysFile();
            sysFile.setType("0");
            sysFile.setParentId("-1");
            List<SysFile> sysFileListByCondition = sysFileDao.findSysFileListByCondition(sysFile);
            sysFileListByCondition.stream().forEach(e->{
                reloadDirWithParentId(e.getId());
            });
        }
    }
    private void   reloadDirWithParentId(String parentId){
        SysFile sysFileById = sysFileDao.findSysFileById(parentId);
        List<SysFile> sysFileAllbyPath = sysFileDao.findSysFileAllbyPath(sysFileById.getPath());
        if (ObjectUtil.isNotNull(sysFileAllbyPath)&&sysFileAllbyPath.size()>0){
            sysFileAllbyPath.stream().forEach(e->{
                if (!e.getId().equals(parentId)){
                    reloadDir(e.getPath());
                }
            });
        }
    }

    public static void deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return ;
        }
        if (dirFile.isFile()) {
             dirFile.delete();
        } else {
            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }
        dirFile.delete();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateFilePath(SysFile sysFile,String title){
        String oldPath = sysFile.getPath();
        if (ObjectUtil.isNotNull(sysFile.getAddress())){
            sysFile.setPath(sysFile.getAddress()+"/"+title);
        }else {
            sysFile.setPath("/"+title);
        }
        sysFile.setTitle(title);
        if (sysFile.getType().equals("0")){
            //文件夹需要修改下级所有的path和address
            List<SysFile> sysFileAllWithoutStatus = sysFileDao.findSysFileAllWithoutStatus(oldPath);
            String path = sysFile.getPath();
            sysFileAllWithoutStatus.stream().forEach(e->{
                if (!e.getId().equals(sysFile.getId())){
                    e.setPath(e.getPath().replace(oldPath,path));
                    e.setAddress(e.getAddress().replaceAll(oldPath,path));
                    sysFileDao.updateSysFile(e);
                }
            });
            fixFileName(filePath+oldPath,title);
        }else {
            fixFileName(filePath+oldPath,title.substring(0,title.lastIndexOf(".")));
        }
        SysFileLog sysFileLog = creatSysFileLog(sysFile);
        sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.UPDATE);
        sysFileLog.setComment(""+currentAdmin().getRealName()+"将 "+oldPath+"  修改为  "+sysFile.getTitle());
        sysFileLogDao.saveSysFileLog(sysFileLog);
        sysFileDao.updateSysFile(sysFile);
    }


    /**
     * 通过文件路径直接修改文件名
     *
     * @param filePath    需要修改的文件的完整路径
     * @param newFileName 需要修改的文件的名称
     * @return
     */
    private void fixFileName(String filePath, String newFileName) {
        File f = new File(filePath);
        newFileName = newFileName.trim();
        String newFilePath = null;
        if (f.isDirectory()) { // 判断是否为文件夹
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName;
        } else {
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName
                    + filePath.substring(filePath.lastIndexOf("."));
        }
        File nf = new File(newFilePath);
        try {
            f.renameTo(nf); // 修改文件名
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    @Transactional(readOnly = true)
    public List<SysFile> findSysFileOpenTime() {
        return sysFileDao.findSysFileOpenTime();
    }
    @Transactional(readOnly = true)
    public List<SysFile> findSysFileListByRole(SysFile sysFile) {
        return sysFileDao.findSysFileListByRole(sysFile);
    }
    @Transactional(readOnly = true)
    public long findSysFileCountByRole(SysFile sysFile) {
        return sysFileDao.findSysFileCountByRole(sysFile);
    }
    //判断删除或者回复，，删除将isDelete 等于0改为2 ，回复将所有isDelete 改为0
    @Transactional(rollbackFor = Exception.class)
    public void delOrRollbackIsDelete(SysFile sysFile,String isDelete){
        String path = sysFile.getPath();
        List<SysFile> sysFileAllWithoutStatus = sysFileDao.findSysFileAllWithoutStatus(path);
//        List<SysFile> updataList=new ArrayList<>();
        List<String> list=new ArrayList<>();
        if (!isDelete.equals("0")){
            //删除
            sysFileAllWithoutStatus.stream().forEach(e->{
                if (e.getIsDelete().equals("0")){
//                    updataList.add(e);
                    list.add(e.getId());
                }
            });
        }else {
            //恢复
            sysFileAllWithoutStatus.stream().forEach(e->{
                //恢复增加日志
//                if (!e.getIsDelete().equals("0")){
//                    if (!e.getId().equals(sysFile.getId())){
//                        SysFileLog sysFileLog = creatSysFileLog(e);
//                        sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.Recover);
//                        sysFileLog.setComment(""+currentAdmin().getRealName()+"将 "+e.getAddress()+"/"+e.getTitle()+" 从回收站还原");
//                        sysFileLogDao.saveSysFileLog(sysFileLog);
//                    }
//                }
                list.add(e.getId());
            });
//            updataList.addAll(sysFileAllWithoutStatus);
        }
        String[] rowData = list.toArray(new String[list.size()]);
        if (rowData.length>0){
            SysFile updataSysFile = new SysFile();
            updataSysFile.setIsDelete(isDelete);
            updataSysFile.setRowData(rowData);
            sysFileDao.batchUpdateSysFile(updataSysFile);
        }
    }
    //根据条件读取多久的数据数量
    @Transactional(readOnly = true)
    public long findSysFileCountByDate(Integer num){
        return sysFileDao.findSysFileCountByDate(num);
    }
    @Transactional(readOnly = true)
    public List<Map<String,Object>> findSysFileCountByMonth(){
        return sysFileDao.findSysFileCountByMonth();
    }

    @Transactional(readOnly = true)
    public List<SysFile> findSysFileListByConditionRecycleBin(SysFile sysFile) {
        return sysFileDao.findSysFileListByConditionRecycleBin(sysFile);
    }

    @Transactional(readOnly = true)
    public long findCountByConditionRecycleBin(SysFile sysFile){
        return sysFileDao.findCountByConditionRecycleBin(sysFile);
    }
    @Transactional(readOnly = true)
    public List<SysFile> findSysFileListByRecycleBinRole(SysFile sysFile) {
        return sysFileDao.findSysFileListByRecycleBinRole(sysFile);
    }
    @Transactional(readOnly = true)
    public long findCountByRecycleBinRole(SysFile sysFile) {
        return sysFileDao.findCountByRecycleBinRole(sysFile);
    }

    @Transactional(readOnly = true)
    public List<SysFile> findSysFileListByINStatus(SysFile sysFile) {
        return sysFileDao.findSysFileListByINStatus(sysFile);
    }
    @Transactional(readOnly = true)
    public long findSysFileCountByINStatus(SysFile sysFile){
        return sysFileDao.findSysFileCountByINStatus(sysFile);
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateIsDelWithAction(SysFile sysFile){
        //如果状态为2或者1   上级也需要更改
        if ("1".equals(sysFile.getIsDelete())||"2".equals(sysFile.getIsDelete())){
            sysFile.setIsDelete("9");
            sysFileDao.updateSysFileWithOutUpdatetime(sysFile);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSysFileWithOutUpdatetime(SysFile sysFile) {
        sysFileDao.updateSysFileWithOutUpdatetime(sysFile);
    }
    @Transactional(readOnly = true)
    public Map<String,Object> roleMap(){
        Map map=new HashMap();
        List<SysRole> roleByList = sysRoleDao.findRoleByAdminId(myId());
        List<String> roleName=new ArrayList<>();
        List<String> roleID=new ArrayList<>();
        roleByList.stream().forEach(e->{
            roleName.add(e.getTitle());
            roleID.add(e.getId());
        });
        map.put("roleID", StringUtils.join(roleID,","));
        map.put("roleName",StringUtils.join(roleName,","));
        map.put("roleByList",roleByList);
        System.out.println(map);
        return map;
    }
    public void addRoleWithSysFile(SysFile sysFile){
        Map<String, Object> map = roleMap();
        String roleName = (String) map.get("roleName");
        String roleID = (String) map.get("roleID");
        if (!roleName.contains("超级管理员")){
            SysFileRole fileRole = new SysFileRole();
            fileRole.setSysFileId(sysFile.getId());
            fileRole.setSysFileParentId(sysFile.getParentId());
            String[] split = roleID.split(",");
            Arrays.stream(split).forEach(e->{
                fileRole.setSysRoleId(e);
                SysFileRole oneSysFileRoleByCondition = sysFileRoleDao.findOneSysFileRoleByCondition(fileRole);
                if (ObjectUtil.isNull(oneSysFileRoleByCondition)){
                    fileRole.setId(sequenceId.nextId());
                    sysFileRoleDao.saveSysFileRole(fileRole);
                }
            });
        }
    }
    @Transactional(readOnly = true)
    public boolean checkFIleInRole(File file){
        String absolutePath = file.getAbsolutePath();
        String replace = absolutePath.replace("\\", "/");
        String path="/"+ replace.replace(filePath,"");
        Map<String, Object> map = roleMap();
        String roleName = (String) map.get("roleName");
        String roleID = (String) map.get("roleID");
        if (!roleName.contains("超级管理员")){
            List<SysFileRole> list=new ArrayList<>();
            List<String> pathList=new ArrayList<>();

            String[] split = roleID.split(",");
            SysFileRole fileRole = new SysFileRole();
            Arrays.stream(split).forEach(e->{
                fileRole.setSysRoleId(e);
                List<SysFileRole> sysFileRoleAllList = sysFileRoleDao.findSysFileRoleAllList(fileRole);
                list.addAll(sysFileRoleAllList);
            });
            //去重后
            List<SysFileRole> collectList= list.stream().distinct().collect(Collectors.toList());
            collectList.stream().forEach(d->{
                SysFile fileById = sysFileDao.findSysFileById(d.getSysFileId());
                pathList.add(fileById.getPath());
            });
            for (String s : pathList) {
                if(path.equals(s)){
                    return true;
                }
            }
        }
        return false;
    }
}
