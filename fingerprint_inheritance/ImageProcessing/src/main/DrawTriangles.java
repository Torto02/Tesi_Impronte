package main;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import rendering.*;
import rendering.Utility.FileMover;
import rendering.Utility.FilesName;
import rendering.Utility.json.JsonHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class DrawTriangles {

    public static void main(String args[]) throws IOException, ParseException {


        String folderPath;
        if(args.length == 0) {
            Scanner in = new Scanner(System.in);

            System.out.println("Insert the relative (w.r.t. the src folder) or absolute path of the fingerprint's folder you want to analyze: ");

            folderPath = in.nextLine();
        }
        else{
            folderPath = args[0];
        }

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


        SkeletonMinutiaeEdges.drawAllTriangles(imageHeight, imageWidth);


        FileMover.moveImages(new File(FilesName.getInstance().getFolderPath()));
        //dt.open(new File(FilesName.getInstance().getFolderPath() + "ProcessedData\\edges_animation.gif"));

    }
}
