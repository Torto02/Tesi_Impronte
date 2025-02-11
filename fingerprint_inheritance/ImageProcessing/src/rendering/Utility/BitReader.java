package rendering.Utility;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class BitReader {


	public static void main(String args[]) throws IOException {
		//image dimension
		int width = 480;
		int height = 504;
		//create buffered image object img
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//file object
		File f = null;
		//create random image pixel by pixel

		//write image

		String FILENAME = "C:\\Users\\franc\\Desktop\\transparency3\\004-scaled-image.dat";

		FileInputStream fi = new FileInputStream(FILENAME); // I assume this is different file
		StringBuffer buf = new StringBuffer();
		int b = fi.read();
		int counter = 0;
		ByteArrayOutputStream dataBuf = new ByteArrayOutputStream();
		//while (b != -1) {
		int c = 0;
		while (c < 64) {
			buf.append((int) b);
			b = fi.read();
			counter++;
			if (counter % 8 == 0) {
				int i = Integer.parseInt(buf.toString(), 2);
				byte bi = (byte) (i & 0xff);
				dataBuf.write(bi);
				buf.delete(0, buf.length());
			}
			c++;
		}
		byte data[] = dataBuf.toByteArray();
		System.out.println(Arrays.toString(data));
	}
}
