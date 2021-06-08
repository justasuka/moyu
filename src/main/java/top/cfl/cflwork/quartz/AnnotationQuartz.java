package top.cfl.cflwork.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.cfl.cflwork.service.AnnotationQuartzService;

/**
 * 定时任务
 * @author Administrator
 *
 */
@Component
@EnableScheduling
public class AnnotationQuartz {

    @Autowired
    private AnnotationQuartzService annotationQuartzService;

    /**
     * 每天一点执行执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void annotationQuartzStatusToDay() {
        annotationQuartzService.annotationQuartzStatus();
    }

    /*@Component
    @Configuration     //1.主要用于标记配置类，兼备Component的效果。
    @EnableScheduling   // 2.开启定时任务
    public class ScheduledUtil {
        //@Scheduled(cron = "0/5 * * * * ?")  //每隔5秒执行一次定时任务
        public void consoleInfo(){
            String path = filePath+"mysqlBackup"+File.separatorChar+dbName+DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss")+".sql";
            MysqlBackUp.backup(host,port,dbName,username,password,path);
            mailService.sendAttachmentsMail(email, "sql备份", "",path);
        }
    }*/




}
