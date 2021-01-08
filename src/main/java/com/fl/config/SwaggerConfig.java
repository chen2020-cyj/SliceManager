package com.fl.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * @Author : 陈友江
 * @create 2020/11/5 11:24
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {


    private String tokenHeader = "token";


    @Value("${swagger.enabled}")
    private Boolean enabled;



    @Bean
    @SuppressWarnings("all")
    public Docket createRestApi(){
        ParameterBuilder ticketPick = new ParameterBuilder();
        ParameterBuilder ticketPick1 = new ParameterBuilder();

        List<Parameter> pars = new ArrayList<>();

        ticketPick.name("page").description("页数")
                .modelRef(new ModelRef("Integer"))
                .parameterType("query")
                .defaultValue("1")
                .required(false)
                .build();
        ticketPick1.name("limit").description("数量")
                .modelRef(new ModelRef("Integer"))
                .parameterType("query")
                .defaultValue("10")
                .required(false)
                .build();
//        pars.add(ticketPick.build());
//        pars.add(ticketPick1.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enabled)
                .apiInfo(apiInfo())
                .select()
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                .globalOperationParameters(pars)
                .forCodeGeneration(true) // 将泛型中的类型扩展到文档中
                // .enable(false) //文档开关，设置为false后，文档将不能被访问
                // .pathMapping("/v2") // 每个接口前统一增加前缀
                // .tags(new Tag("demo1","示例1"), new Tag("demo2", "示例2"))
                .protocols(Sets.newHashSet("http", "https")); // 协议
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .description("zzz")
                .title("zzz")
                .version("1.111")
                .build();
    }

    private List<ApiKey> securitySchemes() {
        return newArrayList(new ApiKey(tokenHeader, tokenHeader, "header"));
    }

    private List<SecurityContext> securityContexts() {
        return newArrayList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build());
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(new SecurityReference(tokenHeader, authorizationScopes));
    }

}


@Configuration
class SwaggerDataConfig{
    @Bean
    public AlternateTypeRuleConvention pageableConvention(final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return newArrayList(newRule(resolver.resolve(Pageable.class), resolver.resolve(Page.class)));
            }
        };


    }


    @ApiModel
    @Data
    private static class Page {
        @ApiModelProperty("页码 (0..N)")
        private Integer page;

        @ApiModelProperty("每页显示的数目")
        private Integer size;

        @ApiModelProperty("以下列格式排序标准：property[,asc | desc]。 默认排序顺序为升序。 支持多种排序条件：如：id,asc")
        private List<String> sort;
    }

}
