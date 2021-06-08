package top.cfl.cflwork.controller.upload;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cfl.cflwork.kit.HttpKit;
import top.cfl.cflwork.pojo.Constant;
import top.cfl.cflwork.pojo.Pager;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.util.FileTypeUtil;
import top.cfl.cflwork.util.QiniuUtil;
import top.cfl.cflwork.util.zip.ZipUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/uploads")
@RestController
public class UploadControllers {

    @Autowired
    private FileTypeUtil fileTypeUtil;
    @Autowired
    private ZipUtils zipUtils;
    @Value("${file.filePath}")
    private  String filePath;

    @PostMapping("/uploadAvatar")
    public String uploadAvatar(MultipartFile file){
        return QiniuUtil.uploadImage(file, Constant.Upload.UPLOAD_AVATAR);

    }
    @PostMapping("/uploadFile")
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
        String url = filePath;
        map.put("url", url);
        map.put("fileSize", checkFileSize(file.getSize()));
        map.put("type",String.valueOf(suffixInt));
        return new ResponseJson(map);
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
//        String url = fileUtil.uploadFile(file, Constant.Upload.UPLOAD_JOBPLANIN);
        String url = QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_JOBPLANIN);
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
//        String url = fileUtil.uploadFile(file, Constant.Upload.UPLOAD_PROBLEM);
        String url = QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_PROBLEM);
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
        String url = QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_PROBLEM);
//        String url = fileUtil.uploadFile(file, Constant.Upload.UPLOAD_PROBLEM);
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
//            String url = fileUtil.uploadFile(file, Constant.Upload.UPLOAD_JOBPLANCAR);
            String url = QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_JOBPLANCAR);
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
        tm.put("url",QiniuUtil.uploadImage(upfile, Constant.Upload.UPLOAD_AVATAR));
        return tm;
    }

    @GetMapping("/downLoadFile")
    public void downLoadFile(@RequestParam("path") String path, HttpServletResponse response){
        HttpKit.downloadFile(path, response);
    }

    @GetMapping("/downLoadFileLocal")
    public HttpServletResponse downLoadFileLocal(String path, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(filePath+path);
            if(!file.exists()){
                return null;
            }

            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
            if (fileTypeUtil.setResouceType(ext)==2){
                try {
                    Thumbnails.of(file).scale(1f).outputQuality(0.25f).toFile(file);
                }catch (Exception e){
                    System.err.println("压缩失败");
                }
            }
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(filePath+path));
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
        return QiniuUtil.uploadImage(file, Constant.Upload.UPLOAD_MATERIAL);
    }
    /**
     * 上传app图标
     * @param file
     * @return
     */
    @PostMapping("/uploadAppIcon")
    public String uploadAppIcon(MultipartFile file){
        return QiniuUtil.uploadImage(file, Constant.Upload.UPLOAD_APP_ICON);
    }
    @PostMapping("/uploadCover")
    public String uploadCover(MultipartFile file){
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
            map.put("url", Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_IMAGE));
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
            map.put("url", Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_VEDIO));
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
            map.put("url", Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_AUDIO));
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
            return new ResponseJson(Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_IMAGE));
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
            map.put("url", Constant.RES_PRE + QiniuUtil.commonUploadFile(file, Constant.Upload.UPLOAD_EDITOR_IMAGE));
            return map;
        } else {
            map.put("uploaded", 0);
            Map<String, String> error = new HashMap<>();
            error.put("message", "只能上传图片");
            map.put("error", error);
            return map;
        }

    }
}
