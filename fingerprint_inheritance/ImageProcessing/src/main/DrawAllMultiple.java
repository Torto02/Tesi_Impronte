package main;

import rendering.*;
import rendering.Utility.FileMover;
import rendering.Utility.FilesName;
import rendering.Utility.Names;
import rendering.Utility.json.JsonHelper;
import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DrawAllMultiple {

	public static void main(String args[]) throws IOException, ParseException {

		Names names = new Names("/home/alessandro/Tirocinio/Prodotti da SourceAFIS/Names.txt");

		for (int i = 0; i < names.size; ++i) {

			String folderPath = "/home/alessandro/Tirocinio/Prodotti da SourceAFIS/";
			//folderPath = folderPath + "Original\\transparency_";
			//folderPath = folderPath + "Enhanced\\transparency_";
			folderPath = folderPath + "transparency_";
			folderPath = folderPath + names.names[i] + "/";

			FileUtils.deleteDirectory(new File(folderPath+"ProcessedData"));
			new File(folderPath+"ProcessedData").mkdir();

			String imageFile = folderPath + "001-decoded-image.json";
			String minutiaeFile = folderPath + "072-skeleton-minutiae.json";
			//String minutiaeFile = folderPath + "inheritance\\updated_minutiae.json";
			String edgeFile = folderPath + "inheritance//graph_c++.json";
			String edgeSCCFile = folderPath + "inheritance//scc_color.json";
			String skeletonData = folderPath + "059-valleys-thinned-skeleton.dat";
			String boundedSkeletonData = folderPath + "inheritance//059B-valleys-thinned-skeleton-bounded.txt";
			String ridgesFile = folderPath + "inheritance//ridges.json";
			String ridgesColorFile = folderPath + "inheritance//ridges_color.json";
			String trianglesFile = folderPath + "inheritance//triangles_points.json";


			FilesName.getInstance().build(folderPath, imageFile, minutiaeFile, edgeFile, edgeSCCFile, skeletonData, boundedSkeletonData, ridgesFile, ridgesColorFile, trianglesFile);

			int imageHeight = JsonHelper.getHeight();
			int imageWidth = JsonHelper.getWidth();

			//GradientImages.renderAll(imageHeight, imageWidth);

			//int black = new Color(0, 0, 0).getRGB();
			//BinaryImages.renderAll(imageHeight, imageWidth, black);

			//MinutiaeImages minutiaeDraw = new MinutiaeImages();
			//minutiaeDraw.drawMinutiae(imageHeight, imageWidth);

			SkeletonMinutiae.draw(imageHeight, imageWidth);
			//EdgesImages.drawEdges(imageHeight, imageWidth);

			SkeletonMinutiaeEdges.draw(imageHeight, imageWidth);
			
			/*BoundingBox boundingBox = new BoundingBox(imageHeight, imageWidth);
			boundingBox.makeBoundingBox();
			boundingBox.foundEdgeMinutia();
			boundingBox.foundInnerMinutia();*/
			//boundingBox.renderResult(names.names[i], folderPath);
			
			EdgeAnimation.drawEdgeAnimation(imageHeight,imageWidth,500,false, true);
			Desktop dt = Desktop.getDesktop();

			FileMover.moveImages(new File(FilesName.getInstance().getFolderPath()));

			System.out.println(names.names[i] + " done.");
		}
	}

}
