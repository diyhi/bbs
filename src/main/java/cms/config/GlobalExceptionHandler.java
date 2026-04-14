package cms.config;

import cms.annotation.DynamicRouteTarget;
import cms.dto.ClientRequestResult;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常
 * 和cms.config.BusinessException一起使用
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * 捕获并处理自定义的BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)//HTTP 状态码200
    public Object handleBusinessException(BusinessException ex, HandlerMethod handlerMethod) {
        // 从异常中获取错误信息
        Map<String, String> errors = ex.getErrors();

        // 检查方法或类上是否有 @DynamicRouteTarget 注解
        boolean isClient = handlerMethod.hasMethodAnnotation(DynamicRouteTarget.class)
                || handlerMethod.getBeanType().isAnnotationPresent(DynamicRouteTarget.class);

        if(isClient){//前台抛出错误  例如throw new BusinessException(Map.of("id", "Id不存在"));
            ClientRequestResult result = ClientRequestResult.fail(ex.getErrors());

            // 自动搬运异常里的 extraData 到ClientRequestResult对象的 extraData 中
            if (ex.getExtraData() != null) {
                ex.getExtraData().forEach(result::add);
            }
            return result;//{"success": false, "error": {...}}
            //return new ClientRequestResult(false, ex.getErrors());//{"success": false, "error": {...}}
        }

        //管理后台抛出错误
        // 返回统一的失败结果
        return new RequestResult(ResultCode.FAILURE, errors);//{"code": 500, "message": "失败", "data": {...}}
    }

    /**
     * 捕获并处理所有未处理的通用异常
     *
     * @param ex 异常对象
     * @return 统一的错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//HTTP 状态码 500错误
    public ResponseEntity<RequestResult> handleGenericException(Exception ex) {

        // 检查是否为客户端主动断开连接 (Connection reset by peer / Broken pipe)
        // 包含 Tomcat 的 ClientAbortException
        if (isClientAbortException(ex)) {
            logger.warn("客户端主动断开连接，无需处理 (原因: {})", ex.getMessage());
            // 直接返回 null 或空的 body，避免 Spring 尝试用 JSON 转换器去写响应体
            // 此时连接已断开，写任何内容都会触发第二次异常
            return null;
        }


        // 记录异常日志
        logger.error("捕获的全局异常", ex);
        // 返回统一的错误响应给客户端
        Map<String, String> error = Map.of("exception", ex.getMessage());

        return new ResponseEntity<>(new RequestResult(ResultCode.FAILURE, error), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理403错误
     * @param e
     * @return
     */
    @ExceptionHandler(CustomAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 强制返回 403
    public Object handle403(CustomAccessDeniedException e) {
        return "";
    }

    /**
     * Web 应用找不到请求的静态资源
     * @param ex 异常对象
     * @return
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//HTTP 状态码404
    public void handleNoResourceFoundException(NoResourceFoundException ex) {

    }


    /**
     * 访问不存在的链接时报错 org.springframework.web.servlet.NoHandlerFoundException: No endpoint GET /control/help/list
     * @param ex
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 强制返回 404 状态码
    public ResponseEntity<RequestResult> handleNoHandlerFound(NoHandlerFoundException ex) {
        // 返回统一的错误响应给客户端
        Map<String, String> error = Map.of("exception", ex.getMessage());
        return new ResponseEntity<>(new RequestResult(ResultCode.FAILURE, error), HttpStatus.NOT_FOUND);
    }


    /**
     * 专门处理连接中断异常，避免日志污染

    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("Broken pipe")) {
            // 这是客户端主动断开连接，无需记录为错误，因为不是服务器问题
            logger.warn("客户端已关闭连接。发生“Broken pipe”异常");
        } else {
            logger.error("IO 异常", ex);
        }
    }*/

    /**
     * 判断是否为客户端主动中断
     */
    private boolean isClientAbortException(Throwable ex) {
        if (ex == null) return false;
        String message = ex.getMessage();
        // 检查异常类型或消息内容
        if (ex instanceof IOException || "org.apache.catalina.connector.ClientAbortException".equals(ex.getClass().getName())) {
            return message != null && (message.contains("Broken pipe") || message.contains("Connection reset by peer"));
        }
        // 递归检查 Cause
        return isClientAbortException(ex.getCause());
    }
}
