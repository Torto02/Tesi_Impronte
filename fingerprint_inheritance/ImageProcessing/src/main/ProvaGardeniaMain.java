package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import rendering.Utility.Gardenia;
import rendering.Utility.Operation;

public class ProvaGardeniaMain {
	
	public static void main(String args[]){


		String path;
		String file1;
		String file2;

		if(args.length == 0) {

			path = "/home/laura/Documenti/fingerprint4/examples2/";


			Scanner in = new Scanner(System.in);

			System.out.println("Insert the name of the relative or absolute path of the folder that contains the FP folders ");
			path = in.nextLine();

			System.out.println("Insert the name of the first fingerprint folder");

			file1 = in.nextLine();
			System.out.println("Insert the name of the second fingerprint folder");

			file2 = in.nextLine();
			in.close();
		}
		else{
			path = args[0];
			file1 = args[1];
			file2 = args[1];
		}

		printOperationsMinutiaOnFile(path, file1, file2);
		//printScoreTwoSequencesProve();
		//printHeatFile();
	}
	
	
	public static void checkAllSequences() {
		
		
		try {
			List<String> allFingerprintName = new ArrayList<>();
			FileReader f;
			f = new FileReader("/home/laura/Documenti/fingerprint2/examples2/Names.txt");
			BufferedReader b = new BufferedReader(f);
			String s;
				    
			while(true) {
			   	s = b.readLine();
			   	if(s == null)
				break;
			   	allFingerprintName.add(s);
		    }
			
			List<String> allFingerprintString = new ArrayList<>();
			int sizeMatrix = allFingerprintName.size();
			
			
			String string;
			for(int i=0; i<sizeMatrix; i++) {
				FileReader fString = new FileReader("/home/laura/Documenti/fingerprint2/examples2/" + allFingerprintName.get(i) + "/allFingerprintString.txt");
				BufferedReader bString = new BufferedReader(fString);
				string = bString.readLine();
			   	allFingerprintString.add(string);
			}
			
			int mySize=allFingerprintString.size();
			
			for (int i = 0; i<mySize; i++) {
				
				checkSequence(allFingerprintString.get(i), allFingerprintName.get(i));
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void checkSequence(String sequence, String name) {
		int arco=-1;
			for(int i=0; i<sequence.length(); i++) {
					if (sequence.charAt(i)=='(') {
						int count = 1;
						int k;
						for(k = i+1; k < sequence.length(); k++) {
							if(sequence.charAt(k)=='(')
								count++;
							else
								if(sequence.charAt(k)==')') {
									count--;
									if(count==0) {
										arco=k ;
										break;
									}
								}
									
						}
						if(k == sequence.length())
							System.out.println("Wrong sequence " + name + ". No arc for index " + i);
					}
			}
	}
	
	public static void printTree(Operation op) {
		if (op == null) {
			System.out.println("fine ramo");
			System.out.println("");
			return;
		}
		System.out.println(op.getIndexString());
		System.out.println(op.getOperationString());
		System.out.println(op.getPartialScore());
		System.out.println("");
		printTree(op.getLeft());
		printTree(op.getRight());
	}
	
	public static void printMatch(Operation op) {
		if (op == null) {
			return;
		}
		if(op.getOperationString().contentEquals("Arc Match")) {
			System.out.println(op.getIndexString());
			System.out.println(op.getOperationString());
		}
		printMatch(op.getLeft());
		printMatch(op.getRight());
	}
	
	
	
public static void printOperationsMinutiaOnFile(String path, String file1, String file2) {

			
			try {
				
				String sequence1;
				int[] array1;
				FileReader fString1;
				fString1 = new FileReader(path+"/"+ file1 + "/FP-String.txt");
				BufferedReader bString1 = new BufferedReader(fString1);
				sequence1 = bString1.readLine();
				array1=new int[sequence1.length()];
				for(int index1=0; index1<sequence1.length(); index1++) {
					//System.out.println(array1[index]);
					array1[index1]= Integer.parseInt(bString1.readLine());
					
				}
				fString1.close();
				bString1.close();

				String sequence2;
				int[] array2;
				FileReader fString2;
				fString2 = new FileReader(path+"/"+ file2 + "/FP-String.txt");
				BufferedReader bString2 = new BufferedReader(fString2);
				sequence2 = bString2.readLine();
				array2=new int[sequence2.length()];
				for(int index2=0; index2<sequence2.length(); index2++) {
					//System.out.println(array2[index]);
					array2[index2]= Integer.parseInt(bString2.readLine());

				}
				fString2.close();
				bString2.close();
				
				
				System.out.println(sequence1);
				System.out.println(sequence2);
				System.out.println(sequence1.length());
				System.out.println(sequence2.length());
				
				path = path+"/comparison_results/" + file1 +"-"+file2+".txt";
				File file = new File(path);
				if(file.exists())
					file.delete();
				file.createNewFile();
				
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(file1+": "+ sequence1 + "\n" + file2 +": "+ sequence2+ "\n \n");
				Gardenia gardenia = new Gardenia();
				gardenia.setSequences(sequence1, sequence2);
				gardenia.setIndexMinutiaArray(array1,array2);
				gardenia.printMatchMapMinutiaOnFile(bw, file1, file2);
				fw.close();
				bw.close();
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
	
	
	public static void printOperationsOnFile(String file1, String file2) {
		
		String path = "/home/laura/Documenti/fingerprint2/examples2/operations/";
		
			
			
			try {
				
				String sequence1;
				int[] array1;
				String sequence2;
				int[] array2;
				FileReader fString;
				fString = new FileReader("/home/laura/Documenti/fingerprint2/examples2/transparency_" + file1 + ".tif/allFingerprintString.txt");
				BufferedReader bString = new BufferedReader(fString);
				sequence1 = bString.readLine();
				array1=new int[sequence1.length()];
				for(int index=0; index<sequence1.length(); index++) {
					array1[index]= Integer.parseInt(bString.readLine());
					//System.out.println(array1[index]);
				}
				
				fString = new FileReader("/home/laura/Documenti/fingerprint2/examples2/transparency_" + file2 + ".tif/allFingerprintString.txt");
				bString = new BufferedReader(fString);
				sequence2 = bString.readLine();
				array2=new int[sequence2.length()];
				for(int index=0; index<sequence2.length(); index++) {
					array2[index]= Integer.parseInt(bString.readLine());
					//System.out.println(array2[index]);
				}
				
				
				System.out.println(sequence1);
				System.out.println(sequence2);
				System.out.println(sequence1.length());
				System.out.println(sequence2.length());
				
				path = path + file1 +"-"+file2+".txt";
				File file = new File(path);
				if(file.exists())
					file.delete();
				file.createNewFile();
				
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(file1+": "+ sequence1 + "\n" + file2 +": "+ sequence2+ "\n \n");
				Gardenia gardenia = new Gardenia();
				gardenia.setSequences(sequence1, sequence2);
				gardenia.setIndexMinutiaArray(array1,array2);
				gardenia.printMatchMapOnFile(bw, file1, file2);
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
	
	public static void printScoreTwoSequencesProve() {
		//012_3_7
		String sequence1 = "()(((((((()((.(((()(()(()..()..().).).))())))()))()))))((()))())()";
		//012_3_8
		String sequence2 = "()(((((((()((.(((()(()(()..()..().).).))())))()))()))))((()))())()";
		System.out.println(sequence1);
		System.out.println(sequence2);
		//double startTime = System.currentTimeMillis()*(0.001);
		
		
		Gardenia gardenia = new Gardenia();
		gardenia.setSequences(sequence1, sequence2);
		//int score = gardenia.getScore();
		//System.out.println(score);
		
		//System.out.println("");
		
		gardenia.getMatchMap();
		
		
		//Operation finalScore = gardenia.getAllOperatios();
		//double endTime = System.currentTimeMillis()*(0.001);
		//System.out.println(finalScore.getPartialScore());
		//System.out.println(endTime-startTime);
		//printMatch(finalScore);
	}
	
	
	public static void printHeatFile() {
		String path = "/home/laura/Documenti/fingerprint4/examples2/scores_all4_15760.txt";
		
		List<String> allFingerprintName = new ArrayList<>();
		try {
			FileReader f;
			f = new FileReader("/home/laura/Documenti/fingerprint4/examples2/Names.txt");
			BufferedReader b = new BufferedReader(f);
			String s;
				    
			while(true) {
			   	s = b.readLine();
			   	if(s == null)
				break;
			   	allFingerprintName.add(s);
		    }
			
			List<String> allFingerprintString = new ArrayList<>();
			int sizeMatrix = allFingerprintName.size();
			
			
			String string;
			for(int i=0; i<sizeMatrix; i++) {
				if(allFingerprintName.get(i).contains("_4_")) {
				FileReader fString = new FileReader("/home/laura/Documenti/fingerprint4/examples2/" + allFingerprintName.get(i) + "/FP-String.txt");
				BufferedReader bString = new BufferedReader(fString);
				string = bString.readLine();
			   	allFingerprintString.add(string);
				}
			}
			
			int mySize=allFingerprintString.size();
			int[][] matrixScore = new int[mySize][mySize];
			
			
			File file = new File(path);
			if(file.exists())
				file.delete();
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			
			
			for(int i=0; i < mySize; i++) {
				for(int j = i; j<mySize; j++) {
					System.out.println(i + " " + j);
					Gardenia gardenia = new Gardenia();
					gardenia.setSequences(allFingerprintString.get(i), allFingerprintString.get(j));
					double startTime = System.currentTimeMillis()*(0.001);
					int score = gardenia.getScoreKey();
					double endTime = System.currentTimeMillis()*(0.001);
					matrixScore[i][j]=score;
					matrixScore[j][i]=score;
					System.out.println(score);
					System.out.println(endTime-startTime);
				}
			}
			
				for(int i=0; i < mySize; i++) {
					for(int j = 0; j<mySize; j++) {
						bw.write(matrixScore[i][j]+ " ");
						if(j==mySize-1)
							bw.write('\n');
						bw.flush();
					}
				}
				
				bw.close();
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}