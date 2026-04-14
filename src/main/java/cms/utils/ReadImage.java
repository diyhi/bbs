package cms.utils;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;


/**
 * 读取图片(解决jpg图片变红)
 *
 */
public class ReadImage {
	
	
	/**
	 * 读取图片
	 * @param filePath 图片路径
	 */
	public static BufferedImage read(String filePath){
		//解决ImageIO.read(in)读取图片可能就会导致变色
		Image src = Toolkit.getDefaultToolkit().getImage(filePath);
		BufferedImage image = toBufferedImage(src);// Image 转 BufferedImage
		//Thumbnails.of(image);
		return image;
	}
	

	
	/**
	 * Image 转 BufferedImage
	 * @param image 图像
	 * @return
	 */
	private static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}
		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		// 复制图像到图像缓冲区
		Graphics g = bimage.createGraphics();
		// 将图像绘制到图像缓冲区
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}
}
