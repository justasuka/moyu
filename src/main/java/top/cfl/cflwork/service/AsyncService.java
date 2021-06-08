package top.cfl.cflwork.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.cfl.cflwork.dao.SysFileDao;
import top.cfl.cflwork.pojo.SysFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AsyncService {
    @Value("${file.filePath}")
    private  String filePath;
    @Autowired
    private SysFileDao sysFileDao;
    @Async
    public void updateImgHandW(List<SysFile> sysFileList){
        sysFileList.stream().forEach(e->{
            updateImgHandW(e);
        });
    }
    @Async
    public void updateImgHandW(SysFile sysFile){
            try {
                if ("2".equals(sysFile.getType())){
                    File input = new File(filePath + sysFile.getPath());
                    BufferedImage read = ImageIO.read(input);
                    int height = read.getHeight();
                    int width = read.getWidth();
//                    int i = height - width;
//                    if (i>0){
//                        sysFile.setShape("竖图");
//                    }else if (i<0){
//                        sysFile.setShape("横图");
//                    }else {
//                        sysFile.setShape("方形");
//                    }
//                    sysFile.setShootingTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(input.lastModified())));
                    sysFile.setHeight(String.valueOf(height));
                    sysFile.setWidth(String.valueOf(width));
                    sysFileDao.updateSysFileWithOutUpdatetime(sysFile);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
    }
}
