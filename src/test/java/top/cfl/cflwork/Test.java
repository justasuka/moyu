package top.cfl.cflwork;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

public class Test {
    public static void main(String[] args) {
        // 读取txt内容为字符串
        StringBuilder txtContent = new StringBuilder();
        // 每次读取的byte数
        byte[] b = new byte[8 * 1024];
        InputStream in = null;
        try
        {
            // 文件输入流
            in = new FileInputStream("F:\\WeChat Files\\wxid_6abniv2y7c4h22\\FileStorage\\File\\2021-06\\1(2).txt");
//            in = new FileInputStream("F:\\14.txt");
//            GenerateImage(in,"F:\\12.jpeg");
            while (in.read(b) != -1)
            {
                // 字符串拼接
                txtContent.append(new String(b));

            }
            // 关闭流
            in.close();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        System.out.println(txtContent.toString());
//        GetImageStr();
        GenerateImage(txtContent.toString(),"F:\\15.jpeg");
//        convertBase64ToFile(txtContent.toString(),"F:\\","13.jpeg");
    }


//    public static boolean GenerateImage(InputStream imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
//        BASE64Decoder decoder = new BASE64Decoder();
//        try {
//            // Base64解码
//            byte[] bytes = decoder.decodeBuffer(imgStr);
//            for (int i = 0; i < bytes.length; ++i) {
//                if (bytes[i] < 0) {// 调整异常数据
//                    bytes[i] += 256;
//                }
//            }
//            // 生成jpeg图片
//            OutputStream out = new FileOutputStream(imgFilePath);
//            out.write(bytes);
//            out.flush();
//            out.close();
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }


    public static boolean GenerateImage(String imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) // 图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        System.out.println(imgStr);
        imgStr = imgStr.split(",")[1];
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * 将base64字符串，生成文件
     */
    public static File convertBase64ToFile(String fileBase64String, String filePath, String fileName) {

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bfile = decoder.decodeBuffer(fileBase64String);

            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    // 图片转化成base64字符串
    public static String GetImageStr() {
        String imgFile = "F:\\122.png";// 待处理的图片
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        System.out.println(encoder.encode(data));
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }
}
