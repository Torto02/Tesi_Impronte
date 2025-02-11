package rendering;

import rendering.Utility.FilesName;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;

public class BinaryImages {

	public static void render(int height, int width, String filename, int color) throws IOException {

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		File file = new File(filename);
		FileInputStream fis = null;
		try {

			fis = new FileInputStream(file);

			int content;
			int x = 0;
			int y = 0;
			int p;
			while ((content = fis.read()) != -1) {

				if (content != 0) {
					p = color;
				} else {
					p = 0;
				}

				//System.out.println(x + " " + y);
				img.setRGB(x, y, p);

				x = x + 1;
				if (x == width) {
					x = 0;
					y = y + 1;
				}


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

		File f = null;

		try {
			String noExtension = filename.substring(0, filename.lastIndexOf('.'));
			f = new File(noExtension + "_img.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		String printName = filename.substring(filename.lastIndexOf('\\') + 1, filename.lastIndexOf('.'));
		//System.out.println(printName + " done.");

	}

	public static void render(int width, String filename, int color, BufferedImage img) throws IOException {

		//BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		File file = new File(filename);
		FileInputStream fis = null;
		try {

			fis = new FileInputStream(file);

			int content;
			int x = 0;
			int y = 0;
			int p;
			while ((content = fis.read()) != -1) {

				if (content != 0) {
					p = color;
				} else {
					p = 0;
				}

				//System.out.println(x + " " + y);
				img.setRGB(x, y, p);

				x = x + 1;
				if (x == width) {
					x = 0;
					y = y + 1;
				}

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
	}


	public static void renderTxt(int height, int width, String filename, int color) throws IOException {


		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		File file = new File(filename);
		InputStream fis = null;
		try {

			fis = new FileInputStream(file);
			Reader reader = new InputStreamReader(fis, Charset.forName("UTF-8"));

			int content;
			char ch_content;
			int x = 0;
			int y = 0;
			int p;
			while ((content = fis.read()) != -1) {

				ch_content = (char) content;

				if (ch_content != '0') {
					p = color;
				} else {
					p = 0;
				}

				//System.out.println(x + " " + y);
				img.setRGB(x, y, p);

				x = x + 1;
				if (x == width) {
					x = 0;
					y = y + 1;
				}

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

		File f = null;

		try {
			String noExtension = filename.substring(0, filename.lastIndexOf('.'));
			f = new File(noExtension + "_img.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		String printName = filename.substring(filename.lastIndexOf('\\') + 1, filename.lastIndexOf('.'));
		//System.out.println(printName + " done.");

	}

	public static void renderTxt(int width, String filename, int color, BufferedImage img) throws IOException {


		//BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		File file = new File(filename);
		InputStream fis = null;
		try {

			fis = new FileInputStream(file);
			Reader reader = new InputStreamReader(fis, Charset.forName("UTF-8"));

			int content;
			char ch_content;
			int x = 0;
			int y = 0;
			int p;
			while ((content = fis.read()) != -1) {

				ch_content = (char) content;

				if (ch_content != '0') {
					p = color;
				} else {
					p = 0;
				}

				//System.out.println(x + " " + y);
				img.setRGB(x, y, p);

				x = x + 1;
				if (x == width) {
					x = 0;
					y = y + 1;
				}

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
	}

	public static void renderAll(int height, int width, int color) throws IOException {

		int fileNumber = 9;

		String[] names = new String[fileNumber];

		names[0] = "033-binarized-image.dat";
		names[1] = "035-filtered-binary-image.dat";
		names[2] = "037-pixel-mask.dat";
		names[3] = "039-inner-mask.dat";
		names[4] = "041-ridges-binarized-skeleton.dat";
		names[5] = "043-ridges-thinned-skeleton.dat";
		names[6] = "057-valleys-binarized-skeleton.dat";
		names[7] = "059-valleys-thinned-skeleton.dat";
		names[8] = "inheritance\\059B-valleys-thinned-skeleton-bounded.txt";


		for (int i = 0; i < fileNumber; ++i) {
			if (i == 8) {
				renderTxt(height, width, (FilesName.getInstance().getFolderPath() + names[i]), color);
			}
			else{
				render(height, width, (FilesName.getInstance().getFolderPath() + names[i]), color);
			}
		}
	}

}