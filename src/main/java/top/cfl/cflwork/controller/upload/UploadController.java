package top.cfl.cflwork.controller.upload;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.val;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import top.cfl.cflwork.kit.HttpKit;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.SysFile;
import top.cfl.cflwork.pojo.SysFileLog;
import top.cfl.cflwork.service.SysFileLogService;
import top.cfl.cflwork.service.SysFileService;
import top.cfl.cflwork.util.FileTypeUtil;
import top.cfl.cflwork.util.FileUtil;
import top.cfl.cflwork.util.QiniuUtil;
import top.cfl.cflwork.pojo.Constant;
import top.cfl.cflwork.util.zip.ZipCompressor;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/upload")
@RestController
public class UploadController {

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private FileTypeUtil fileTypeUtil;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysFileLogService sysFileLogService;
    @Value("${file.filePath}")
    private  String filePath;
    @PostMapping("/uploadAvatar")
    public String uploadAvatar(MultipartFile file){
 //       return QiniuUtil.uploadImage(file, Constant.Upload.UPLOAD_AVATAR);
        return fileUtil.uploadAvatar(file, Constant.Upload.UPLOAD_AVATAR);

    }
    @PostMapping("/uploadFileWhitOut")
    @ApiOperation(value = "创建时间：2018-10-29。说明：上传文件到本地", notes = "返回资源名称和资源的url")
    public ResponseJson uploadFile(@ApiParam(value = "上传文件到本地", required = true) MultipartFile file) {
        //文件名后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        int suffixInt = fileTypeUtil.setResouceType(suffix);
        if(suffixInt==0){
            return new ResponseJson(false,"不支持的文件格式");
        }
        //不包含文件后缀名
        Map<String, String> map = new HashMap<>();
        map.put("name", file.getOriginalFilename());
        //获取保存文件路径
        String url = fileUtil.uploadFile(file, Constant.Upload.UPLOAD_CENTER);
        map.put("url", url);
        map.put("fileSize", checkFileSize(file.getSize()));
        map.put("type",String.valueOf(suffixInt));
        return new ResponseJson(map);
    }
    @PostMapping(value = "/uploadFile")
    @ApiOperation(value = "创建时间：2018-10-29。说明：上传文件到本地", notes = "返回资源名称和资源的url")
    public ResponseJson uploadFile(@ApiParam(value = "上传文件到本地", required = true)
        @RequestParam("file") String file, @RequestParam("parentId") String parentId,@RequestParam("fileName") String fileName){
        if(ObjectUtil.isNull(parentId)||parentId.equals("")){
            parentId="-1";
        }
        List<Map<String, String>> list=new ArrayList<>();
        String path="";
        //根据pid判断路径
        if (ObjectUtil.isNotNull(parentId)&&!parentId.equals("")){
            SysFile sysFileById = sysFileService.findSysFileById(parentId);
            if (ObjectUtil.isNotNull(sysFileById)){
                 path = sysFileById.getPath();
//                relativePath=path+"/"+relativePath;
            }
        }
        //得到路径后解析图片
        File file1 =FileUtil.createOrRenameFile(new File(filePath+path + "/" + fileName));
//        BufferedImage image = null;
//        byte[] imageByte = null;
//        try {
//            imageByte = DatatypeConverter.parseBase64Binary(file);
//            InputStream bis = new ByteArrayInputStream(imageByte);
//            image = ImageIO.read(bis);
//            ImageIO.write(image, file1.getName().substring(file1.getName().lastIndexOf(".")+1), file1);
//            bis.close();
//        } catch (IOException e) {
////            e.printStackTrace();
//        }
//        if (!file1.exists()){
            BASE64Decoder decoder = new BASE64Decoder();
            try
            {
                //Base64解码
                byte[] b = decoder.decodeBuffer(file);
                //  System.out.println("解码完成");
                for(int i=0;i<b.length;++i)
                {
                    if(b[i]<0)
                    {//调整异常数据
                        b[i]+=256;
                    }
                }
                // System.out.println("开始生成图片");
                //生成jpeg图片
                OutputStream out = new FileOutputStream(file1);
                out.write(b);
                out.flush();
                out.close();
            }  catch (Exception e){
            }
//        }

        //不包含文件后缀名
        Map<String, String> map = new HashMap<>();
        map.put("name", file1.getName());
        //获取保存文件路径
        String url = fileUtil.uploadFiles(file1,path);
        map.put("url", url);
        map.put("fileSize", checkFileSize(file1.length()));
        list.add(map);
        sysFileService.saveSysFile(file1,parentId,url);
        return new ResponseJson(list);
    }

    @PostMapping("/uploadFiles")
    @ApiOperation(value = "创建时间：2018-10-29。说明：上传文件到本地", notes = "返回资源名称和资源的url")
    public ResponseJson uploadFiles(@ApiParam(value = "上传文件到本地", required = true) MultipartFile file,String parentId,String relativePath,boolean isReplace) {
        if(ObjectUtil.isNull(parentId)||parentId.equals("")){
            parentId="-1";
        }
        List<Map<String, String>> list=new ArrayList<>();
        //文件名后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        int suffixInt = fileTypeUtil.setResouceType(suffix);
        if(suffixInt==0){
            return new ResponseJson(false,"不支持的文件格式");
        }
        //带目录结构
        if (ObjectUtil.isNotNull(parentId)&&!parentId.equals("")){
            SysFile sysFileById = sysFileService.findSysFileById(parentId);
            if (ObjectUtil.isNotNull(sysFileById)){
                String path = sysFileById.getPath();
                relativePath=path+"/"+relativePath;
            }
        }
        if (ObjectUtil.isNotNull(isReplace)&&isReplace){
            File file1 = new File(filePath + relativePath);
            if (file1.exists()){
//                return
                if (file1.length()==file.getSize())
                 return   new ResponseJson();
            }
        }
        //不包含文件后缀名
        Map<String, String> map = new HashMap<>();
        map.put("name", file.getOriginalFilename());
        //获取保存文件路径
        String url = fileUtil.uploadFile(file,"",relativePath,parentId,isReplace);
        map.put("url", url);
        map.put("relativePath",relativePath);
        map.put("fileSize", checkFileSize(file.getSize()));
        map.put("type",String.valueOf(suffixInt));
        list.add(map);
        sysFileService.saveSysFileByMultipartFile(file,parentId,url);
        return new ResponseJson(list);
    }

    @GetMapping("/uploadFileList")
    @ApiOperation(value = "创建时间：2018-10-29。说明：上传文件到本地", notes = "返回资源名称和资源的url")
    public ResponseJson uploadFileList(@ApiParam(value = "上传文件到本地") MultipartFile file) {
        return new ResponseJson();
    }

    @PostMapping("/uploadFilePlanIn")
    @ApiOperation(value = "创建时间：2018-10-29。说明：上传文件到七牛", notes = "返回资源名称和资源的url")
    public ResponseJson uploadFilePlanIn(@ApiParam(value = "上传到七牛的文件file", required = true) MultipartFile file) {
        //文件名后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        int suffixInt = fileTypeUtil.setResouceType(suffix);
        if(suffixInt!=1 && suffixInt!=2){
            return new ResponseJson(false,"不支持的文件格式");
        }
        //不包含文件后缀名
        Map<String, String> map = new HashMap<>();
        map.put("name", file.getOriginalFilename());
        //获取保存文件路径
        String url = fileUtil.uploadFile(file, Constant.Upload.UPLOAD_JOBPLANIN);
        //String url = QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_JOBPLANIN);
        map.put("url", url);
        map.put("fileSize", checkFileSize(file.getSize()));
        map.put("type", suffixInt+"");
        return new ResponseJson(map);
    }
    @PostMapping("/uploadFileProblem")
    @ApiOperation(value = "创建时间：2018-10-29。说明：上传文件到七牛", notes = "返回资源名称和资源的url")
    public ResponseJson uploadFileProblem(@ApiParam(value = "上传到七牛的文件file", required = true) MultipartFile file) {
        //文件名后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        int suffixInt = fileTypeUtil.setResouceType(suffix);
        if(suffixInt==0){
            return new ResponseJson(false,"不支持的文件格式");
        }
        //不包含文件后缀名
        Map<String, String> map = new HashMap<>();
        map.put("name", file.getOriginalFilename());
        //获取保存文件路径
        String url = fileUtil.uploadFile(file, Constant.Upload.UPLOAD_PROBLEM);
 //       String url = QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_PROBLEM);
        map.put("url", url);
        map.put("fileSize", checkFileSize(file.getSize()));
        map.put("type", suffixInt+"");
        return new ResponseJson(map);
    }
    @PostMapping("/uploadFileEmergencyTab")
    @ApiOperation(value = "创建时间：2018-10-29。说明：上传文件到七牛", notes = "返回资源名称和资源的url")
    public ResponseJson uploadFileEmergencyTab(@ApiParam(value = "上传到七牛的文件file", required = true) MultipartFile file) {
        //文件名后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        int suffixInt = fileTypeUtil.setResouceType(suffix);
        if(suffixInt==0){
            return new ResponseJson(false,"不支持的文件格式");
        }
        //不包含文件后缀名
        Map<String, String> map = new HashMap<>();
        map.put("fileName", file.getOriginalFilename());
        //获取保存文件路径
  //      String url = QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_PROBLEM);
        String url = fileUtil.uploadFile(file, Constant.Upload.UPLOAD_PROBLEM);
        map.put("url", url);
        map.put("fileSize", checkFileSize(file.getSize()));
        return new ResponseJson(map);
    }
    @PostMapping("/uploadJobCarFile")
    public ResponseJson uploadJobCarFile(@RequestParam("file") MultipartFile file){
        try{
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            Map<String, String> map = new HashMap<>();
            //获取保存文件路径
            String url = fileUtil.uploadFile(file, Constant.Upload.UPLOAD_JOBPLANCAR);
            //String url = QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_JOBPLANCAR);
            map.put("name", file.getOriginalFilename());
            map.put("url", url);
            map.put("suffix", suffix);
            map.put("size",String.valueOf(file.getSize()));
            return new ResponseJson(map);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseJson(false,"上传失败");
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
    /**
     * 给umeditor专用的上传图片
     * @param upfile
     * @return
     */
    @PostMapping("/upload4ume")
    public Map<String,Object> upload4ume(MultipartFile upfile){
//        "{\"name\":\""+ up.getFileName() +"\", \"originalName\": \""+
//                up.getOriginalName() +"\", \"size\": "+ up.getSize()
//                +", \"state\": \""+ up.getState() +"\", \"type\": \""+
//                up.getType() +"\", \"url\": \""+ up.getUrl() +"\"}";
        Map<String,Object> tm = new HashMap<>();
        tm.put("name",upfile.getName());
        tm.put("originalName",upfile.getOriginalFilename());
        tm.put("size",upfile.getSize());
        tm.put("state","SUCCESS");
        tm.put("type",upfile.getContentType());
//        tm.put("url",QiniuUtil.uploadImage(upfile, Constant.Upload.UPLOAD_AVATAR));
        tm.put("url",fileUtil.uploadFile(upfile, Constant.Upload.UPLOAD_AVATAR));
        return tm;
    }

    @GetMapping("/downLoadFile")
    public void downLoadFile(@RequestParam("path") String path, HttpServletResponse response){
        HttpKit.downloadFile(path, response);
    }

    @GetMapping("/downLoadFileLocal/{type}")
    public HttpServletResponse downLoadFileLocal(String path, HttpServletResponse response,@PathVariable String type) {
        try {
            // path是指欲下载的文件的路径。(包含文件夹？需要压缩？)
            if (path.substring(path.lastIndexOf("/")).contains(".")){
                File file = new File(filePath+path);
                // 取得文件名。
                String filename = file.getName();
                // 取得文件的后缀名。
                String ext = filename.substring(filename.lastIndexOf(".") + 1);

                int suffixInt = fileTypeUtil.setResouceType(ext);
                if(suffixInt==2){
                    //是否压缩图片
                    if (type.equals("1"))
                        Thumbnails.of(file).scale(1f).outputQuality(0.25f).toFile(file);
                }
                // 以流的形式下载文件。
                FileInputStream in = new FileInputStream(file);
                InputStream fis = new BufferedInputStream(in);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();
                // 设置response的Header
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
                response.addHeader("Content-Length", "" + file.length());
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                toClient.write(buffer);
                toClient.flush();
                toClient.close();

                return response;
            }
            File file1 = new File(filePath+path);
            File[] files = file1.listFiles();
            for (File file : files) {
                if(!file.exists()){
                    return null;
                }
                // 取得文件名。
                String filename = file.getName();
                // 取得文件的后缀名。
                String ext = filename.substring(filename.lastIndexOf(".") + 1);

                int suffixInt = fileTypeUtil.setResouceType(ext);
                if(suffixInt==2){
                    //是否压缩图片
                    if (type.equals("1"))
                        Thumbnails.of(file).scale(1f).outputQuality(0.25f).toFile(file);
                }

                // 以流的形式下载文件。
                FileInputStream in = new FileInputStream(file);
                InputStream fis = new BufferedInputStream(in);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();
                // 设置response的Header
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
                response.addHeader("Content-Length", "" + file.length());
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                toClient.write(buffer);
                toClient.flush();
                toClient.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    /**
     * 上传教材封面
     * @param file
     * @return
     */
    @PostMapping("/uploadMaterial")
    public String uploadMaterial(MultipartFile file){
//        return QiniuUtil.uploadImage(file, Constant.Upload.UPLOAD_MATERIAL);
        return fileUtil.uploadFile(file, Constant.Upload.UPLOAD_MATERIAL);
    }
    /**
     * 上传app图标
     * @param file
     * @return
     */
    @PostMapping("/uploadAppIcon")
    public String uploadAppIcon(MultipartFile file){
//        return QiniuUtil.uploadImage(file, Constant.Upload.UPLOAD_APP_ICON);
        return QiniuUtil.uploadImage(file, Constant.Upload.UPLOAD_APP_ICON);
    }
    @PostMapping("/uploadCover")
    public String uploadCover(MultipartFile file){
//        return QiniuUtil.uploadImage(file, Constant.Upload.UPLOAD_COVER);
        return QiniuUtil.uploadImage(file, Constant.Upload.UPLOAD_COVER);
    }
    @PostMapping("/uploadImgForCks")
    public Map<String, Object> uploadImgForCks(@RequestParam("upload") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        String ext = QiniuUtil.getExt(file);
        if (".jpg".equals(ext)
                || ".png".equals(ext)
                || ".gif".equals(ext)
                || ".bmp".equals(ext)
                || ".jpeg".equals(ext)
                ) {
            if (file.getSize() > 3 * 1024 * 1024) {
                map.put("uploaded", 0);
                Map<String, String> error = new HashMap<>();
                error.put("message", "图片大小不能超过3MB");
                map.put("error", error);
                return map;
            }
            map.put("uploaded", 1);
            map.put("fileName", file.getOriginalFilename());
//            map.put("url", Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_IMAGE));
            map.put("url", Constant.RES_PRE + fileUtil.uploadFile(file, Constant.Upload.UPLOAD_EDITOR_IMAGE));
            return map;
        } else {
            map.put("uploaded", 0);
            Map<String, String> error = new HashMap<>();
            error.put("message", "只能上传图片");
            map.put("error", error);
            return map;
        }

    }

    @PostMapping("/uploadVideo")
    public Map<String, Object> uploadVideo(@RequestParam("upload") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        String ext = QiniuUtil.getExt(file);
        if (".mp4".equals(ext)
                || ".mov".equals(ext)
                || ".rmvb".equals(ext)
                || ".mkv".equals(ext)
                || ".flv".equals(ext)
                || ".wmv".equals(ext)
                || ".avi".equals(ext)
                || ".mpg".equals(ext)
                || ".webm".equals(ext)
                ) {
            if (file.getSize() > 300 * 1024 * 1024) {
                map.put("uploaded", 0);
                Map<String, String> error = new HashMap<>();
                error.put("message", "视频大小不能超过300MB");
                map.put("error", error);
                return map;
            }
            map.put("uploaded", 1);
            map.put("fileName", file.getOriginalFilename());
//            map.put("url", Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_VEDIO));
            map.put("url", Constant.RES_PRE + fileUtil.uploadFile(file, Constant.Upload.UPLOAD_EDITOR_VEDIO));
            return map;
        } else {
            map.put("uploaded", 0);
            Map<String, String> error = new HashMap<>();
            error.put("message", "只能上传视频");
            map.put("error", error);
            return map;
        }

    }

    @PostMapping("/uploadAudio")
    public Map<String, Object> uploadAudio(@RequestParam("upload") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        String ext = QiniuUtil.getExt(file);
        if (".mp3".equals(ext)
                || ".ape".equals(ext)
                || ".ogg".equals(ext)
                || ".wav".equals(ext)
                ) {
            if (file.getSize() > 10 * 1024 * 1024) {
                map.put("uploaded", 0);
                Map<String, String> error = new HashMap<>();
                error.put("message", "音频大小不能超过10MB");
                map.put("error", error);
                return map;
            }
            map.put("uploaded", 1);
            map.put("fileName", file.getOriginalFilename());
//            map.put("url", Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_AUDIO));
            map.put("url", Constant.RES_PRE + fileUtil.uploadFile(file, Constant.Upload.UPLOAD_EDITOR_AUDIO));
            return map;
        } else {
            map.put("uploaded", 0);
            Map<String, String> error = new HashMap<>();
            error.put("message", "只能上传音频");
            map.put("error", error);
            return map;
        }

    }

    @PostMapping("/uploadSvgString")
    public ResponseJson uploadSvgString(@RequestBody String base64) throws IOException {
        String decodeStr = URLDecoder.decode(base64, "utf-8");
        byte[] bytes = decodeStr.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        String key = Constant.Upload.UPLOAD_EDITOR_IMAGE+QiniuUtil.getDatePath() + "/"+ QiniuUtil.newName()+".svg";
        String s = QiniuUtil.commonUploadInputstreamForKey(inputStream, key);
        return new ResponseJson(s);
    }
    @PostMapping("/convertMmlToImage")
    public ResponseJson convertMmlToImage(@RequestBody String mml) throws IOException {
        HttpRequest request = HttpRequest.post(Constant.THIRDPARTY_SERVICE.WIRIS_SHOW_IMAGE)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body("mml=" + URLEncoder.encode(mml, "UTF-8") + "&lang=zh-cn&metrics=true&centerbaseline=false");
        HttpResponse response = request.execute();
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        String svg = jsonObject.getJSONObject("result").getStr("content");
        byte[] bytes = svg.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        String key = Constant.Upload.UPLOAD_EDITOR_IMAGE+QiniuUtil.getDatePath() + "/"+ QiniuUtil.newName()+".svg";
        String s = QiniuUtil.commonUploadInputstreamForKey(inputStream, key);
        return new ResponseJson(s);

    }
    /**
     * ck编辑器里自定义的上传图片功能
     * @param file
     * @return
     */
    @PostMapping("/uploadImgForImageBox")
    public ResponseJson uploadImgForImageBox(@RequestParam("file") MultipartFile file) {
        String ext = QiniuUtil.getExt(file);
        if (".jpg".equals(ext)
                || ".png".equals(ext)
                || ".gif".equals(ext)
                || ".bmp".equals(ext)
                || ".jpeg".equals(ext)
                ) {
            if (file.getSize() > 10 * 1024 * 1024) {
                return new ResponseJson(false,"图片大小不能超过10MB");
            }
//            return new ResponseJson(Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_IMAGE));
            return new ResponseJson(Constant.RES_PRE + fileUtil.uploadFile(file, Constant.Upload.UPLOAD_EDITOR_IMAGE));
        } else {
            return new ResponseJson(false,"只能上传图片");
        }

    }
    @PostMapping("/uploadImgForCk&responseType=json")
    public Map<String, Object> uploadImgForCk(@RequestParam("upload") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        String ext = QiniuUtil.getExt(file);
        if (".jpg".equals(ext)
                || ".png".equals(ext)
                || ".gif".equals(ext)
                || ".bmp".equals(ext)
                || ".jpeg".equals(ext)
                ) {
            if (file.getSize() > 3 * 1024 * 1024) {
                map.put("uploaded", 0);
                Map<String, String> error = new HashMap<>();
                error.put("message", "图片大小不能超过3MB");
                map.put("error", error);
                return map;
            }
            map.put("uploaded", 1);
            map.put("fileName", file.getOriginalFilename());
//            map.put("url", Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_IMAGE));
            map.put("url", Constant.RES_PRE + fileUtil.uploadFile(file, Constant.Upload.UPLOAD_EDITOR_IMAGE));
            return map;
        } else {
            map.put("uploaded", 0);
            Map<String, String> error = new HashMap<>();
            error.put("message", "只能上传图片");
            map.put("error", error);
            return map;
        }

    }

    // 递归方式 计算文件的大小
    private long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    @PostMapping("/checkDown")
    public boolean checkDown(@RequestBody SysFile sysFile){
        String path = sysFile.getPath();
        if (ObjectUtil.isNull(path)){
            path="";
        }
        File file1 = new File(filePath + path);
        if (file1.isDirectory()){
            if (getTotalSizeOfFilesInDir(file1)>300*1024*1024)
                return false;
        }
        return true;
    }
    /**
     *  下载
     * @return
     */
//    @Async
    @PostMapping("/downLoadDIROrFile/{type}")
    public void down( HttpServletResponse response,@PathVariable String type, @RequestBody SysFile sysFile,HttpServletRequest request){
        //type 0为原图下载，1为压缩
        SysFileLog sysFileLog = sysFileService.creatSysFileLog(sysFile);
        sysFileLog.setOperation(Constant.FILE_CHANGE_STUTUS.Download);
        if (ObjectUtil.isNull(sysFile.getAddress())){
            sysFile.setAddress("");
        }
        sysFileLog.setComment(sysFileLog.getAdminName()+" "+sysFileLog.getOperation()+"了 "+sysFile.getAddress()+"/"+sysFile.getTitle());
        sysFileLogService.saveSysFileLog(sysFileLog);
        String path = sysFile.getPath();
        if (ObjectUtil.isNull(path)){
            path="";
        }
        File file1 = new File(filePath + path);
        if (file1.isDirectory()){

            //指定下载的路径
            String name=filePath+path.substring(0,path.lastIndexOf("/")+1)+System.currentTimeMillis()+".zip";
            ZipCompressor zc = new ZipCompressor(name);
            File file=null;
            try {
                zc.compress(sysFileService,type,filePath+path);
                file=new File(name);
                //写入response
                ZipCompressor.downloadZip(file,response);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(file!=null){
                    file.delete();
                }
            }
        }else{
            downLoadFile(file1,response,type);
        }
    }

    public HttpServletResponse downLoadFile(File file, HttpServletResponse response,String type) {
        try {
            File file1=null;
            if (type.equals("1")){
                String path = file.getPath();
                String fileName = file.getName();
                int index = fileName.lastIndexOf(".");
                String name = fileName.substring(0, index);
                String suff = fileName.substring(index);
                file1 = new File(path + name+"_img"+suff);
                if (file.length()>2048000){
                    Thumbnails.of(file).scale(0.6f).outputQuality(0.25f).toFile(file1); }
                else {
                    Thumbnails.of(file).scale(0.8f).outputQuality(0.25f).toFile(file1);
                }
                file=file1;
            }
            // 以流的形式下载文件。
            FileInputStream in = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(in);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            file1.delete();
            toClient.close();
            return response;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }
}
