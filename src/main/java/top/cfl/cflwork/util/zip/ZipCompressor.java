//package top.cfl.cflwork.util.zip;
//
//import net.coobird.thumbnailator.Thumbnails;
//import org.springframework.beans.factory.annotation.Autowired;
//import top.cfl.cflwork.util.FileTypeUtil;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.*;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.zip.CRC32;
//import java.util.zip.CheckedOutputStream;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
///**
// * 压缩类
// * @author xrj
// * @date 2019/10/31
// */
//public class ZipCompressor {
//
//    static final int BUFFER = 10240;
//
//    /**
//     * 压缩的文件夹
//     */
//    private File zipFile;
//
//    public ZipCompressor(String pathName) {
//        zipFile = new File(pathName);
//    }
//
//
//    /**
//     * 遍历需要压缩文件集合
//     * @param pathName
//     * @throws IOException
//     */
//    public void compress( SysFileService sysFileService,String type,String... pathName) throws IOException {
//        ZipOutputStream out =null;
//        FileOutputStream fileOutputStream=null;
//        CheckedOutputStream cos=null;
//        try {
//            fileOutputStream = new FileOutputStream(zipFile);
//            cos = new CheckedOutputStream(fileOutputStream,new CRC32());
//            out = new ZipOutputStream(cos);
//            String basedir = "";
//            for (int i=0;i<pathName.length;i++){
//                compress(new File(pathName[i]), out, basedir,type,sysFileService);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }finally {
//            if(out!=null){
//                out.close();
//            }
//            if(fileOutputStream!=null){
//                fileOutputStream.close();
//            }
//            if(cos!=null){
//                cos.close();
//            }
//        }
//    }
//    /**
//     * 压缩
//     * @param file
//     * @param out
//     * @param basedir
//     */
//    private void compress(File file, ZipOutputStream out, String basedir,String type, SysFileService sysFileService) throws IOException {
//        // 判断是目录还是文件
//        if (file.isDirectory()) {
//            //判断是否属于权限文件夹
//            boolean flag = sysFileService.checkFIleInRole(file);
//            if (flag)
//                this.compressDirectory(type,file, out, basedir,sysFileService);
//        } else {
//            String path = file.getPath();
//            String fileName = file.getName();
//            int index = fileName.lastIndexOf(".");
//            String name = fileName.substring(0, index);
//            String suff = fileName.substring(index);
//            int indexOf = path.indexOf(fileName);
//            File file1 = new File(path.substring(0,indexOf) + name+"_img"+suff);
//            //图片压缩
//            String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
//            int suffixInt = new FileTypeUtil().setResouceType(suffix);
//            if (suffixInt==2){
//                if (type.equals("1")){
//                    if (file.length()>2048000){
//                        Thumbnails.of(file).scale(0.3f).outputQuality(0.25f).toFile(file1);
//                    }else {
//                        Thumbnails.of(file).scale(0.5f).outputQuality(0.25f).toFile(file1);
//                    }
//                    this.compressFile(file1, out, basedir);
//                    file1.delete();
//                }else {
//                    this.compressFile(file, out, basedir);
//                }
//            }
//        }
//    }
//    /**
//     * 压缩一个目录
//     * */
//    private void compressDirectory(String type,File dir, ZipOutputStream out, String basedir, SysFileService sysFileService) throws IOException {
//        if (!dir.exists()){
//            return;
//        }
//        File[] files = dir.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            // 递归
//            compress(files[i], out, basedir + dir.getName() + "/",type,sysFileService);
//        }
//    }
//    /**
//     * 压缩一个文件
//     *
//     * */
//    private void compressFile(File file, ZipOutputStream out, String basedir) throws IOException {
//        if (!file.exists()) {
//            return;
//        }
//        BufferedInputStream bis =null;
//        try {
//            bis = new BufferedInputStream(new FileInputStream(file));
//            ZipEntry entry = new ZipEntry(basedir + file.getName());
//            out.putNextEntry(entry);
//            int count;
//            byte[] data = new byte[BUFFER];
//            while ((count = bis.read(data, 0, BUFFER)) != -1) {
//                out.write(data, 0, count);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }finally {
//            if(bis!=null){
//                bis.close();
//            }
//        }
//    }
//
//    /**
//     * 下载压缩包
//     * @param file
//     * @param response
//     * @return
//     */
//    public static HttpServletResponse downloadZip(File file, HttpServletResponse response) {
//        InputStream fis = null;
//        OutputStream toClient = null;
//        try {
//            // 以流的形式下载文件。
//            fis = new BufferedInputStream(new FileInputStream(file.getPath()));
//            byte[] buffer = new byte[fis.available()];
//            fis.read(buffer);
//            // 清空response
//            response.reset();
//            toClient = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/octet-stream");
//
//            //如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
//            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
//            toClient.write(buffer);
//            toClient.flush();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }finally{
//            try {
//                File f = new File(file.getPath());
//                f.delete();
//                if(fis!=null){
//                    fis.close();
//                }
//                if(toClient!=null){
//                    toClient.close();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return response;
//    }
//}
//
//
