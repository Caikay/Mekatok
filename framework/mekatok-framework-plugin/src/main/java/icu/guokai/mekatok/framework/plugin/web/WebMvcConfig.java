package icu.guokai.mekatok.framework.plugin.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import icu.guokai.mekatok.framework.core.constant.Global;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 用于配置 MVC 的转发和指定以错误页面的注册
 * @author GuoKai
 * @date 2021/8/16
 */
@Configuration
@SuppressWarnings("all")
public class WebMvcConfig implements WebMvcConfigurer, ErrorPageRegistrar {

    /**
     * 用于在序列化时对空值的转换
     */
    private static final JsonSerializer<Object> NULL_VALUE_SERIALIZER = new JsonSerializer<Object>() {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeNull();
        }
    };

    /**
     * 注册 消息转换器 至队列
     * @param converters 转换器队列
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0,new MappingJackson2HttpMessageConverter(defaultObjectMapping()));
    }

    /**
     * 定义转换映射
     * @return 转换映射
     */
    @Bean
    public ObjectMapper defaultObjectMapping() {
        var mapping = new ObjectMapper();
        // 控制转换为 {}
        mapping.getSerializerProvider().setNullValueSerializer(NULL_VALUE_SERIALIZER);
        //加载序列化和反序列化规则
        var simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalTime.class,new LocalTimeSerializer(DateTimeFormatter.ofPattern(Global.TIME_FORMAT)))
                .addSerializer(LocalDate.class,new LocalDateSerializer(DateTimeFormatter.ofPattern(Global.DATE_FORMAT)))
                .addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(Global.DATETIME_FORMAT)))
                .addDeserializer(LocalTime.class,new LocalTimeDeserializer(DateTimeFormatter.ofPattern(Global.TIME_FORMAT)))
                .addDeserializer(LocalDate.class,new LocalDateDeserializer(DateTimeFormatter.ofPattern(Global.DATE_FORMAT)))
                .addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(Global.DATETIME_FORMAT)));
        //注册至映射
        mapping.registerModule(simpleModule);
        return mapping;
    }

    /**
     * 注册 用于处理 异常状态码 的控制器
     * @param registry 异常页面注册
     */
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        registry.addErrorPages(
                new ErrorPage(HttpStatus.UNAUTHORIZED, "/" + HttpStatus.UNAUTHORIZED.value()),
                new ErrorPage(HttpStatus.FORBIDDEN, "/" + HttpStatus.FORBIDDEN.value()),
                new ErrorPage(HttpStatus.NOT_FOUND, "/" + HttpStatus.NOT_FOUND.value())
        );
    }

    /**
     * 设置 Cors
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**").allowedOriginPatterns("*")
                .allowedHeaders("*").allowedMethods("*").allowCredentials(true).maxAge(3600);
    }
}
