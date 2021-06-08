package top.cfl.cflwork.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.cfl.cflwork.annotation.Log;
import top.cfl.cflwork.pojo.SysLog;
import top.cfl.cflwork.service.SysLogService;
import top.cfl.cflwork.util.HttpContextUtils;
import top.cfl.cflwork.util.IPUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static top.cfl.cflwork.interceptor.LoginInterceptor.currentAdmin;
import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@Aspect
@Component
public class LogAspect {
    @Autowired
    public SysLogService sysLogService;
    @Pointcut("@annotation(top.cfl.cflwork.annotation.Log)")
    public void logPointCut() {
    }
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // 执行方法
        Object result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //异步保存日志
        saveLog(point, time);
        return result;
    }

    public void saveLog(ProceedingJoinPoint joinPoint, long time) throws InterruptedException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();
        Log syslog = method.getAnnotation(Log.class);
        if (syslog != null) {
            // 注解上的描述
            sysLog.setOperation(syslog.value());
        }
        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");
        // 请求的参数
        Object[] args = joinPoint.getArgs();
        // 获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        // 设置IP地址
        sysLog.setIp(IPUtils.getIpAddr(request));
//        sysLog.setParams(JSON.toJSONString(args));
        try{
            sysLog.setUserId(myId());
            sysLog.setUsername(currentAdmin().getUsername());
        }catch (Exception e){
            sysLog.setUserId(null);
            sysLog.setUsername(null);
        }
        sysLog.setTime((int) time);
        sysLogService.saveSysLog(sysLog);
    }
}
