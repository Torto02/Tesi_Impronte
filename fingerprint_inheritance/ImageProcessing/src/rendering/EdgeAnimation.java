package rendering;

import rendering.Utility.FilesName;
import rendering.Utility.GifSequenceWriter;
import rendering.Utility.json.EdgeParser;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EdgeAnimation {

	private static void drawSingleEdge(BufferedImage img, int index) throws IOException {
		String folderPath = FilesName.getInstance().getFolderPath();
		new File(folderPath+"\\Animation").mkdir();
		EdgesImages.drawEdge(img, EdgeParser.getEdge(FilesName.getInstance().getEdgesFile(), index));
		File f = new File(folderPath + "\\Animation\\edge_" + index +".png");
		ImageIO.write(img, "png", f);
	}

	public static void drawEdgeAnimation(int imageHeight, int imageWidth, int time, boolean loop, boolean delete) throws IOException {
		String folderPath = FilesName.getInstance().getFolderPath();
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int black = new Color(0, 0, 0).getRGB();
		BinaryImages.render(imageWidth, FilesName.getInstance().getSkeletonData(), black, img);

		MinutiaeImages minutiaeDraw = new MinutiaeImages();
		minutiaeDraw.drawMinutiae(img, false);

		ImageOutputStream output =
				new FileImageOutputStream(new File(folderPath + "edges_animation.gif"));

		// create a gif sequence with the type of the first image, 1 second
		// between frames, which loops continuously
		GifSequenceWriter writer =
				new GifSequenceWriter(output, img.getType(), time, loop);

		for (int i = 0; i < EdgeParser.edgesCount(FilesName.getInstance().getEdgesFile()); ++i) {
			//BufferedImage img_temp = img;
			drawSingleEdge(img, i);


			//BufferedImage firstImage = ImageIO.read(new File(args[0]));

			// create a new BufferedOutputStream with the last argument


			// write out the first image to our sequence...
			writer.writeToSequence(img);
		}
		writer.close();
		output.close();

		if(delete)
			FileUtils.deleteDirectory(new File(folderPath+"\\Animation"));
	}

}