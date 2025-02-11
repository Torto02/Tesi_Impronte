package main;


import rendering.*;

import rendering.Utility.FileMover;
import rendering.Utility.FilesName;
import rendering.Utility.json.JsonHelper;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class MainPistore {

	public static void main(String args[]) throws IOException, ParseException {

		String folderPath;
		if(args.length == 0) {
			Scanner in = new Scanner(System.in);

			System.out.println("Insert the relative (w.r.t. the src folder) or absolute path of the fingerprint's folder you want to analyze: ");

			folderPath = in.nextLine();
			in.close();
		}
		else{
			folderPath = args[0];
		}
		//printSingleFingerprintString(folderPath);
		printAllFingerprintString(folderPath);
		
	}
	
	public static void printSingleFingerprintString(String folderPath) throws IOException, ParseException{

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

		StringGenerator strGen = new StringGenerator(imageHeight,imageWidth);
		strGen.createStringFromInheritanceData();


		//FileMover.moveFile("FP-String.txt");
}
	
	public static void printAllFingerprintString(String folderPath1) throws IOException, ParseException{
		
		List<String> allFingerprintName = new ArrayList<>();
		FileReader f = new FileReader("/home/laura/Documenti/fingerprint4/examples2/Names.txt");
		BufferedReader b = new BufferedReader(f);
		String s;
			    
		while(true) {
		   	s = b.readLine();
		   	if(s == null)
			break;
		   	allFingerprintName.add(s);
	    }
		
		for(int i = 0; i<allFingerprintName.size(); i++) {
		String folderPath = folderPath1+allFingerprintName.get(i)+"/";
		System.out.println(folderPath);
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

		StringGenerator strGen = new StringGenerator(imageHeight,imageWidth);
		strGen.createStringFromInheritanceData();
		
		}
	}
}
