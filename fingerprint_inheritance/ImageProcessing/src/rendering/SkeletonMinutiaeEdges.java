package rendering;

import rendering.Utility.FilesName;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SkeletonMinutiaeEdges {

	public static void draw(int imageHeight, int imageWidth) throws IOException {
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int black = new Color(0, 0, 0).getRGB();
		BinaryImages.render(imageWidth, FilesName.getInstance().getSkeletonData(), black, img);

		EdgesImages.drawEdges(img);

		MinutiaeImages minutiaeDraw = new MinutiaeImages();
		minutiaeDraw.drawMinutiae(img, true);


	}

	public static void drawBB(int imageHeight, int imageWidth) throws IOException {
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int black = new Color(0, 0, 0).getRGB();
		BinaryImages.renderTxt(imageWidth, FilesName.getInstance().getBoundedSkeletonData(), black, img);

		EdgesImages.drawEdges(img);

		MinutiaeImages minutiaeDraw = new MinutiaeImages();
		minutiaeDraw.drawMinutiaeBB(img, true);


	}

	public static void drawSCC(int imageHeight, int imageWidth) throws IOException {
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int black = new Color(0, 0, 0).getRGB();
		BinaryImages.renderTxt(imageWidth, FilesName.getInstance().getBoundedSkeletonData(), black, img);

		EdgesImages.drawColoredEdges(img);

		MinutiaeImages minutiaeDraw = new MinutiaeImages();
		minutiaeDraw.drawMinutiaeBB(img, true);
	}

	public static void drawRidges(int imageHeight, int imageWidth) throws IOException {
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int black = new Color(0, 0, 0).getRGB();
		BinaryImages.renderTxt(imageWidth, FilesName.getInstance().getBoundedSkeletonData(), black, img);

		RidgesImage.drawColoredRidges(img);

		MinutiaeImages minutiaeDraw = new MinutiaeImages();
		minutiaeDraw.drawMinutiaeBB(img, true);


	}

	public static void drawAllTriangles(int imageHeight, int imageWidth) throws IOException {
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int black = new Color(0, 0, 0).getRGB();
		BinaryImages.renderTxt(imageWidth, FilesName.getInstance().getBoundedSkeletonData(), black, img);

		RidgesImage.drawTriangles(img);

		MinutiaeImages minutiaeDraw = new MinutiaeImages();
		minutiaeDraw.drawMinutiaeBB(img, true);


	}

}
