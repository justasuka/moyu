package top.cfl.cflwork;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Configuration
@EnableMethodCache(basePackages = "top.cfl.cflwork")
@EnableCreateCacheAnnotation
@EnableAsync
public class MoyuApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoyuApplication.class, args);
	}
}
