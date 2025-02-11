package rendering;

import rendering.Utility.FilesName;
import rendering.Utility.MyColorHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class GradientImages {

	public static void render(int height, int width, String filename) {
		//image dimension
		//create buffered image object img
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//file object
		File f = null;
		//create random image pixel by pixel

		//write image

		File file = new File(filename);
		FileInputStream fis = null;
		String hexString = "";
		try {
			fis = new FileInputStream(file);

			int content;
			int x = 0;
			int y = 0;
			int count = 1;
			int toMakeInvisible = 0;
			while ((content = fis.read()) != -1) {
				hexString += String.format("%02X", content);

				if (count % 8 == 0) {
					long longBits = Long.valueOf(hexString, 16);
					double doubleValue = Double.longBitsToDouble(longBits);
					Color color = MyColorHelper.numberToColor(doubleValue);
					int tmpRGB = color.getRGB();
					if (count == 8) {
						toMakeInvisible = tmpRGB;
					}
					if (tmpRGB == toMakeInvisible) {
						tmpRGB = 0;
					}
					hexString = "";
					img.setRGB(x, y, tmpRGB);

					x = x + 1;
					if (x == width) {
						x = 0;
						y = y + 1;
					}
				}

				count++;

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}


		try {
			String noExtension = filename.substring(0, filename.lastIndexOf('.'));
			f = new File(noExtension + "_img.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		String printName = filename.substring(filename.lastIndexOf('\\') + 1, filename.lastIndexOf('.'));
		System.out.println(printName + " done.");
	}

	public static void renderAll(int height, int width) throws IOException {

		int fileNumber = 2;

		String[] names = new String[fileNumber];

		names[0] = "002-decoded-image.dat";
		names[1] = "004-scaled-image.dat";
		//names[2] = "021-equalized-image.dat";
		//names[2] = "023-pixelwise-orientation.dat";
		//names[3] = "029-parallel-smoothing.dat";
		//names[4] = "030-orthogonal-smoothing.dat";


		for (int i = 0; i < fileNumber; ++i) {
			render(height, width, (FilesName.getInstance().getFolderPath() + names[i]));
		}

	}
}