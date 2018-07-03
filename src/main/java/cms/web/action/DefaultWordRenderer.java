package cms.web.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.google.code.kaptcha.text.WordRenderer;
import com.google.code.kaptcha.util.Configurable;


/**
 * kaptcha验证码文本字符渲染
 *
 */
public class DefaultWordRenderer extends Configurable implements WordRenderer{

	
	/**
	 * Renders a word to an image.
	 * 
	 * @param word 待渲染的字符
	 * @param width 图像的宽度
	 * @param height 图像的高度
	 * @return The BufferedImage created from the word.
	 */
	@Override
	public BufferedImage renderWord(String word, int width, int height){

		int fontSize = getConfig().getTextProducerFontSize(); 
		
		
		/**
		//验证码文本字符样式默认为粗体,修改为普通字体 
        String paramName = "kaptcha.textproducer.font.names";  
        String paramValue = (String)getConfig().getProperties().get(paramName);  
        String fontNames[] = paramValue.split(",");  
        Font fonts[] = new Font[fontNames.length];  
        for(int i = 0; i < fontNames.length; i++){  
            fonts[i] = new Font(fontNames[i], Font.ITALIC, fontSize);  
        } **/ 

		//验证码文本字符样式默认为粗体
		Font[] fonts = getConfig().getTextProducerFonts(fontSize);
        
        
		Color color = getConfig().getTextProducerFontColor();
		int charSpace = getConfig().getTextProducerCharSpace();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2D = image.createGraphics();
		g2D.setColor(color);

		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g2D.setRenderingHints(hints);

		
		
		FontRenderContext frc = g2D.getFontRenderContext();
		Random random = new Random();

		int startPosY = (height - fontSize) / 5 + fontSize-8;
	
		char[] wordChars = word.toCharArray();
		Font[] chosenFonts = new Font[wordChars.length];
		int [] charWidths = new int[wordChars.length];
		int widthNeeded = 0;
		for (int i = 0; i < wordChars.length; i++)
		{
			chosenFonts[i] = fonts[random.nextInt(fonts.length)];

			char[] charToDraw = new char[]{
				wordChars[i]
			};
			GlyphVector gv = chosenFonts[i].createGlyphVector(frc, charToDraw);
			charWidths[i] = (int)gv.getVisualBounds().getWidth();
			if (i > 0)
			{
				widthNeeded = widthNeeded + 2;
			}
			widthNeeded = widthNeeded + charWidths[i];
		}
		
		int startPosX = (width - widthNeeded) / 2;
		for (int i = 0; i < wordChars.length; i++)
		{
			g2D.setFont(chosenFonts[i]);
			char[] charToDraw = new char[] {
				wordChars[i]
			};
			g2D.drawChars(charToDraw, 0, charToDraw.length, startPosX, startPosY);
			startPosX = startPosX + (int) charWidths[i] + charSpace;
		}
		
/**
		//扭曲字体
		Random rand = new Random();

		RippleFilter rippleFilter = new RippleFilter();
		rippleFilter.setWaveType(RippleFilter.SINE);
		rippleFilter.setXAmplitude(7.6f);
		rippleFilter.setYAmplitude(rand.nextFloat() + 1.0f);
		rippleFilter.setXWavelength(rand.nextInt(7) + 8);
		rippleFilter.setYWavelength(rand.nextInt(3) + 2);
		rippleFilter.setEdgeAction(TransformFilter.BILINEAR);


		BufferedImage effectImage = rippleFilter.filter(image, null);

		g2D.drawImage(effectImage, 0, 0, null, null);
		g2D.dispose();
		return effectImage;**/
		
		return image;
	}

}
