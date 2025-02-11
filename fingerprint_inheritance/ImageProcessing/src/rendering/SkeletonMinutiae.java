package rendering;

import rendering.Utility.FilesName;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SkeletonMinutiae {

	public static void draw(int imageHeight, int imageWidth) throws IOException {

		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int black = new Color(0, 0, 0).getRGB();
		BinaryImages.render(imageWidth, FilesName.getInstance().getSkeletonData(), black, img);

		MinutiaeImages minutiaeDraw = new MinutiaeImages();
		minutiaeDraw.drawMinutiae(img,false);
	}

	public static void drawBBMinutiae(int imageHeight, int imageWidth) throws IOException {

		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int black = new Color(0, 0, 0).getRGB();
		BinaryImages.renderTxt(imageWidth, FilesName.getInstance().getBoundedSkeletonData(), black, img);

		MinutiaeImages minutiaeDraw = new MinutiaeImages();
		minutiaeDraw.drawMinutiaeBB(img,false);




	}
}