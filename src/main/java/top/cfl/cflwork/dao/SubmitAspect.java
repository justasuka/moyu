package top.cfl.cflwork.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import top.cfl.cflwork.pojo.ResponseJson;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@Aspect
@Configuration
public class SubmitAspect {
    private final Cache<String, Object> CACHES = CacheBuilder.newBuilder()
            // 最大缓存 100 个
            .maximumSize(100)
            // 设置缓存过期时间为S
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build();

    @Pointcut("@annotation(top.cfl.cflwork.dao.RepeatSubmit)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RepeatSubmit form = method.getAnnotation(RepeatSubmit.class);
        String key = getCacheKey(myId(), method, pjp.getArgs());
        if (!StringUtils.isEmpty(key)) {
            if (CACHES.getIfPresent(key) == null) {
                // 如果是第一次请求,就将key存入缓存中
                CACHES.put(key, key);
            } else {
                return  new ResponseJson("请勿重复请求");
            }
        }
        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException("服务器异常");
        }
    }

    /**
     * 加上用户的唯一标识
     */
    private String getCacheKey(String uid, Method method, Object[] args) {
        return uid + "/" + method.getName();
    }
}