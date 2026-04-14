package cms.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.*;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson扩展封装
 * RedisCacheTypeFixAspect.java类在配合本类使用
 */
@Configuration
public class JacksonConfig {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);


    @Bean
    @Primary
    public JsonMapper objectMapper() {
        // 使用 Jackson 3.x 的 JsonMapper.builder() 构建器
        SimpleModule customModule = buildCustomModule();
        final JsonMapper mapper = JsonMapper.builder()
                // 注册模块
                .addModule(customModule)

                // 设置日期时间格式
                .defaultDateFormat(new java.text.SimpleDateFormat(DATE_TIME_FORMAT))

                // 设置时区
                //.defaultTimeZone(TimeZone.getTimeZone("GMT+8"))

                // JSON转POJO时，若JSON中的某个字段在POJO中未定义，忽略
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

                // 允许出现特殊字符和转义符 (使用 3.x 的 JsonReadFeature)
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)


                .build();

        return mapper;
    }



    /**
     * 构建包含 Long 和 LocalDateTime 定制的模块
     */
    private SimpleModule buildCustomModule() {
        SimpleModule simpleModule = new SimpleModule();

        // 将所有的 long 类型序列化为 string
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        //序列化LocalDateTime 类型
        simpleModule.addSerializer(LocalDateTime.class, new  LocalDateTimeSerializer(FORMATTER));
        //反序列化 LocalDateTime 类型
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));

        return simpleModule;
    }



}
