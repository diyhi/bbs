package cms.config;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cms.utils.PathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 验让码配置
 * @author Gao
 *
 */
@Configuration
public class KaptchaConfig {
    private static final Logger logger = LogManager.getLogger(KaptchaConfig.class);

    /**
     * 是否有边框 默认为true 我们可以自己设置yes，no
     */
    @Value("${kaptcha.border}")
    private String border;

    /**
     * 边框颜色 默认为Color.BLACKkaptcha.border.thickness 边框粗细度 默认为1
     */
    @Value("${kaptcha.border.color}")
    private String borderColor;

    /**
     * 验证码文本字符颜色 默认为Color.BLACK
     */
    @Value("${kaptcha.textproducer.font.color}")
    private String textproducerFontColor;

    /**
     * 图片宽度 默认为200
     */
    @Value("${kaptcha.image.width}")
    private String imageWidth;

    /**
     * 图片高度 默认为50
     */
    @Value("${kaptcha.image.height}")
    private String imageHeight;

    /**
     * 文本字符间距 默认为2
     */
    @Value("${kaptcha.textproducer.char.space}")
    private String textproducerCharSpace;

    /**
     * 文本字符大小 默认为40
     */
    @Value("${kaptcha.textproducer.font.size}")
    private String textproducerFontSize;

    /**
     * 噪点颜色 默认为Color.BLACK black
     */
    @Value("${kaptcha.noise.color}")
    private String noiseColor;

    /**
     * 文本字符渲染
     */
    @Value("${kaptcha.word.impl}")
    private String wordImpl;

    /**
     * 图片样式  com.google.code.kaptcha.impl.WaterRipple水纹     com.google.code.kaptcha.impl.ShadowGimpy阴影效果   cms.web.action.FishEyeGimpy鱼眼效果
     */
    @Value("${kaptcha.obscurificator.impl}")
    private String obscurificatorImpl;

    /**
     * 背景颜色渐变，开始颜色
     */
    @Value("${kaptcha.background.clear.from}")
    private String backgroundClearFrom;

    /**
     * 背景颜色渐变，结束颜色
     */
    @Value("${kaptcha.background.clear.to}")
    private String backgroundClearTo;

    /**
     * 放入session的KEY名称
     */
    @Value("${kaptcha.session.key}")
    private String sessionKey;

    /**
     * 验证码文本字符长度 默认为5
     */
    @Value("${kaptcha.textproducer.char.length}")
    private String textproducerCharLength;

    /**
     * 文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
     */
    @Value("${kaptcha.textproducer.font.names}")
    private String textproducerFontNames;


    /**
     * 配置 Kapcha 参数
     * @return
     */
    @Bean
    public DefaultKaptcha getDefaultKapcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", border);
        properties.setProperty("kaptcha.border.color", borderColor);
        properties.setProperty("kaptcha.textproducer.font.color", textproducerFontColor);
        properties.setProperty("kaptcha.image.width", imageWidth);
        properties.setProperty("kaptcha.image.height", imageHeight);
        properties.setProperty("kaptcha.textproducer.char.space", textproducerCharSpace);
        properties.setProperty("kaptcha.textproducer.font.size", textproducerFontSize);
        properties.setProperty("kaptcha.noise.color", noiseColor);
        properties.setProperty("kaptcha.word.impl", wordImpl);
        properties.setProperty("kaptcha.obscurificator.impl", obscurificatorImpl);
        properties.setProperty("kaptcha.background.clear.from", backgroundClearFrom);
        properties.setProperty("kaptcha.background.clear.to", backgroundClearTo);
        properties.setProperty("kaptcha.session.key", sessionKey);
        properties.setProperty("kaptcha.textproducer.char.length", textproducerCharLength);
        properties.setProperty("kaptcha.textproducer.font.names", textproducerFontNames);



        Config config = new Config(properties,false);
        defaultKaptcha.setConfig(config);
        if(PathUtil.isStartupFromJar()){//jar启动
            this.production_registerFont();
        }else{//IDE启动
            this.development_registerFont();
        }
        return defaultKaptcha;
    }

    /**
     * 注册字体(开发环境)
     */
    private void development_registerFont() {
        // JAR 文件的路径
        String rootPath = Config.class.getProtectionDomain().getCodeSource().getLocation().getPath();//本行代码在开发环境返回/D:/JAVA/bbs-pro-jdk21/lib/kaptcha-2.3.3.jar      服务器环境返回nested:/D:/bbs-pro-jdk21/bbs-pro-jdk21-v7.0.jar/!BOOT-INF/lib/kaptcha-2.3.3.jar!/
        try (JarFile rootJar = new JarFile(new File(rootPath))) {
            Enumeration<JarEntry> entries = rootJar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.startsWith("font/") && (name.endsWith(".ttf") || name.endsWith(".otf"))) {
                    try (InputStream in = rootJar.getInputStream(entry)) {
                        Font font = Font.createFont(Font.TRUETYPE_FONT, in);
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        ge.registerFont(font);
                        //System.out.println("注册字体: " + name);
                    } catch (FontFormatException e) {
                        if (logger.isErrorEnabled()) {
                            logger.error("注册字体",e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("读取资源",e);
            }
        }
    }
    /**
     * 注册字体(生产环境)
     */
    private void production_registerFont() {
        // 使用 Spring 提供的资源路径匹配解析器
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            // 扫描 classpath 下 font 目录及其子目录下所有的 ttf 和 otf 文件
            Resource[] resources = resolver.getResources( "classpath*:/**/font/*.{ttf,otf}");

            for (Resource resource : resources) {
                String filename = resource.getFilename();

                if (resource.getDescription().contains("kaptcha") && filename != null && (filename.toLowerCase().endsWith(".ttf") || filename.toLowerCase().endsWith(".otf"))) {
                    // 使用 try-with-resources 自动关闭流
                    try (InputStream in = resource.getInputStream()) {
                        Font font = Font.createFont(Font.TRUETYPE_FONT, in);
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        ge.registerFont(font);

                        // if (logger.isInfoEnabled()) {
                        //     logger.info("字体注册成功: {}", resource.getFilename());
                        // }
                    } catch (FontFormatException e) {
                        logger.error("字体格式非法: {}", resource.getFilename(), e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("从类路径读取字体资源失败", e);
        }

    }


}

