package top.cfl.cflwork.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于yed项目权限放行
 */
@ConfigurationProperties(prefix = "cflwork.auth")
@Component
@Data
public class CflworkAuthIgnore {
    private List<String> ignores;

}
