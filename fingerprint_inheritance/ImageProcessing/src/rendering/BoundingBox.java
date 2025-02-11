package rendering;

import rendering.Utility.*;
import rendering.Utility.json.EdgeParser;
import rendering.Utility.json.MinutiaeParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BoundingBox {

    private static int m_imageHeight;
    private static int m_imageWidth;
    private static MyPoint m_imageCenter;
    private static int[][] m_imageMatrix;
    /*[0] Contains Min X
    * [1] Contains Max X
    * [2] Contains Min Y
    * [3] Contains Max Y*/
    private static final ArrayList<MyPoint> m_externalPoints = new ArrayList<>(4);


	private int thirdQuadrantPointsNumber;
	private int fourthQuadrantPointsNumber;
	private static int cutPixel;

	static List<Minutia> minutiae = new ArrayList<>();
	static List<Minutia> innerMinutiae = new ArrayList<Minutia>();
	static List<Minutia> edgeMinutiae = new ArrayList<Minutia>();
	
	static List<MyEdge> boundingEdges = new ArrayList<>();
	static String fingerprintString = new String();
	static int[] minutiaIdArray;
	static List<String> matchingStrings = new ArrayList<>();
	static List<Match> matchingTable = new ArrayList<>();
	
	static List<String> allFingerprintName = new ArrayList<>();
	
	static String LCS;
    
    public BoundingBox(int imageHeight, int imageWidth)  {

        m_imageHeight = imageHeight;
        m_imageWidth = imageWidth;
        for(int i=0; i<4; i++) {
        	m_externalPoints.add(new MyPoint());
        }
        
    }
    
    public void makeBoundingBoxSingle() throws IOException{
    	MinutiaeParser parser = new MinutiaeParser();
    	EdgeParser edgeParser = new EdgeParser();
        //generateMatrixFromSkeleton();
        String folderPath = FilesName.getInstance().getFolderPath();

			setM_externalPoints();
        	//setM_imageCenter();
        	boundingEdges = edgeParser.getEdges(folderPath + "inheritance/graph_c++.json");
			innerMinutiae = parser.getMinutiae(folderPath + "inheritance/internal_minutiae.json");
			edgeMinutiae = parser.getMinutiae(folderPath + "inheritance/edge_minutiae.json");
  
        	//examineFingerprintShape();
			//findBoundingPoints();
			//connectBoundingBoxes();
			createFingerprintString();			
		printSingleFingerprintOnFile();
        System.out.println("\nThe End :)\n");
    }

    private void printSingleFingerprintOnFile() throws IOException {
    	String folderPath = FilesName.getInstance().getFolderPath();
		FileWriter w = new FileWriter(folderPath + "allFingerprintString.txt", false);
		w.write(fingerprintString);
		w.write("\n");
		for(int i = 0; i<minutiaIdArray.length; i++) {
			w.write(minutiaIdArray[i]+ "\n");
		}
		w.flush();
    }

/*	public void makeBoundingBox() throws IOException {

    	MinutiaeParser parser = new MinutiaeParser();
    	EdgeParser edgeParser = new EdgeParser();
    	String fingerprintName;
        generateMatrixFromSkeleton();
        readFingerprintFromAFile("/home/alessandro/test/Names.txt");
        //Se non hai già generato il file contenente tutte le stringhe delle impronte digitali, esegui semplicemente così
        
        //readStringsFromAFile("home/alessandro/test/allFingerprintString.txt");
        
        //altrimenti se hai già il file (allFingerprintString.txt), commenta da riga 83 (compresa) a riga 93 (compresa) e 
        //la 97 e rimuovi il commento dalla riga 75 e 96
        for (int k = 0; k < allFingerprintName.size(); k++) {
        	
			fingerprintName = allFingerprintName.get(k);
		
			setM_externalPoints();
        	setM_imageCenter();
        	boundingEdges = edgeParser.getEdges("/home/alessandro/test/transparency_" + fingerprintName + "/inheritance/graph_c++.json");
			innerMinutiae = parser.getMinutiae("/home/alessandro/test/transparency_" + fingerprintName + "/inheritance/internal_minutiae.json");
			edgeMinutiae = parser.getMinutiae("/home/alessandro/test/transparency_" + fingerprintName + "/inheritance/edge_minutiae.json");
  
        	examineFingerprintShape();
			findBoundingPoints();
			connectBoundingBoxes();
			createFingerprintString();			
			matchingStrings.add(fingerprintString);
			
			//matchingAlgorithm("/home/alessandro/test/transparency_", fingerprintName, k);
        }
        printFingerprintStringOnAFile();
        System.out.println("The End :-)\n");
    }
    */
	
    public static void printFingerprintStringOnAFile() throws IOException {
    	String folderPath = FilesName.getInstance().getFolderPath();
		FileWriter w = new FileWriter(folderPath + "allFingerprintString.txt", true);
		for(int k = 0; k < matchingStrings.size(); k++) {
			w.write(matchingStrings.get(k));
			w.write("\n");
		}
		w.flush();
    }
    
	public static void readFingerprintFromAFile(String fileName) throws IOException {
		//http://www.dis.uniroma1.it/liberato/informatica/letturastringhe.shtml
		FileReader f = new FileReader(fileName);
		BufferedReader b = new BufferedReader(f);
		String s;
			    
		while(true) {
		   	s = b.readLine();
		   	if(s == null)
			break;
		   	allFingerprintName.add(s);
	    }
		
	}
	public static void longestCommonSubsequence(String str1, String str2) {

		LongestCommonSubsequence longestCommonSubsequence = new LongestCommonSubsequence();

		int m = str1.length();
		int n = str2.length();
		longestCommonSubsequence.lcs(str1, str2, m, n);
		LCS = longestCommonSubsequence.getLongestCommonSubsequence();

	}

	
	public static void matchingAlgorithm(String pathName, String f1, int k) throws IOException {
		Match m = null;
		String f2, ms1, ms2;
		double[] scores = new double[allFingerprintName.size()];
		double[] percentages = new double[allFingerprintName.size()];
		int j = 0;
		// System.out.println("Compute the local alignments between two files\n ");
		for (int w = 0; w < matchingStrings.size(); w++) {
			m = new Match();
			f2 = allFingerprintName.get(w);
			ms1 = matchingStrings.get(k);
			//System.out.println(k + "\n");
			ms2 = matchingStrings.get(w);
			
			m.setFirstFingerprintName(f1);
			m.setSecondFingerprintName(f2);
			m.setFirstFingerprintString(ms1);
			m.setSecondFingerprintString(ms2);
			// compute and output the score and alignments
			SmithWaterman sw = new SmithWaterman(ms1, ms2);
			// System.out.println("The dynamic programming distance matrix is");
			// sw.printDPMatrix();
			List ms = sw.getMatches();
			m.setScore(sw.getAlignmentScore());
			//scores[j] = sw.getAlignmentScore();
			longestCommonSubsequence(ms1, ms2);
			m.setLCS(LCS);
			calculatePercentage(m, ms1, ms2);
			percentages[j] = m.getPercentage();
			
			matchingTable.add(m);
			j++;
			sw.getMatches();
			
		}
		m.printPercentageOnACSV(percentages);
		for(int w = 0; w < percentages.length; w++) {
			percentages[w] = 0;
		}
	}

	public static void calculatePercentage(Match m, String str1, String str2) {
		if(str1.length() > str2.length()) 
			m.setPercentage((LCS.length()*100)/str2.length());
		else 
			m.setPercentage((LCS.length()*100)/str1.length());			
	}
	
	@SuppressWarnings("resource")
	static void printFingerprintStringOnAFile(String pathName, String fileOutName, String fingerprintString)
			throws IOException {
		FileWriter w = new FileWriter(pathName + fileOutName + ".txt", true);

		w.write(fingerprintString);
		w.write('\n');
		w.flush();

	}

	public static void getAllX1Minutiae(List<Minutia> minutiaX1Array) {
		List<Minutia> app = new ArrayList<>();
		
		//for (int k = 0; k < boundingEdges.size(); k++) {
		for(MyEdge item : boundingEdges) {
			
			if (item.getX1() < m_imageCenter.getWidth())
				minutiaX1Array.add(new Minutia(item.getX1(), item.getY1(), item.getId1()));
			else
				app.add(new Minutia(item.getX1(), item.getY1(), item.getId1()));
			if(!searchMinutia(innerMinutiae, new Minutia(item.getX2(), item.getY2()))) {
				if (item.getX2() < m_imageCenter.getWidth())
					minutiaX1Array.add(new Minutia(item.getX2(), item.getY2(), item.getId2()));
				else
					app.add(new Minutia(item.getX2(), item.getY2(), item.getId2()));}
//		}
		}
		Collections.sort(minutiaX1Array);
	//	for(Minutia item : minutiaX1Array) {
	//		System.out.println("Id: " + item.getId());
	//	}
		Collections.sort(app);
		for(Minutia item : app) {
			System.out.println("Id APP: " + item.getId());
		}
		Collections.reverse(app);
		minutiaX1Array.addAll(app);


	}

	public static void createFingerprintString() {

		List<Minutia> minutiaX1Array = new ArrayList<>();
		int[] elementCounter = new int[3];
		// first array cell contains open brackets, second array cell contains close
		// brackets, third array cell contains asterisk
		fingerprintString = "";

		getAllX1Minutiae(minutiaX1Array);
	/*	for(int i=0; i<minutiaX1Array.size(); i++) {
			if(minutiaX1Array.get(i).getId()<= 10) {
			System.out.print("Id: "+minutiaX1Array.get(i).getId() + " ");
			System.out.print("X: "+minutiaX1Array.get(i).getX() + " ");
			System.out.print("Y: "+minutiaX1Array.get(i).getY());
			System.out.println(" ");
			}
		}*/
		
		//elementCounter = creating(minutiaX1Array, elementCounter);
		//elementCounter =
		creatingForGardenia(minutiaX1Array, elementCounter);
		//correctString(elementCounter);
	}
	
	private static void correctString(int[] elementCounter){
		
		if(elementCounter[0]-elementCounter[1] == 0)
			return;
		
		int arco=-1;
			for(int i=0; i<fingerprintString.length(); i++) {
				if (fingerprintString.charAt(i)=='(') {
					int count = 1;
					int k;
					for(k = i+1; k < fingerprintString.length(); k++) {
						if(fingerprintString.charAt(k)=='(')
							count++;
						else
							if(fingerprintString.charAt(k)==')') {
								count--;
								if(count==0) {
									arco=k ;
									break;
								}
							}							
					}
					if(k == fingerprintString.length()) {
						int[] temp = new int[minutiaIdArray.length-1];
						for (int j = 0; j<minutiaIdArray.length; j++) {
							if(j<i) {
								temp[j]=minutiaIdArray[j];
							}else if(j>i) {
								temp[j-1]=minutiaIdArray[j];
							}
						}
						minutiaIdArray = temp;
						String tempString = new String();
						if(i==0) {
							tempString =fingerprintString.substring(i+1, fingerprintString.length());
						}else if (i==fingerprintString.length()-1) {
							tempString = fingerprintString.substring(0, i-1);
						}else {
							tempString = fingerprintString.substring(0, i-1) + fingerprintString.substring(i+1, fingerprintString.length());
						}
						fingerprintString = tempString;
					}
								
				}
		}
		
	}

	public static void correctBracketsPosition() {
		String app;
		int co = 0, cc = 0, disparityIndex = 0, difference = 0;

		for (int k = 0; k < fingerprintString.length(); k++) {
			if (fingerprintString.charAt(k) == '(')
				co++;
			if (fingerprintString.charAt(k) == ')')
				cc++;
			if (co - cc < difference) {
				difference = co - cc;
				disparityIndex = k + 1;
			}
		}
		app = fingerprintString.substring(0, disparityIndex);
		if (disparityIndex != 0)
			replaceFirstOccurency(app, ' ');
		fingerprintString += app;

	}

	public static void replaceFirstOccurency(String app, char substitution) {

		char[] string = fingerprintString.toCharArray();
		char[] support = app.toCharArray();
		int beginIndex = 0, endIndex = 0, totalMatch = 0, i = 0, w = 0;

		for (int k = 0; k < fingerprintString.length(); k++) {
			if (string[k] == support[i]) {
				beginIndex = k;
				w = k++;
				for (int j = 0; j < app.length(); j++) {
					if (string[w] == support[j]) {
						totalMatch++;
					}
					w++;
				}
				endIndex = beginIndex + w;
				k--;
				if (totalMatch == app.length()) {
					for (int j = beginIndex; j < endIndex; j++)
						string[j] = substitution;
					break;
				}
				w = i = endIndex = beginIndex = totalMatch = 0;

			}
		}
		fingerprintString = String.valueOf(string);
		fingerprintString = fingerprintString.replace(" ", "");
	}

	public static int[] creating(List<Minutia> list, int[] counter) {

		Minutia actual;
		int result;
		
		for (int k = 0; k < list.size(); k++) {
			actual = new Minutia(list.get(k).getX(), list.get(k).getY());
			result = searchEdge(actual);
			if (result == 0) {
				fingerprintString += '(';
				counter[0]++;
			} else if (result == 1) {
				fingerprintString += ')';
				counter[1]++;
			} else if (result == 2) {
				fingerprintString += '*';
				counter[2]++;
			} else {
				System.out.println("Minuzia non prevista\n");
			}
		}
		return counter;
	}
	
	public static int[] creatingForGardenia(List<Minutia> list, int[] counter) {

		Minutia actual;
		int result;
		minutiaIdArray = new int[list.size()];
		for (int k = 0; k < list.size(); k++) {
			actual = new Minutia(list.get(k).getX(), list.get(k).getY(),list.get(k).getId());
			minutiaIdArray[k] = actual.getId();
			result = searchEdge(actual);
			if (result == 0) {
				fingerprintString += '(';
				counter[0]++;
			} else if (result == 1) {
				fingerprintString += ')';
				counter[1]++;
			} else if (result == 2) {
				fingerprintString += '.';
				counter[2]++;
			} else {
				System.out.println("Minuzia non prevista\n");
			}
		}
		return counter;
	}

	public static void correctBrackets(int[] counter) {
		if (counter[0] < counter[1])
			for (int k = counter[0]; k < counter[1]; k++)
				fingerprintString = '(' + fingerprintString;
		else
			for (int k = counter[1]; k < counter[0]; k++)
				fingerprintString = fingerprintString + ')';
	}

	public static int searchEdge(Minutia a) {

		MyEdge tmp;
		
		for (int k = 0; k < boundingEdges.size(); k++) {
			
			tmp = boundingEdges.get(k);
			
			if (tmp.getX1() == a.getX() && tmp.getY1() == a.getY()) {
				if (!tmp.getVisitedM1()) {
					boundingEdges.get(k).setVisitedM1(true);
					if (searchMinutia(innerMinutiae,
							new Minutia(boundingEdges.get(k).getX2(), boundingEdges.get(k).getY2()))) {
						boundingEdges.get(k).setVisitedM2(true);
						return 2;
					}
					if (!tmp.getVisitedM2()) {
					//	boundingEdges.get(k).setVisitedM2(true);
						return 0; // open bracket
					} else {
						return 1; // close bracket
					}

				}
			}
			if (tmp.getX2() == a.getX() && tmp.getY2() == a.getY()) {
				if (!tmp.getVisitedM2()) {
					boundingEdges.get(k).setVisitedM2(true);
					if (searchMinutia(innerMinutiae,
							new Minutia(boundingEdges.get(k).getX1(), boundingEdges.get(k).getY1()))) {
						boundingEdges.get(k).setVisitedM1(true);
						return 2;
					}
					if (!tmp.getVisitedM1()) {
						//boundingEdges.get(k).setVisitedM1(true);
						return 0; // open bracket
					} else {
						return 1; // close bracket
					}

				}
			}
		}
		return -1;

	}

	public static Minutia searchAllMinutia(List<Minutia> list, Minutia m) {

		for (Minutia item : list)
			if (m.getX() == item.getX() && m.getY() == item.getY())
				return item;
		return null;
	}

	public static boolean searchMinutia(List<Minutia> list, Minutia minutia) {
		if (minutia == null)
			return false;

		for (Minutia m : list)
			if (minutia.getX() == m.getX() && minutia.getY() == m.getY())				
				return true;
		return false;
	}


		
	//corretto sotto
	@SuppressWarnings("resource")
	public static void readStringsFromAFile(String fileName) throws IOException {
		//http://www.dis.uniroma1.it/liberato/informatica/letturastringhe.shtml
		FileReader f = new FileReader(fileName);
	    BufferedReader b = new BufferedReader(f);
	    String s;
	    
	    while(true) {
	    	s = b.readLine();
	    	if(s == null)
	    		break;
	    	matchingStrings.add(s);
	    }
	}

/*    public void foundEdgeMinutia() throws IOException {
    	locateEdgeMinutiae();
    	outputMinutiaeInAJSONFile("300-edge-minutia", edgeMinutiae);
    }
    
    public void foundInnerMinutia() throws IOException {
    	deleteExternalMinutiae();
    	outputMinutiaeInAJSONFile("301-inner-minutia", innerMinutiae);
    }*/
    
    private void setM_externalPoints() {

        String filename = FilesName.getInstance().getSkeletonData();
        File file = new File(filename);
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(file);

            int content;
            int x = 0;
            int y = 0;
            int p;
            while ((content = fis.read()) != -1) {

                if (content == 1) {
                    for (int i = 0; i <4; i++) {
                        if (
                        		((x < m_externalPoints.get(i).getX()) && (i == 0)) ||
                                ((x > m_externalPoints.get(i).getX()) && (i == 1)) ||
                                ((y < m_externalPoints.get(i).getY()) && (i == 2)) ||
                                ((y > m_externalPoints.get(i).getY()) && (i == 3)) )
                            	m_externalPoints.get(i).setLocation(y,x);
                    }
                }

                x = x + 1;
                if (x == m_imageWidth) {
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
    
    private void outputMinutiaeInAJSONFile(String fileName, List<Minutia> newMinutiae) throws IOException {
		FileWriter w = new FileWriter(fileName + ".json", true);
		
		w.write("{\n");
		w.write('"');
		w.write("width");
		w.write('"');
		w.write(" : ");
		w.write(m_imageWidth + ",\n");
		w.write('"');
		w.write("height");
		w.write('"');
		w.write(" : ");
		w.write(m_imageHeight + ",\n");
		w.write('"');
		w.write("minutiae");
		w.write('"');
		w.write(" : [\n");

		for(int j = 0; j < newMinutiae.size(); j++) {
			w.write("{\n");
			w.write('"');
			w.write("x");
			w.write('"');
			w.write(" : ");
			w.write(newMinutiae.get(j).getX() + ",\n");
			w.write('"');
			w.write("y");
			w.write('"');
			w.write(" : ");
			w.write(newMinutiae.get(j).getY() + ",\n");
			w.write('"');
			w.write("direction");
			w.write('"');
			w.write(" : ");
			w.write(newMinutiae.get(j).getDirection() + ",\n");
			w.write('"');
			w.write("type");
			w.write('"');
			w.write(" : ");
			if(newMinutiae.get(j).getType() == 0) {
				w.write('"');
				w.write("ending");
				w.write('"');
				w.write("\n");
			}
			else {
				w.write('"');
				w.write("bifurcation");
				w.write('"');
				w.write("\n");
			}
			
			if(j == newMinutiae.size() - 1)
				w.write("}\n");
			else
				w.write("},\n");
		}
		w.write("]\n}");
		w.flush();
	}
    
    /*private void locateEdgeMinutiae() {
		Minutia m;
		if(checkFingerprintHeight(0) == 2) {
			for(int i = firstQuadrantPointsOfInterest[0].getWidth() + cutPixel; i < secondQuadrantPointsOfInterest[0].getWidth() - cutPixel; i++)
				if(m_imageMatrix[secondQuadrantPointsOfInterest[0].getHeight() + cutPixel + 1][i] == 1) {
					m = new Minutia(i,secondQuadrantPointsOfInterest[0].getHeight() + cutPixel,0,0);
					edgeMinutiae.add(m);
				}	
			
		}
		else {
			for(int i = firstQuadrantPointsOfInterest[0].getWidth() + cutPixel ; i < secondQuadrantPointsOfInterest[0].getWidth() - cutPixel; i++)
				if(m_imageMatrix[firstQuadrantPointsOfInterest[0].getHeight() + cutPixel + 1][i] == 1) {
					m = new Minutia(i,firstQuadrantPointsOfInterest[0].getHeight() + cutPixel,0,0);
					edgeMinutiae.add(m);		
			}	
		}
		
		for(int j = m_externalPoints.get(2).getHeight() + 1; j < m_externalPoints.get(3).getHeight() - 1; j++) 
			for(int i = m_externalPoints.get(0).getWidth() - 1; i < m_imageCenter.getWidth(); i++) {
				if(m_imageMatrix[j][i] == 3 && m_imageMatrix[j][i + 1] == 1) {
					m = new Minutia(i,j,0,0);
					edgeMinutiae.add(m);
					break;
				}
			*//*	if(checkIndicesQuadrant(j,i) == 1) {
					if(m_imageMatrix[j][i] == 3 && m_imageMatrix[j + 1][i] == 0 && m_imageMatrix[j + 1][i + 1] == 1) {
						m = new Minutia(i,j,0,0);
						edgeMinutiae.add(m);
						break;
					}
				}
				else {
					if(m_imageMatrix[j][i] == 3 && m_imageMatrix[j - 1][i] == 0 && m_imageMatrix[j - 1][i + 1] == 1) {
						m = new Minutia(i,j,0,0);
						edgeMinutiae.add(m);
						break;
					}
				}*//*
				
			}
		for(int j = m_externalPoints.get(2).getHeight() + 1; j < m_externalPoints.get(3).getHeight() - 1; j++)
			for(int i = m_externalPoints.get(1).getWidth() + 1; i > m_imageCenter.getWidth(); i--) {
				if(m_imageMatrix[j][i] == 3 && m_imageMatrix[j][i - 1] == 1) {
					m = new Minutia(i,j,0,0);
					edgeMinutiae.add(m);
					break;
				}
			*//*	if(checkIndicesQuadrant(j,i) == 2) {
					if(m_imageMatrix[j][i] == 3 && m_imageMatrix[j + 1][i] == 0 && m_imageMatrix[j + 1][i - 1] == 1) {
						m = new Minutia(i,j,0,0);
						edgeMinutiae.add(m);
						break;
					}
				}
				else {
					if(m_imageMatrix[j][i] == 3 && m_imageMatrix[j - 1][i] == 0 && m_imageMatrix[j - 1][i - 1] == 1) {
						m = new Minutia(i,j,0,0);
						edgeMinutiae.add(m);
						break;
					}
				}	*//*
			}
		if(checkFingerprintHeight(1) == 4) {
			for(int i = fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getWidth() - cutPixel; i > thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getWidth() + cutPixel; i--)
				if(m_imageMatrix[fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getHeight() - cutPixel - 1][i] == 1) {
					m = new Minutia(i,fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getHeight() - cutPixel,0,0);
					edgeMinutiae.add(m);	
				}	
		}
		else {
			for(int i = thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getWidth() + cutPixel; i < fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getWidth() - cutPixel; i++)
				if(m_imageMatrix[thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getHeight() - cutPixel - 1][i] == 1) {
					m = new Minutia(i,thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getHeight() - cutPixel,0,0);
					edgeMinutiae.add(m);
				}	
		}
	}
	
	private int checkIndicesQuadrant(int j, int i) {
		if(j < m_imageCenter.getHeight())
			if(i < m_imageCenter.getWidth())
				return 1;
			else
				return 2;
		else
			if(i < m_imageCenter.getWidth())
				return 3;
			else
				return 4;
	}
	
	private void deleteExternalMinutiae() {
		
		for(int k = 0; k < minutiae.size(); k++) {		
			if(checkFingerprintHeight(0) == 2) {
				if(checkFingerprintHeight(1) == 3) {
					if(minutiae.get(k).getY() >= secondQuadrantPointsOfInterest[0].getHeight() + cutPixel && minutiae.get(k).getY() <= thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getHeight() - cutPixel) {
						deleting(minutiae.get(k));						
					}
				}
				else {
					if(minutiae.get(k).getY() >= secondQuadrantPointsOfInterest[0].getHeight() + cutPixel && minutiae.get(k).getY() <= fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getHeight() - cutPixel) {
						deleting(minutiae.get(k));													
					}
				}
			}
			else {
				if(checkFingerprintHeight(0) == 3) {
					if(minutiae.get(k).getY() >= firstQuadrantPointsOfInterest[0].getHeight() + cutPixel && minutiae.get(k).getY() + cutPixel <= thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getHeight() - cutPixel) {
						deleting(minutiae.get(k));						
					}
				}
				else {
					if(minutiae.get(k).getY() >= firstQuadrantPointsOfInterest[0].getHeight() + cutPixel && minutiae.get(k).getY() <= fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getHeight() - cutPixel){
						deleting(minutiae.get(k));										
					}
				}
			}
		}
	}
	
	private int checkFingerprintHeight(int half) {
		if(half == 0) 
			if(firstQuadrantPointsOfInterest[0].getHeight() > secondQuadrantPointsOfInterest[0].getHeight())
				return 2;
			else
				return 1;
		else
			if(thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getHeight() > fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getHeight()) 
				return 3;
			else
				return 4;
		
	}
	
	private void deleting(Minutia analyzingPoint) {
		MyPoint boundPoint;
			
		boundPoint = new MyPoint();
		if(analyzingPoint.getX() > m_imageCenter.getWidth()) {
			boundPoint.setHeight(analyzingPoint.getY());
			boundPoint.setWidth(discoverBoundingPoint(boundPoint, 1));
			if(boundPoint.getWidth() >= analyzingPoint.getX())
				innerMinutiae.add(analyzingPoint);		
			
		}
		else {
			boundPoint.setHeight(analyzingPoint.getY());
			boundPoint.setWidth(discoverBoundingPoint(boundPoint, 0));
			if(boundPoint.getWidth() <= analyzingPoint.getX()) 
				innerMinutiae.add(analyzingPoint);	
		}
		
	}
	
	private int discoverBoundingPoint(MyPoint boundPoint, int half) {

		if(half == 0) {
			for(int i = m_externalPoints.get(0).getWidth(); i < m_imageCenter.getWidth(); i++) 
				if(m_imageMatrix[boundPoint.getHeight()][i] == 3) 
					return i;
							
		}
		else {
			for(int i = m_externalPoints.get(1).getWidth(); i > m_imageCenter.getWidth(); i--) 
				if(m_imageMatrix[boundPoint.getHeight()][i] == 3) 
					return i;
		}
	
		return 0;
	}
   
	@SuppressWarnings("unused")
	private void renderResult(String fingerprintName, String pathName) {
        BufferedImage img = new BufferedImage(m_imageWidth, m_imageHeight, BufferedImage.TYPE_INT_ARGB);

        int red = new Color(204, 0, 0).getRGB();
        int black = new Color(0, 0, 0).getRGB();
        int p;
        for (int j = 0; j < m_imageHeight; j++) {
            for (int i = 0; i < m_imageWidth; i++) {
                if (m_imageMatrix[j][i] == 3) {
                    p = red;
                } else if (m_imageMatrix[j][i] == 0) {
                    p = 0;
                } else {
                    p = black;
                }

                img.setRGB(i, j, p);
            }
        }


        try {
           // File f = new File(pathName+fingerprintName+"/"+fingerprintName + "_img.png");
        	 File f = new File(fingerprintName + ".png");
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

    }
    
    private void generateMatrixFromSkeleton(){

        m_imageMatrix = new int[m_imageWidth][m_imageHeight];
        m_externalPoints.get(0).setLocation(m_imageHeight,m_imageWidth);
        m_externalPoints.get(1).setLocation(0,0);
        m_externalPoints.get(2).setLocation(m_imageHeight, m_imageWidth);
        m_externalPoints.get(3).setLocation(0,0);
                
        cutPixel = 10;
        
        int color = new Color(0, 0, 0).getRGB();
        String filename = FilesName.getInstance().getSkeletonData();
        File file = new File(filename);
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(file);

            int content;
            int x = 0;
            int y = 0;
            int p;
            //System.out.println("y=" + m_imageHeight+ " x=" +  m_imageWidth+"\n");
            while ((content = fis.read()) != -1) {
            	//System.out.println(x + " " + y +"\n");
            	if (y<m_imageHeight) {
                if (content == 1) {
                    m_imageMatrix[x][y] = color;
                } else {
                    m_imageMatrix[x][y] = 0;
                }

                x = x + 1;
                if (x == m_imageWidth) {
                    x = 0;
                    y = y + 1;
                    
                }
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
    
    private void setM_imageCenter() {
  
    	int c_X = (m_externalPoints.get(1).getWidth() - m_externalPoints.get(0).getWidth())/2;
		int c_Y = (m_externalPoints.get(3).getHeight() - m_externalPoints.get(2).getHeight())/2;
		m_imageCenter = new MyPoint();
		m_imageCenter.setLocation(m_externalPoints.get(2).getHeight() + c_Y, m_externalPoints.get(0).getWidth() + c_X);
    }
    
    private void connectBoundingBoxes() {

		if(firstQuadrantPointsOfInterest[0].getHeight() > secondQuadrantPointsOfInterest[0].getHeight()) {
			for(int i = firstQuadrantPointsOfInterest[0].getWidth() + cutPixel; i < secondQuadrantPointsOfInterest[0].getWidth() - cutPixel; i++)
				m_imageMatrix[firstQuadrantPointsOfInterest[0].getHeight() + cutPixel][i] = 3;
			
			for(int j = firstQuadrantPointsOfInterest[0].getHeight() + cutPixel; j < secondQuadrantPointsOfInterest[0].getHeight() + cutPixel; j++)
				m_imageMatrix[j][firstQuadrantPointsOfInterest[0].getWidth() + cutPixel] = 3;
		}
		else {
			
			for(int i = firstQuadrantPointsOfInterest[0].getWidth() + cutPixel; i < secondQuadrantPointsOfInterest[0].getWidth() - cutPixel; i++)
				m_imageMatrix[firstQuadrantPointsOfInterest[0].getHeight() + cutPixel][i] = 3;
			
			for(int j = firstQuadrantPointsOfInterest[0].getHeight() + cutPixel; j < secondQuadrantPointsOfInterest[0].getHeight() + cutPixel; j++)
				m_imageMatrix[j][secondQuadrantPointsOfInterest[0].getWidth() - cutPixel] = 3;	
		}
	
		if(thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getHeight() > fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getHeight()) {
		
			for(int i = thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getWidth() + cutPixel; i < fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getWidth() - cutPixel; i++)
				m_imageMatrix[thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getHeight() - cutPixel][i] = 3;
			
			for(int j = thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getHeight() - cutPixel; j > fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getHeight() - cutPixel; j--)
				m_imageMatrix[j][fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getWidth() - cutPixel] = 3;			
		}
		else {
			
			for(int i = fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getWidth() - cutPixel; i > thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getWidth() + cutPixel; i--)
				m_imageMatrix[fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getHeight() - cutPixel][i] = 3;
			
			for(int j = fourthQuadrantPointsOfInterest[fourthQuadrantPointsNumber].getHeight() - cutPixel; j > thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getHeight() - cutPixel; j--)
				m_imageMatrix[j][thirdQuadrantPointsOfInterest[thirdQuadrantPointsNumber].getWidth() + cutPixel] = 3;	
		}
		
	}

    private void examineFingerprintShape() {
		
		MyPoint referencePoint = new MyPoint();
		MyPoint beginPoint = new MyPoint();
		MyPoint endPoint = new MyPoint();
		int founded = 0, specialCase = 0;

		//first quadrant
		referencePoint.setLocation(m_externalPoints.get(2).getHeight(), m_externalPoints.get(0).getWidth());
		beginPoint.setLocation(m_externalPoints.get(2).getHeight(), m_externalPoints.get(0).getWidth());
		endPoint.setLocation(m_imageCenter.getHeight(), m_imageCenter.getWidth());
		while(founded < 6 || specialCase == 8) {
			referencePoint = discoverNextReferencePoint(1, referencePoint);
			if(referencePoint==null)
				break;
			founded = examine(firstQuadrantPointsOfInterest, beginPoint, endPoint, referencePoint, 1 );
			if(founded < 6)
				if((specialCase % 2) != 0) {
					referencePoint.increaseHeight();
					specialCase++;
				}
				else
					specialCase++;
			
		}
		firstQuadrantPointsNumber = founded - 1;
		
		//third quadrant
		founded = specialCase = 0;		
		beginPoint.setLocation(m_imageCenter.getHeight(), m_externalPoints.get(0).getWidth());
		endPoint.setLocation(m_externalPoints.get(3).getHeight(), m_imageCenter.getWidth());
		while((founded < 6 || specialCase == 8) && referencePoint!=null) {
			founded = examine(thirdQuadrantPointsOfInterest, beginPoint, endPoint, referencePoint, 3 );
			if((specialCase % 2) != 0) {
				referencePoint.increaseHeight();
				specialCase++;
			}
			else
				specialCase++;
			referencePoint = discoverNextReferencePoint(3, referencePoint);
		}
		thirdQuadrantPointsNumber = founded - 1;
		
		//second quadrant
		founded = specialCase = 0;
		referencePoint = new MyPoint();
		beginPoint.setLocation(m_externalPoints.get(2).getHeight(), m_externalPoints.get(1).getWidth());
		endPoint.setLocation(m_imageCenter.getHeight(), m_imageCenter.getWidth());
		referencePoint.setLocation(m_externalPoints.get(2).getHeight(), m_externalPoints.get(1).getWidth());
		while((founded < 6 || specialCase == 8)) {
			referencePoint = discoverNextReferencePoint(2, referencePoint);
			if(referencePoint == null)
				break;
			founded = examine(secondQuadrantPointsOfInterest, beginPoint, endPoint, referencePoint, 2 );
			if((specialCase % 2) != 0) {
				referencePoint.increaseHeight();
				specialCase++;
			}
			else
				specialCase++;
		}
		secondQuadrantPointsNumber = founded - 1;
		
		//fourth quadrant
		founded = specialCase = 0;
		beginPoint.setLocation(m_imageCenter.getHeight(), m_externalPoints.get(1).getWidth());
		endPoint.setLocation(m_externalPoints.get(3).getHeight(), m_imageCenter.getWidth());
		while((founded < 6 || specialCase == 8) && referencePoint!=null) {
			founded = examine(fourthQuadrantPointsOfInterest, beginPoint, endPoint, referencePoint, 4 );
			if((specialCase % 2) != 0) {
				referencePoint.increaseHeight();
				specialCase++;
			}
			else
				specialCase++;
			referencePoint = discoverNextReferencePoint(4, referencePoint);	
		}
		fourthQuadrantPointsNumber = founded - 1;
	}

	private int examine(MyPoint[] interestPoints, MyPoint begin, MyPoint end, MyPoint referencePoint, int quadrant){
		int range = 4, foundedPoints = 0;
		
		if(begin.getWidth() > end.getWidth()){
		 	//second and fourth quadrants
		 	 for(int j = begin.getHeight(); j < end.getHeight(); j++)
		 	 	for(int i = begin.getWidth(); i > end.getWidth(); i--)
		 	 	if(quadrantCondition(quadrant, referencePoint, range, j, i)) {
		 	 			interestPoints[foundedPoints] = new MyPoint();
						interestPoints[foundedPoints].setLocation(j, i);
						referencePoint.setLocation(j,i);
						writeDownPoint(quadrant, j, i);						
						foundedPoints++;
						break;
					}	
		}
		else {
		 //first and third quadrants
			for(int j = begin.getHeight(); j < end.getHeight(); j++) 
				for(int i = begin.getWidth(); i < end.getWidth(); i++) 
					if(quadrantCondition(quadrant, referencePoint, range, j, i)) {
						interestPoints[foundedPoints] = new MyPoint();
						interestPoints[foundedPoints].setLocation(j, i);
						referencePoint.setLocation(j,i);
						writeDownPoint(quadrant, j,i);
						foundedPoints++;
						break;
					}	
		}
		return foundedPoints;
	}

	private void writeDownPoint(int quadrant, int j, int i) {
		if(quadrant == 1)
			if(limitExceeded(quadrant, j, i) == 1)
				//j is in the third quadrant
				m_imageMatrix[m_imageCenter.getHeight() - 1][i + cutPixel] = 3;
			else if(limitExceeded(quadrant, j, i) == 2)
					//i is in the second quadrant
					m_imageMatrix[j + cutPixel][m_imageCenter.getWidth() - 1] = 3;
				else
					//j and i are in the correct quadrant
					m_imageMatrix[j + cutPixel][i + cutPixel] = 3;
		
		if(quadrant == 2)
			if(limitExceeded(quadrant, j, i) == 1)
				//j is in the fourth quadrant
				m_imageMatrix[m_imageCenter.getHeight() - 1][i - cutPixel] = 3;
			else if(limitExceeded(quadrant, j, i) == 2)
					//i is in the first quadrant
					m_imageMatrix[j + cutPixel][m_imageCenter.getWidth() + 1] = 3;
				else
					//j and i are in the correct quadrant
					m_imageMatrix[j + cutPixel][i - cutPixel] = 3;
		
		if(quadrant == 3)
			if(limitExceeded(quadrant, j, i) == 1)
				//j is in the first quadrant
				m_imageMatrix[m_imageCenter.getHeight() + 1][i + cutPixel] = 3;
			else if(limitExceeded(quadrant, j, i) == 2)
					//i is in the fourth
					m_imageMatrix[j - cutPixel][m_imageCenter.getWidth() - 1] = 3;
				 else
					//j and i are in the correct quadrant
					 m_imageMatrix[j - cutPixel][i + cutPixel] = 3;
		
		if(quadrant == 4)
			if(limitExceeded(quadrant, j, i) == 1)
				// j is in the second quadrant
				m_imageMatrix[m_imageCenter.getHeight() + 1][i - cutPixel] = 3;
			else if(limitExceeded(quadrant, j, i) == 2)
					//i is in the third quadrant
					m_imageMatrix[j - cutPixel][m_imageCenter.getWidth() + 1] = 3;
				 else
					 //j and i are in the correct quadrant
					 m_imageMatrix[j - cutPixel][i - cutPixel] = 3;
	}
	
	private int limitExceeded(int quadrant, int j, int i) {
		if(quadrant == 1) 
			if(j + cutPixel > m_imageCenter.getHeight()) 
				return 1;
			else if(i + cutPixel > m_imageCenter.getWidth())
				return 2;
		
		if(quadrant == 2) 
			if(j + cutPixel > m_imageCenter.getHeight()) 
				return 1;
			else if(i - cutPixel < m_imageCenter.getWidth())
				return 2;
		
		if(quadrant == 3) 
			if(j - cutPixel < m_imageCenter.getHeight()) 
				return 1;
			else if(i + cutPixel > m_imageCenter.getWidth())
				return 2;
		
		if(quadrant == 4) 
			if(j - cutPixel < m_imageCenter.getHeight()) 
				return 1;
			else if(i - cutPixel < m_imageCenter.getWidth())
				return 2;
		
		return 0;
	}
	
	private boolean quadrantCondition(int quadrant, MyPoint referencePoint, int range, int j, int i) {
		if(quadrant == 1 || quadrant == 4)
			if(m_imageMatrix[j][i] == 1 && referencePoint.getWidth() > i && i > referencePoint.getWidth() - range) 
				return true;
		if(quadrant == 2 || quadrant == 3)
			if(m_imageMatrix[j][i] == 1 && referencePoint.getWidth() < i && i < referencePoint.getWidth() + range) 
				return true;			
		return false;			
	}
		
	private MyPoint discoverNextReferencePoint(int quadrant, MyPoint referencePoint) {
		MyPoint supportPoint = new MyPoint();
		
		if(quadrant == 1) 
			for(int j = referencePoint.getHeight(); 
					j < m_imageCenter.getHeight(); 
					j++) 
					for(int i = referencePoint.getWidth() + 1; i < m_imageCenter.getWidth(); i++) 
						if(m_imageMatrix[j][i] == 1) {
							supportPoint.setLocation(j, i);
							return supportPoint;
						}
							

		if(quadrant == 2)
			for(int j = referencePoint.getHeight(); j < m_imageCenter.getHeight(); j++) 
				for(int i = referencePoint.getWidth() - 1; i > m_imageCenter.getWidth(); i--) 
					if(m_imageMatrix[j][i] == 1) {
						supportPoint.setLocation(j, i);
						return supportPoint;
					}

		if(quadrant == 3) 
			for(int j = referencePoint.getHeight(); j > m_imageCenter.getHeight(); j--) 
					for(int i = referencePoint.getWidth() + 1; i < m_imageCenter.getWidth(); i++) 
						if(m_imageMatrix[j][i] == 1) {
							supportPoint.setLocation(j, i);
							return supportPoint;
						}
							
		if(quadrant == 4)
			for(int j = referencePoint.getHeight(); j > m_imageCenter.getHeight(); j--) 
				for(int i = referencePoint.getWidth() - 1; i > m_imageCenter.getWidth(); i--) 
					if(m_imageMatrix[j][i] == 1) {
						supportPoint.setLocation(j, i);
						return supportPoint;
					}

		return null;
	}	
	
	private void findBoundingPoints() { 
		  MyPoint column = new MyPoint();
		  column.setLocation(0,0);
		  MyPoint nextColumn = new MyPoint();
		  nextColumn.setLocation(0,0);
		  
		  for(int j = m_externalPoints.get(2).getHeight(); j < m_externalPoints.get(3).getHeight(); j++) {
			for(int i = m_externalPoints.get(0).getWidth(); i < m_imageCenter.getWidth(); i++) {	
				if(m_imageMatrix[j][i] == 3) {
					column = new MyPoint();
					column.setLocation(j,i);
					
				}
				if(m_imageMatrix[j + 1][i] == 3) {
					nextColumn = new MyPoint();
					nextColumn.setLocation(j+1, i);
				}
				if(column.getHeight() != nextColumn.getHeight() - 1 && (nextColumn.getHeight() >= m_externalPoints.get(2).getHeight() && nextColumn.getHeight() <= m_externalPoints.get(3).getHeight()) && (column.getHeight() >= m_externalPoints.get(2).getHeight() && column.getHeight() <= m_externalPoints.get(3).getHeight()))
					connectVerticalBoundingPoints(column, nextColumn);
				else if((nextColumn.getWidth() >= m_externalPoints.get(0).getWidth() && nextColumn.getWidth() <= m_imageCenter.getWidth()) && (column.getWidth() >= m_externalPoints.get(0).getWidth() && column.getWidth() <= m_imageCenter.getWidth())) 
					connectHorizontalBoundingPoints(j, column, nextColumn);
				column = nextColumn;
			}
		}
		column = new MyPoint();
		column.setLocation(0,0);
		nextColumn = new MyPoint();
		nextColumn.setLocation(0,0);
		
		for(int j = m_externalPoints.get(2).getHeight(); j < m_externalPoints.get(3).getHeight(); j++) {
			for(int i = m_externalPoints.get(1).getWidth(); i > m_imageCenter.getWidth(); i--) {
				if(m_imageMatrix[j][i] == 3){
					column = new MyPoint();
					column.setLocation(j,i);
				}
				if(m_imageMatrix[j + 1][i] == 3) {
					nextColumn = new MyPoint();
					nextColumn.setLocation(j+1, i);
				}
				if(column.getHeight() != nextColumn.getHeight() - 1 && (nextColumn.getHeight() >= m_externalPoints.get(2).getHeight() && nextColumn.getHeight() <= m_externalPoints.get(3).getHeight()) && (column.getHeight() >= m_externalPoints.get(2).getHeight() && column.getHeight() <= m_externalPoints.get(3).getHeight()))
					connectVerticalBoundingPoints(column, nextColumn);
				else if((nextColumn.getWidth() >= m_imageCenter.getWidth() && nextColumn.getWidth() <= m_externalPoints.get(1).getWidth()) && (column.getWidth() >= m_imageCenter.getWidth() && column.getWidth() <= m_externalPoints.get(1).getWidth()))
					connectHorizontalBoundingPoints(j, column, nextColumn);	
				column = nextColumn;
			}
		}		
	}
	
	private void connectHorizontalBoundingPoints(int y_current, MyPoint current, MyPoint next) {
		if(current.getWidth() < next.getWidth()) 
			for(int i = current.getWidth() + 1; i < next.getWidth(); i++) 
				m_imageMatrix[y_current][i] = 3;	
		if(current.getWidth() > next.getWidth()) 
			for(int i = current.getWidth() - 1; i > next.getWidth(); i--) 
				m_imageMatrix[y_current][i] = 3;
	}
	
	private void connectVerticalBoundingPoints(MyPoint current, MyPoint next) {
		 if(current.getWidth() > next.getWidth()) {
			for(int j = current.getHeight(); j < next.getHeight(); j++) 
				if(current.getWidth() != next.getWidth()) {
					m_imageMatrix[j][current.getWidth()] = 3;	
					current.decreaseWidth();
				}
				else 
					m_imageMatrix[j][current.getWidth()] = 3;
			}
		if(current.getWidth() < next.getWidth()){
			for(int j = current.getHeight(); j < next.getHeight(); j++)  
				if(current.getWidth() != next.getWidth()) {
					m_imageMatrix[j][current.getWidth()] = 3;
					current.increaseWidth();
				}
				else 
					m_imageMatrix[j][current.getWidth()] = 3;				
		}
	}*/

}
