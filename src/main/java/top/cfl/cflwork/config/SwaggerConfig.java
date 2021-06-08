package top.cfl.cflwork.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import top.cfl.cflwork.pojo.Constant;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@ComponentScan("top.cfl.cflwork.controller")
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket loginApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("登录管理")
                .apiInfo(apiInfo())
                .select()
                .paths(loginPaths())
                .build();
    }

    private Predicate<String> loginPaths() {
        return or(
                regex("/login/.*")

        );
    }

    @Bean
    public Docket sysBannerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("轮播图管理")
                .apiInfo(apiInfo())
                .globalOperationParameters(setHeaderToken())
                .select()
                .paths(sysBannerPaths())
                .build();
    }

    private Predicate<String> sysBannerPaths() {
        return or(
                regex("/sysBanner/.*")

        );
    }

    @Bean
    public Docket sysAccountApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户管理")
                .apiInfo(apiInfo())
                .globalOperationParameters(setHeaderToken())
                .select()
                .paths(sysAccountPaths())
                .build();
    }


    private Predicate<String> sysAccountPaths() {
        return or(
                regex("/sysAccount/.*")

        );
    }

    @Bean
    public Docket sysSubscribeApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("预约管理")
                .apiInfo(apiInfo())
                .globalOperationParameters(setHeaderToken())
                .select()
                .paths(sysSubscribePaths())
                .build();
    }


    private Predicate<String> sysSubscribePaths() {
        return or(
                regex("/sysSubscribe/.*")

        );
    }

    @Bean
    public Docket sysContentApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("内容管理")
                .apiInfo(apiInfo())
                .globalOperationParameters(setHeaderToken())
                .select()
                .paths(sysContentPaths())
                .build();
    }


    private Predicate<String> sysContentPaths() {
        return or(
                regex("/sysContent/.*")

        );
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("cflwork")
                .description("<h4>接口里pager对象只在查询列表时用到</h4>")
                .termsOfServiceUrl("http://springfox.io")
                .contact(new Contact("文件前缀地址","http://lw.mykefang.com","test@163.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("1.0")
                .build();
    }
    private List<Parameter> setHeaderToken() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("token").description("令牌验证").modelRef(new ModelRef("string")).parameterType("header").required(true).build();
        pars.add(tokenPar.build());
        return pars;
    }

}
