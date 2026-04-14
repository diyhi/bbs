package cms.controller.frontend;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * favicon.ico图标
 *
 */
@Controller
public class FaviconController {
	
	@RequestMapping(value={"/favicon.ico","/apple-touch-icon.png","/apple-touch-icon-120x120.png"})
	public ResponseEntity<byte[]> favicon(HttpServletRequest request, HttpServletResponse response
			)throws Exception {

        // 从 classpath中加载文件
        ClassPathResource faviconResource = new ClassPathResource("static/favicon.ico");

        // 检查文件是否存在，如果不存在则返回 404 Not Found
        if (!faviconResource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 读取文件的所有字节
        byte[] iconBytes = faviconResource.getInputStream().readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        // 设置媒体类型
        headers.setContentType(MediaType.valueOf("image/x-icon"));
        headers.setContentLength(iconBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(iconBytes);
	}
}
