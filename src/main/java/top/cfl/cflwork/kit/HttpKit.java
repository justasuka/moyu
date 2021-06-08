package top.cfl.cflwork.kit;

import cn.hutool.core.util.StrUtil;
import top.cfl.cflwork.pojo.Constant;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/*
 */
public class HttpKit {
    public static void downloadFile(String path, HttpServletResponse response) {
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            URL url = new URL(Constant.RES_PRE + (path.startsWith("/")?path:("/"+path)));
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(3000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            response.setContentLength(urlConnection.getContentLength());
            response.setCharacterEncoding("utf-8");
            response.addHeader("Content-Type", "application/octet-stream");
            String disposition = urlConnection.getHeaderField("Content-Disposition");
            response.addHeader("Content-Disposition", StrUtil.isNotEmpty(disposition)?disposition.replaceFirst("inline;","attachment;"):"attachment; filename=\"文件.jpg\"; filename*=utf-8' '文件.jpg");
            outputStream = response.getOutputStream();
            int i = -1;
            byte[] b = new byte[1024];
            while ((i = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, i);
                outputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
