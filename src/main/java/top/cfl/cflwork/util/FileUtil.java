package top.cfl.cflwork.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.pojo.SysFile;
import top.cfl.cflwork.pojo.SysFileLog;
import top.cfl.cflwork.service.SysFileLogService;
import top.cfl.cflwork.service.SysFileService;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static top.cfl.cflwork.interceptor.LoginInterceptor.currentAdmin;
import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@Component
public class FileUtil {
    @Value("${file.filePath}")
    private  String filePath;
    @Value("${file.fileURL}")
    private  String fileURL;
    @Autowired
    private  SequenceId sequenceId;
    @Autowired
    private FileTypeUtil fileTypeUtil;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysFileLogService sysFileLogService;


    public  String uploadAvatar(MultipartFile file, String suffix){
        try {
//            String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            File targetFile = new File(fileURL +suffix+DateUtil.format(DateUtil.date(),"yyyy-MM-dd")+"/");
            if(!targetFile.exists()){
                targetFile.mkdirs();
            }
            String path = suffix+DateUtil.format(DateUtil.date(),"yyyy-MM-dd")+"/"+ file.getOriginalFilename();
            File dest = new File(fileURL +path);
//            Thumbnails.of(filePath +path).scale(1f).outputQuality(0.25f).toFile(dest);
//            getPathAllFilesUpload(dest)
            if (dest.exists()){
                //文件存在替换了原本数据
            }
            file.transferTo(dest);
            return "/"+path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public  String uploadFile(MultipartFile file, String suffix){
        try {
//            String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            File targetFile = new File(filePath +suffix+DateUtil.format(DateUtil.date(),"yyyy-MM-dd")+"/");
            if(!targetFile.exists()){
                targetFile.mkdirs();
            }
            String path = suffix+DateUtil.format(DateUtil.date(),"yyyy-MM-dd")+"/"+ file.getOriginalFilename();
            File dest = new File(filePath +path);
//            Thumbnails.of(filePath +path).scale(1f).outputQuality(0.25f).toFile(dest);
//            getPathAllFilesUpload(dest)
            if (dest.exists()){
                //文件存在替换了原本数据
            }
            file.transferTo(dest);
            return "/"+path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String uploadFiles(MultipartFile file,String relativePath){
        try {
            String path = relativePath + file.getOriginalFilename();
            File dest = new File(filePath +path);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            if (dest.exists()){
                //文件存在替换了原本数据
            }
            file.transferTo(dest);
            return "/"+path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String uploadFiles(File file,String path){
        try {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            byte [] b = new byte[1024];
            File file1 = new File(filePath + DateUtil.format(DateUtil.date(), "yyyy-MM-dd") + "/" + file);
            File orRenameFile = createOrRenameFile(file1);
            new FileInputStream(file);
            new FileOutputStream(orRenameFile);
            while(true){
                int temp = fis.read(b, 0, b.length);
                //如果temp = -1的时候，说明读取完毕
                if(temp == -1){
                    break;
                }
                fos.write(b, 0, temp);
            }


            return "/"+path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public  String uploadFile(MultipartFile file, String suffix,String relativePath,String parentId,boolean isReplace){
        try {
            //获取传入文件路径
            StringBuffer dir=new StringBuffer();
            if (!relativePath.equals("")&&relativePath!=null){
                dir.append( relativePath.replace(file.getOriginalFilename(), ""));
                if (ObjectUtil.isNull(dir)||dir.toString().equals("")){
                    dir.append("/");
                }
            }
            File targetFile;
            String path;
            if (!parentId.equals("-1")){
                //判断文件首字母为/  替换
                if (dir.toString().startsWith("/")){
                    if (dir.length()>0)
                        dir.deleteCharAt(0);
                }
                 targetFile=  new File(filePath + suffix +dir);
//                 targetFile=  new File(filePath + suffix +dir+"/"+DateUtil.format(DateUtil.date(),"yyyy-MM-dd"));
            }else {
                targetFile=  new File(filePath +suffix+DateUtil.format(DateUtil.date(),"yyyy-MM-dd")+"/"+dir);
            }
            if(!targetFile.exists()){
                    targetFile.mkdirs();
            }
            if (!parentId.equals("-1")){
                path = dir+ file.getOriginalFilename();
//                path = dir+DateUtil.format(DateUtil.date(),"yyyy-MM-dd")+"/"+ file.getOriginalFilename();
            }
            else {
                if (dir.toString().equals("/")){
                    path = suffix+DateUtil.format(DateUtil.date(),"yyyy-MM-dd")+dir+ file.getOriginalFilename();
                }else {
                path = suffix+DateUtil.format(DateUtil.date(),"yyyy-MM-dd")+"/"+dir+ file.getOriginalFilename();
                }
            }
            File dest = new File(filePath +path);
            if (ObjectUtil.isNotNull(isReplace)&&isReplace){
                if (dest.exists()){
                    file.transferTo(dest);
                    return   "/"+path;
                }
            }
//            重命名重复文件
            File orRenameFile = createOrRenameFile(dest);
            if (!file.getOriginalFilename().equals(orRenameFile.getName())){
                file.transferTo(orRenameFile);
                String replace = path.replace(file.getOriginalFilename(), orRenameFile.getName());
                return "/"+replace;
            }
            file.transferTo(dest);
            return "/"+path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 获取某个文件夹下的所有文件--上传使用
     */
    public static  ArrayList<File> getPathAllFilesUpload(File file, ArrayList<File> list){
        if(file.isFile()){//如果是文件，直接装载
            list.add(file);
        }else{//文件夹
            File[] files = file.listFiles();
            for(File f : files){//递归
                if(f.isDirectory()){
                    getPathAllFilesUpload(f,list);
                }else{
                    list.add(f);
                }
            }
        }
        return list;
    }

    /*
     * 获取某个文件夹下的所有文件并压缩图片--下载
     */
    public  ArrayList<File> getPathAllFilesAndDownload(File file, ArrayList<File> list){
        if(file.isFile()){//如果是文件，直接装载
            list.add(file);
        }else{//文件夹
            File[] files = file.listFiles();
            for(File f : files){//递归
                if(f.isDirectory()){
                    getPathAllFilesAndDownload(f,list);
                }else{
                    // 取得文件的后缀名。
                    String ext = f.getName().substring(f.getName().lastIndexOf(".") + 1).toUpperCase();
                    if (fileTypeUtil.setResouceType(ext)==2){
                        try {
                            Thumbnails.of(file).scale(1f).outputQuality(0.25f).toFile(file);
                        }catch (Exception e){
                            System.err.println("压缩失败");
                        }
                    }
                    list.add(f);
                }
            }
        }
        return list;
    }

    public static String[] getFileInfo(File from){
        String fileName=from.getName();
        int index = fileName.lastIndexOf(".");
        String toPrefix="";
        String toSuffix="";
        if(index==-1){
            toPrefix=fileName;
        }else{
            toPrefix=fileName.substring(0,index);
            toSuffix=fileName.substring(index,fileName.length());
        }
        return new String[]{toPrefix,toSuffix};
    }
    /**
     * sdcard/pic/tanyang.jpg
     *
     * toPrefix: tanyang
     * toSuffix: tanyang.jpg
     * @param from
     * @param toPrefix
     * @param toSuffix
     * @return
     */
    public static File createOrRenameFile(File from, String toPrefix, String toSuffix) {
        File directory = from.getParentFile();
        File newFile = new File(directory, toPrefix + toSuffix);
        for (int i = 1; newFile.exists() && i < Integer.MAX_VALUE; i++) {
            newFile = new File(directory, toPrefix + '(' + i + ')' + toSuffix);
        }
        return newFile;
    }
    public static File createOrRenameFile(File from){
        String[] fileInfo = getFileInfo(from);
        String toPrefix=fileInfo[0];
        String toSuffix=fileInfo[1];
        File file;
        file = createOrRenameFile(from, toPrefix, toSuffix);
        return file;

    }
}
