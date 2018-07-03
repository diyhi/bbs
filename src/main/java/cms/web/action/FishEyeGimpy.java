package cms.web.action;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.google.code.kaptcha.GimpyEngine;


/**
 * kaptcha验证码鱼眼效果(未用上)
 *
 */
public class FishEyeGimpy implements GimpyEngine{
	
	/**
	<!-- Kaptcha验证码 -->
	   <bean id="captchaProducer" class="com.google.code.kaptcha.impl.DefaultKaptcha"> 
	        <property name="config"> 
	            <bean class="com.google.code.kaptcha.util.Config"> 
	                <constructor-arg> 
	                    <props> 
	                        <prop key="kaptcha.border">no</prop> <!-- 是否有边框 默认为true 我们可以自己设置yes，no -->  
	                        <prop key="kaptcha.border.color">105,179,90</prop> <!-- 边框颜色 默认为Color.BLACKkaptcha.border.thickness 边框粗细度 默认为1 -->
	                        <prop key="kaptcha.textproducer.font.color">80,80,80</prop> <!-- 验证码文本字符颜色 默认为Color.BLACK -->
	                        <prop key="kaptcha.image.width">120</prop> <!-- 宽度默认为200 -->
	                        <prop key="kaptcha.image.height">40</prop> <!-- 验证码图片高度 默认为50 -->
	                        <prop key="kaptcha.textproducer.char.space">8</prop> <!-- 验证码文本字符间距 默认为2 -->
	                        <prop key="kaptcha.textproducer.font.size">34</prop> <!-- 验证码文本字符大小 默认为40 -->
	                        <prop key="kaptcha.noise.color">225,225,225</prop> <!-- 验证码噪点颜色 默认为Color.BLACK black -->
	                        <prop key="kaptcha.word.impl">cms.web.action.DefaultWordRenderer</prop><!-- 验证码文本字符渲染-->
	                        <prop key="kaptcha.obscurificator.impl">cms.web.action.FishEyeGimpy</prop><!-- 图片样式  cms.web.action.FishEyeGimpy鱼眼效果-->
	                       	<prop key="kaptcha.background.clear.from">225,225,225</prop><!-- 背景颜色渐变，开始颜色-->
	                       	<prop key="kaptcha.background.clear.to">225,225,225</prop><!-- 背景颜色渐变，结束颜色-->
	                       <!--  <prop key="kaptcha.session.key">code</prop> 放入session的KEY名称 -->
	                        <prop key="kaptcha.textproducer.char.length">5</prop> <!-- 验证码文本字符长度 默认为5 -->
	                        <prop key="kaptcha.textproducer.font.names">宋体,楷体,微软雅黑</prop> <!-- 验证码文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)-->
	                    </props> 
	                </constructor-arg> 
	            </bean> 
	        </property> 
	    </bean>
	**/
	
	
	
	
	
	
	
	/**
	 * Applies distortion by adding fish eye effect and horizontal vertical
	 * lines.
	 * 
	 * @param baseImage the base image
	 * @return the distorted image
	 */
	public BufferedImage getDistortedImage(BufferedImage baseImage)
	{

		Graphics2D graph = (Graphics2D) baseImage.getGraphics();
		int imageHeight = baseImage.getHeight();
		int imageWidth = baseImage.getWidth();

		// want lines put them in a variable so we might configure these later
		int horizontalLines = imageHeight / 7;
		int verticalLines = imageWidth / 7;

		// 计算间距
		int horizontalGaps = imageHeight / (horizontalLines + 1);
		int verticalGaps = imageWidth / (verticalLines + 1);

		// 绘制的横条纹
		for (int i = horizontalGaps; i < imageHeight; i = i + horizontalGaps)
		{
			//graph.setColor(Color.blue);
		//	graph.setColor(new Color(163,163,163));
			graph.setColor(new Color(80,80,80));
			graph.drawLine(0, i, imageWidth, i);

		}

		// 绘制的竖条纹
		for (int i = verticalGaps; i < imageWidth; i = i + verticalGaps){
			//如果是中间则不画线
		//	if(i == imageWidth/2){
		//		continue;
		//	}
		//	graph.setColor(Color.red);
		//	graph.setColor(new Color(163,163,163));
			graph.setColor(new Color(80,80,80));
			graph.drawLine(i, 0, i, imageHeight);
			
			

		}

		// 创建的原始图像的像素阵列
		// we need this later to do the operations on..
		int pix[] = new int[imageHeight * imageWidth];
		int j = 0;

		for (int j1 = 0; j1 < imageWidth; j1++)
		{
			for (int k1 = 0; k1 < imageHeight; k1++)
			{
				pix[j] = baseImage.getRGB(j1, k1);
				j++;
			}

		}

		double distance = ranInt(imageWidth / 4, imageWidth / 3);

		// put the distortion in the (dead) middle
		int widthMiddle = baseImage.getWidth() / 2;
		int heightMiddle = baseImage.getHeight() / 2;

		// 再次遍历所有的像素
		for (int x = 0; x < baseImage.getWidth(); x++)
		{
			for (int y = 0; y < baseImage.getHeight(); y++)
			{

				int relX = x - widthMiddle;
				int relY = y - heightMiddle;

				double d1 = Math.sqrt(relX * relX + relY * relY);
				if (d1 < distance)
				{

					int j2 = widthMiddle
							+ (int) (((fishEyeFormula(d1 / distance) * distance) / d1) * (double) (x - widthMiddle));
					int k2 = heightMiddle
							+ (int) (((fishEyeFormula(d1 / distance) * distance) / d1) * (double) (y - heightMiddle));
					baseImage.setRGB(x, y, pix[j2 * imageHeight + k2]);
				}
			}

		}

		return baseImage;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	private int ranInt(int i, int j)
	{
		double d = Math.random();
		return (int) ((double) i + (double) ((j - i) + 1) * d);
	}

	/**
	 * implementation of: g(s) = - (3/4)s3 + (3/2)s2 + (1/4)s, with s from 0 to
	 * 1
	 * 
	 * @param s
	 * @return
	 */
	private double fishEyeFormula(double s)
	{
		if (s < 0.0D)
			return 0.0D;
		if (s > 1.0D)
			return s;
		else
			return -0.75D * s * s * s + 1.5D * s * s + 0.25D * s;
	}
}
