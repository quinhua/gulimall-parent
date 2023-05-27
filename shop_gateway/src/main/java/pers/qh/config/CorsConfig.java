package pers.qh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        //允许跨域请求
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*"); //允许所有方法，Get、Post、Put...
        config.addAllowedOrigin("*"); //允许所有来源，客户端的ip
        config.addAllowedHeader("*"); //允许所有请求头，请求头中可以传递任意参数

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config); //对所有路径生效

        return new CorsWebFilter(source);
    }
}