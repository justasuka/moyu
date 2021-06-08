package top.cfl.cflwork.util;


import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.config.QiniuConfig;
import top.cfl.cflwork.pojo.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class QiniuUtil {
    private static final Logger logger = LoggerFactory.getLogger(QiniuUtil.class);
    //构造一个带指定Zone对象的配置类
    private static Configuration cfg = new Configuration(Zone.zone0());
    //...其他参数参考类注释
    private static UploadManager uploadManager = new UploadManager(cfg);

    public static String newName() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
    }
    public static String getDatePath(){
        Date date = new Date();
        return  "/" + new SimpleDateFormat("yyyy/").format(date)+ new SimpleDateFormat("MMdd").format(date);

    }
    public static boolean hasFile(String key){
        String r = HttpUtil.get(Constant.RES_PRE + key);
        return r!=null&&!r.startsWith("{\"error");
    }
    public static String getSimpleToken() {
        Auth auth = Auth.create(QiniuConfig.accessKey, QiniuConfig.secretKey);
        StringMap putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"/$(key)\",\"hash\":\"$(etag)\"}");
        long expireSeconds = 600;
        return auth.uploadToken(QiniuConfig.bucket,null, expireSeconds, putPolicy);
    }
    /*---???---*/
    public static String commonUploadFile(MultipartFile file, String suffix) {
        String ext = getExt(file);
        String newName =  newName() + ext;
        String key = suffix+getDatePath() + "/"+ newName;
        return commonUploadFileForKey(file,key);
    }

    /*---???---*/
    public static String commonUploadFileForKey(MultipartFile file, String key){

        if(key!=null){
            key = key.replaceAll("^\\/","");
        }

        String upToken = getSimpleToken();
        try {
            //默认不指定key的情况下，以文件内容的hash值作为文件名
            Response response = uploadManager.put(file.getInputStream(),key , upToken,null,null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            logger.debug(putRet.key);
            logger.debug(putRet.hash);
            return putRet.key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String commonUploadFileForKey(File file, String key){

        if(key!=null){
            key = key.replaceAll("^\\/","");
        }

        String upToken = getSimpleToken();
        try {
            //默认不指定key的情况下，以文件内容的hash值作为文件名
            Response response = uploadManager.put(new FileInputStream(file),key , upToken,null,null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return putRet.key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String commonUploadInputstreamForKey(InputStream inputStream, String key){

        if(key!=null){
            key = key.replaceAll("^\\/","");
        }

        String upToken = getSimpleToken();
        try {
            //默认不指定key的情况下，以文件内容的hash值作为文件名
            Response response = uploadManager.put(inputStream,key , upToken,null,null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            inputStream.close();
            return putRet.key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getExt(MultipartFile file){
        String filename = file.getOriginalFilename();
        String contentType=file.getContentType();
        return filename==null||filename.lastIndexOf(".") == -1 ? "." + contentType.substring(contentType.indexOf("/") + 1) : filename.substring(filename.lastIndexOf("."));
    }

    /**上传图片
     * bmp,jpg,png,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp
     * @param file
     * @param suffix
     * @return
     */
    public static String uploadImage(MultipartFile file, String suffix){
        String ext = getExt(file);
        if(".jpg".equals(ext)
                ||".png".equals(ext)
                ||".gif".equals(ext)
                ||".bmp".equals(ext)
                ||".jpeg".equals(ext)
                ){
            return commonUploadFile(file, suffix);
        }else{
            throw new IllegalArgumentException("wrong file type");
        }

    }
    /**上传图片
     * bmp,jpg,png
     * @param file
     * @param suffix
     * @param size 图片的大小
     * @return
     */
    public static String uploadImage(MultipartFile file, String suffix, int size){
        String ext = getExt(file);
        if(".jpg".equals(ext)
                ||".png".equals(ext)
                ||".gif".equals(ext)
                ||".bmp".equals(ext)
                ||".jpeg".equals(ext)
                ){
            if(file.getSize()>size){
                throw new IllegalArgumentException("size should be less than "+size);
            }
            return commonUploadFile(file, suffix);
        }else{
            throw new IllegalArgumentException("wrong file type");
        }

    }

    /**上传文件
     * @param file
     * @param suffix
     * @return
     * 文档：DOC、DOCX、PPT、PPTX、TXT、PDF、XLS、XLSX
     * 图片：JPG、PNG、GIF、BMP、JPEG
     * 视频：MP4
     * 音频：MP3
     */
    /*----???-----*/
    public static String uploadFile(MultipartFile file, String suffix){
        String ext = getExt(file);
        if(".jpg".equalsIgnoreCase(ext)
                ||".png".equalsIgnoreCase(ext)
                ||".gif".equalsIgnoreCase(ext)
                ||".bmp".equalsIgnoreCase(ext)
                ||".mp4".equalsIgnoreCase(ext)
                ||".mp3".equalsIgnoreCase(ext)
                ||".doc".equalsIgnoreCase(ext)
                ||".docx".equalsIgnoreCase(ext)
                ||".ppt".equalsIgnoreCase(ext)
                ||".pptx".equalsIgnoreCase(ext)
                ||".txt".equalsIgnoreCase(ext)
                ||".pdf".equalsIgnoreCase(ext)
                ||".xls".equalsIgnoreCase(ext)
                ||".xlsx".equalsIgnoreCase(ext)
                ||".jpeg".equalsIgnoreCase(ext)
                ||".wav".equalsIgnoreCase(ext)
                ){
            return commonUploadFile(file, suffix);
        }else{
            throw new RuntimeException("wrong file type");
        }
    }


    /**上传文件,限制大小
     * @param file
     * @param suffix
     * @return
     * 文档：DOC、DOCX、PPT、PPTX、TXT、PDF、XLS、XLSX
     * 图片：JPG、PNG、GIF、BMP、JPEG
     * 视频：MP4
     * 音频：MP3
     */
    /*----???-----*/
    public static String uploadFile(MultipartFile file, String suffix, int size){
        String ext = getExt(file);
        if(".jpg".equalsIgnoreCase(ext)
                ||".png".equalsIgnoreCase(ext)
                ||".gif".equalsIgnoreCase(ext)
                ||".bmp".equalsIgnoreCase(ext)
                ||".mp4".equalsIgnoreCase(ext)
                ||".mp3".equalsIgnoreCase(ext)
                ||".doc".equalsIgnoreCase(ext)
                ||".docx".equalsIgnoreCase(ext)
                ||".ppt".equalsIgnoreCase(ext)
                ||".pptx".equalsIgnoreCase(ext)
                ||".txt".equalsIgnoreCase(ext)
                ||".pdf".equalsIgnoreCase(ext)
                ||".xls".equalsIgnoreCase(ext)
                ||".xlsx".equalsIgnoreCase(ext)
                ||".jpeg".equalsIgnoreCase(ext)
                ||".wav".equalsIgnoreCase(ext)
                ){
            if(file.getSize()>size){
                throw new IllegalArgumentException("size should be less than "+size);
            }
            return commonUploadFile(file, suffix);
        }else{
            throw new RuntimeException("wrong file type");
        }
    }


    /**公文上传文件
     * @param file
     * @param suffix
     * @return
     * 文档：DOC、DOCX、PDF、XLS、XLSX
     * 图片：JPG、PNG、BMP、JPEG
     */
    /*----???-----*/
    public static String uploadDocFile(MultipartFile file, String suffix){
        String ext = getExt(file);
        if(".jpg".equalsIgnoreCase(ext)
                ||".png".equalsIgnoreCase(ext)
                ||".bmp".equalsIgnoreCase(ext)
                ||".doc".equalsIgnoreCase(ext)
                ||".docx".equalsIgnoreCase(ext)
                ||".pdf".equalsIgnoreCase(ext)
                ||".xls".equalsIgnoreCase(ext)
                ||".xlsx".equalsIgnoreCase(ext)
                ||".jpeg".equalsIgnoreCase(ext)
                ){
            return commonUploadFile(file, suffix);
        }else{
            throw new RuntimeException("wrong file type");
        }
    }
    /**
     *
     * @param file
     * @param suffix
     * @return
     * apk文件
     */
    public static String apkFile(MultipartFile file, String suffix){
        String ext = getExt(file);
        if(".apk".equals(ext)
                ){
            return commonUploadFile(file, suffix);
        }else{
            throw new RuntimeException("wrong file type");
        }
    }

    /**
     * 删除文件
     * @param qinliuUrl 上传文件后得到的key
     */
    public static Response deleteFile(String qinliuUrl){
        Configuration cfg = new Configuration(Zone.zone0());
        Auth auth = Auth.create(QiniuConfig.accessKey, QiniuConfig.secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            return bucketManager.delete(QiniuConfig.bucket, qinliuUrl);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
            return ex.response;
        }
    }
    public static String commonUploadFile(File file, String suffix) {
        String newName =  newName()+".png";
        Date date = new Date();
        String suffixDatePath = suffix + "/" + new SimpleDateFormat("yyyy/").format(date)+ new SimpleDateFormat("MMdd").format(date);
        String key = suffixDatePath + "/"+ newName;
        return commonUploadFileForKey(file,key);
    }
}