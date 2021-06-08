package top.cfl.cflwork.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.cfl.cflwork.service.SysLogService;

/**
 * Created by 陈飞龙 on 2019/5/8 15:59
 */
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class ScheduledUtil {

    @Autowired
    private SysLogService sysLogService;
    @Scheduled(cron = "0 0 0 1/5 * ?")  //每隔5秒执行一次定时任务
    /**
     * 用于清空一个月之前的日志记录
     */
    public void consoleInfo(){
        //每隔两天删除日志
        sysLogService.deleteManySysLog();
    }
}
