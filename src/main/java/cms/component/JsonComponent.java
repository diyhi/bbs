package cms.component;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Json组件
 */
@Component("jsonComponent")
public class JsonComponent {
    private static final Logger logger = LogManager.getLogger(JsonComponent.class);

    @Resource
    ObjectMapper objectMapper;

    /**
     * JSON串转换为Java泛型对象
     * @param jsonString JSON字符串
     * @param tr TypeReference,例如: new TypeReference< List<FamousUser> >(){}
     * @return List对象列表
     */
    public <T> T toGenericObject(String jsonString, TypeReference<T> tr) {

        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        } else {
            try {
                return objectMapper.readValue(jsonString, tr);
            } catch (Exception e) {
                //	e.printStackTrace();
                if (logger.isErrorEnabled()) {
                    logger.error("JSON串转换为Java泛型对象",e);
                }

            }
        }
        return null;
    }

    /**
     * Java对象转Json字符串
     * 本方法转换后字符串可以兼容各浏览器
     * @param object Java对象，可以是对象，数组，List,Map等
     * @return json 字符串
     */
    public String toJSONString(Object object) {
        String jsonString = "";
        try {

            jsonString = objectMapper.writeValueAsString(object);

        } catch (Exception e) {
            //e.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("Java对象转Json字符串",e);
            }
        }
        return jsonString;

    }

    /**
     * Json字符串转Java对象
     * @param jsonString json字符串
     * @param c 类
     * @return
     */
    public <T> T toObject(String jsonString, Class<?> c) {

        if (jsonString == null || jsonString.isEmpty()) {

            return (T) "";
        } else {
            try {
                return (T)objectMapper.readValue(jsonString, c);
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Json字符串转Java对象",e);
                }
            }

        }
        return (T) "";
    }

}
