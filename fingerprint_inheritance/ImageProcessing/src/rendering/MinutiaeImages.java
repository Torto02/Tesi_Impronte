package rendering;

import rendering.Utility.FilesName;
import rendering.Utility.Minutia;
import rendering.Utility.json.MinutiaeParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MinutiaeImages {

	public static final int ENDING = 0;
	public static final int BIFURCATION = 1;

	public void drawAllMinutiae(int height, int width) throws IOException {
		int fileNumber = 7;

		String tmpName = FilesName.getInstance().getMinutiaeFile();

		String[] names = new String[fileNumber];

		names[0] = "072-skeleton-minutiae.json";
		names[1] = "073-inner-minutiae.json";
		names[2] = "074-removed-minutia-clouds.json";
		names[3] = "075-top-minutiae.json";
		names[4] = "076-shuffled-minutiae.json";
		names[5] = "300-inner-minutiae.json";
		names[6] = "301-edge-minutiae.json";

		for (int i = 0; i < names.length; ++i) {
			FilesName.getInstance().setMinutiaeFile(FilesName.getInstance().getFolderPath() + names[i]);
			if (i < 5){drawMinutiae(height, width);}
			else if (i == 5){drawMinutiaeColor(height, width,new Color(255,25,200));}
			else if (i == 6){drawMinutiaeColor(height, width,new Color(210,90,0));}
		}

		FilesName.getInstance().setMinutiaeFile(tmpName);
	}

	public void drawMinutiae(int height, int width) throws IOException {

		List<Minutia> minutiae = new ArrayList<>();
		String filename = FilesName.getInstance().getMinutiaeFile();
		minutiae = MinutiaeParser.getMinutiae();

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);


		for (int i = 0; i < minutiae.size(); i++) {
			Minutia temp = minutiae.get(i);
			this.drawMinutia(img, temp.getX(), temp.getY(), temp.getDirection(), temp.getType());
		}

		File f = null;
		try {
			f = new File(FilesName.getInstance().getMinutiaeFile() + "_img.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		//String printName = filename.substring(filename.lastIndexOf('\\') + 1, filename.lastIndexOf('.'));
		//System.out.println(printName + " done.");

	}

	public void drawMinutiaeColor(int height, int width, Color color) throws IOException {

		List<Minutia> minutiae = new ArrayList<>();
		String filename = FilesName.getInstance().getMinutiaeFile();
		minutiae = MinutiaeParser.getMinutiae();

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);


		for (int i = 0; i < minutiae.size(); i++) {
			Minutia temp = minutiae.get(i);
			this.drawMinutiaColor(img, temp.getX(), temp.getY(), temp.getDirection(), temp.getType(),color);
		}

		File f = null;
		try {
			f = new File(FilesName.getInstance().getMinutiaeFile() + "_img.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		//String printName = filename.substring(filename.lastIndexOf('\\') + 1, filename.lastIndexOf('.'));
		//System.out.println(printName + " done.");

	}

	public void drawMinutiae(BufferedImage img, boolean edges) throws IOException {

		List<Minutia> minutiae = new ArrayList<>();
		//String filename = FilesName.getInstance().getMinutiaeFile();
		minutiae = MinutiaeParser.getMinutiae();

		for (int i = 0; i < minutiae.size(); i++) {
			Minutia temp = minutiae.get(i);
			this.drawMinutia(img, temp.getX(), temp.getY(), temp.getDirection(), temp.getType());
		}

		File f = null;
		try {
			if(!edges)
				f = new File(FilesName.getInstance().getFolderPath() + "skeleton_and_minutiae_img.png");
			else
				f = new File(FilesName.getInstance().getFolderPath() + "skeleton_and_minutiae_and_edges_img.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		//String printName = filename.substring(filename.lastIndexOf("transparency_") + 13, filename.lastIndexOf('\\'));
		//System.out.println(printName + " done.");

	}

	public void drawMinutiaeBB(BufferedImage img, boolean edges) throws IOException {

		String tmpName = FilesName.getInstance().getMinutiaeFile();

		int fileNumber = 2;
		String[] names = new String[fileNumber];

		names[0] = "inheritance//edge_minutiae.json";
		//names[1] = "301-edge-minutiae.json";
		names[1] = "inheritance//internal_minutiae.json";
		//names[1] = "303-render-minutiae.json";

		Color color;
		List<Minutia> minutiae;
		for (int i = 0; i < names.length; ++i) {
			if (i == 0){
				color = new Color(255,25,200);
			}
			else {
				color = new Color(210,90,0);
			}
			FilesName.getInstance().setMinutiaeFile(FilesName.getInstance().getFolderPath() + names[i]);
			minutiae = MinutiaeParser.getMinutiae();

			for (int m = 0; m < minutiae.size(); m++) {
				Minutia temp = minutiae.get(m);
				this.drawMinutiaColor(img, temp.getX(), temp.getY(), temp.getDirection(), temp.getType(), color);
			}


		}

		File f = null;
		try {
			if(!edges)
				f = new File(FilesName.getInstance().getFolderPath() + "skeleton_and_minutiaeBB_img.png");
			else
				f = new File(FilesName.getInstance().getFolderPath() + "skeleton_and_minutiaeBB_and_edges_img.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		FilesName.getInstance().setMinutiaeFile(tmpName);

	}

	private void drawMinutia(BufferedImage img, int x, int y, double direction, int type) {

		Color color;

		if (type == BIFURCATION) {
			color = new Color(0, 204, 0);
		} else {
			color = new Color(0, 0, 204);
		}

		//color = new Color(204,0,0);

		drawCircle(x, y, 8, color, img);
		drawLine(x, y, direction, color, img);

	}

	private void drawMinutiaColor(BufferedImage img, int x, int y, double direction, int type, Color color) {
		drawCircle(x, y, 8, color, img);
		drawLine(x, y, direction, color, img);
	}

	private void drawCircle(int x, int y, int r, Color color, BufferedImage img) {
		Graphics graphics = img.getGraphics();
		graphics.setColor(color);
		drawCenteredCircle(graphics, x, y, r);
	}

	private void drawCenteredCircle(Graphics g, int x, int y, int r) {
		x = x - (r / 2);
		y = y - (r / 2);
		g.drawOval(x, y, r, r);
	}

	private void drawLine(int x, int y, double angle, Color color, BufferedImage img) {
		int length = 5;
		//angle = angle - Math.toRadians(90);
		x = (int) (x + 4 * Math.cos(angle));
		y = (int) (y + 4 * Math.sin(angle));
		int x2 = (int) (x + length * Math.cos(angle));
		int y2 = (int) (y + length * Math.sin(angle));
		Graphics graphics = img.getGraphics();
		graphics.setColor(color);
		graphics.drawLine(x, y, x2, y2);
	}
}
