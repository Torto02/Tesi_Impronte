package rendering;

import rendering.Utility.FilesName;
import rendering.Utility.Minutia;
import rendering.Utility.MyEdge;
import rendering.Utility.MyPoint;
import rendering.Utility.json.EdgeParser;
import rendering.Utility.json.MinutiaeParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringGenerator {

    private int m_imageHeight;
    private int m_imageWidth;
    /* [0] Contains Min X --> Left-Most point
     * [1] Contains Max X --> Right-Most point
     * [2] Contains Min Y --> Highest point (TOP because y = 0 on the top of the image)
     * [3] Contains Max Y --> Lowest point (BOTTOM because y = 0 on the top of the image)*/
    private ArrayList<MyPoint> m_externalPoints = new ArrayList<>(4);

    List<Minutia> innerMinutiae = new ArrayList<>();
    List<Minutia> edgeMinutiae = new ArrayList<>();

    List<MyEdge> boundingEdges = new ArrayList<>();
    String fingerprintString = "";
    int[] minutiaIdArray;

    public StringGenerator(int imageHeight, int imageWidth) {

        m_imageHeight = imageHeight;
        m_imageWidth = imageWidth;
        setM_externalPoints();

    }

    private void setM_externalPoints() {

        m_externalPoints.add(0, new MyPoint(0, m_imageWidth));
        m_externalPoints.add(1, new MyPoint(0, 0));
        m_externalPoints.add(2, new MyPoint(m_imageHeight, 0));
        m_externalPoints.add(3, new MyPoint(0, 0));

        String filename = FilesName.getInstance().getBoundedSkeletonData();
        File file = new File(filename);
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(file);

            int content;
            char ch_content;
            int x = 0;
            int y = 0;
            while ((content = fis.read()) != -1) {

                ch_content = (char) content;

                if (ch_content != '0') {
                    for (int i = 0; i < 4; i++) {
                        if (
                                ((x < m_externalPoints.get(i).getX()) && (i == 0)) ||
                                        ((x > m_externalPoints.get(i).getX()) && (i == 1)) ||
                                        ((y < m_externalPoints.get(i).getY()) && (i == 2)) ||
                                        ((y >= m_externalPoints.get(i).getY()) && (i == 3))) {
                            m_externalPoints.get(i).setLocation(y, x);

                        }
                    }
                }

                x = x + 1;
                if (x == m_imageWidth) {
                    x = 0;
                    y = y + 1;
                }

            }
           /* for (int i = 0; i < 4; i++) {
                Graphics graphics = img.getGraphics();
                if (i == 0) {
                    graphics.setColor(red);
                } else if (i == 1) {
                    graphics.setColor(green);
                } else if (i == 2) {
                    graphics.setColor(blue);
                } else {
                    graphics.setColor(orange);
                }
                x = m_externalPoints.get(i).getX() - (8 / 2);
                y = m_externalPoints.get(i).getY() - (8 / 2);
                graphics.drawOval(x, y, 8, 8);
            }*/


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
        System.out.println("hightest point: X:" + m_externalPoints.get(2).getWidth() + " Y: " + m_externalPoints.get(2).getHeight());
        System.out.println("lowest point: X: " + m_externalPoints.get(3).getWidth() + " Y: " + m_externalPoints.get(3).getHeight());
        System.out.println("Width leftmost point: " + m_externalPoints.get(0).getWidth());
        System.out.println("Width rightmost point: " + m_externalPoints.get(1).getWidth());
    }

    public void createStringFromInheritanceData() throws IOException {

        String folderPath = FilesName.getInstance().getFolderPath();

        boundingEdges = EdgeParser.getEdges(folderPath + "inheritance/graph_c++.json");
        innerMinutiae = MinutiaeParser.getMinutiae(folderPath + "inheritance/internal_minutiae.json");
        edgeMinutiae = MinutiaeParser.getMinutiae(folderPath + "inheritance/edge_minutiae.json");


        createFingerprintString();
        printFingerprintOnFile();
        System.out.println("\nThe End :)\n");
    }

    private void printFingerprintOnFile() throws IOException {
        String folderPath = FilesName.getInstance().getFolderPath();
        FileWriter w = new FileWriter(folderPath + "FP-String.txt", false);
        w.write(fingerprintString);
        w.write("\n");
        for (int j : minutiaIdArray) {
            w.write(j + "\n");
        }
        w.flush();
        w.close();
    }

    private void createFingerprintString() {

        List<Minutia> orderedMinutiae = reorderedMinutiae();
        // first array cell contains open brackets, second array cell contains close
        // brackets, third array cell contains asterisk
        creatingForGardenia(orderedMinutiae);
    }

    private void creatingForGardenia(List<Minutia> list) {
    	
    	List<Minutia> s = new ArrayList<Minutia>();
        Minutia actual;
        int result;
        minutiaIdArray = new int[list.size()];
        for (int k = 0; k < list.size(); k++) {
            actual = new Minutia(list.get(k).getX(), list.get(k).getY(), list.get(k).getId());
            minutiaIdArray[k] = actual.getId();
            result = searchEdge(actual,s);
            if (result == 0) {
                fingerprintString += '(';
                //  counter[0]++;
            } else if (result == 1) {
                fingerprintString += ')';
                //   counter[1]++;
            } else if (result == 2) {
                fingerprintString += '.';
                //   counter[2]++;
            } else {
                System.out.println("Minuzia non prevista\n");
            }
        }
    }

    private int searchEdge(Minutia actual, List<Minutia> s) {
		if(!s.isEmpty()) {
			for(Minutia a : s) {
				if(a.getId()==actual.getId()) {
					s.remove(a);
					return 1;
				}
			}
		}
		MyEdge tmp;

        for (MyEdge boundingEdge : boundingEdges) {

            tmp = boundingEdge;

            if (tmp.getX1() == actual.getX() && tmp.getY1() == actual.getY()) {
                if (!tmp.getVisitedArc()) {
                	boundingEdge.setVisited(true);
                    if (searchMinutia(innerMinutiae,
                            new Minutia(boundingEdge.getX2(), boundingEdge.getY2()))) {
                        return 2;
                    }
                    s.add(new Minutia(boundingEdge.getX2(),boundingEdge.getY2(),boundingEdge.getId2()));
                    return 0;
                }
            }
            if (tmp.getX2() == actual.getX() && tmp.getY2() == actual.getY()) {
                if (!tmp.getVisitedArc()) {
                    boundingEdge.setVisited(true);
                    if (searchMinutia(innerMinutiae,
                            new Minutia(boundingEdge.getX1(), boundingEdge.getY1()))) {
                        return 2;
                    }
                    s.add(new Minutia(boundingEdge.getX1(),boundingEdge.getY1(),boundingEdge.getId1()));
                    return 0;
                }
            }
        }
        return -1;
	}

	private int searchEdge(Minutia a) {

        MyEdge tmp;

        for (MyEdge boundingEdge : boundingEdges) {

            tmp = boundingEdge;

            if (tmp.getX1() == a.getX() && tmp.getY1() == a.getY()) {
                if (!tmp.getVisitedM1()) {
                    boundingEdge.setVisitedM1(true);
                    if (searchMinutia(innerMinutiae,
                            new Minutia(boundingEdge.getX2(), boundingEdge.getY2()))) {
                        boundingEdge.setVisitedM2(true);
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
                    boundingEdge.setVisitedM2(true);
                    if (searchMinutia(innerMinutiae,
                            new Minutia(boundingEdge.getX1(), boundingEdge.getY1()))) {
                        boundingEdge.setVisitedM1(true);
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

    private List<Minutia> reorderedMinutiae() {
        List<Minutia> orderedMinutiae = new ArrayList<>();
        List<Minutia> app = new ArrayList<>();

        for (MyEdge item : boundingEdges) {

            if (isLeftPart(item.getX1(),item.getY1()))
                orderedMinutiae.add(new Minutia(item.getX1(), item.getY1(), item.getId1()));
            else
                app.add(new Minutia(item.getX1(), item.getY1(), item.getId1()));
            if (!searchMinutia(innerMinutiae, new Minutia(item.getX2(), item.getY2()))) {
                if (isLeftPart(item.getX2(),item.getY2()))
                    orderedMinutiae.add(new Minutia(item.getX2(), item.getY2(), item.getId2()));
                else
                    app.add(new Minutia(item.getX2(), item.getY2(), item.getId2()));
            }
        }


        Collections.sort(orderedMinutiae);
        Collections.sort(app);
        Collections.reverse(app);
        orderedMinutiae.addAll(app);
        return orderedMinutiae;

    }

    private boolean isLeftPart(int minX) {

        return (minX <= m_externalPoints.get(2).getWidth() && minX <= m_externalPoints.get(3).getWidth());
    }
    
    private boolean isLeftPart(int x, int y) {
    	float x1 = m_externalPoints.get(2).getWidth();
    	float y1 = m_externalPoints.get(2).getHeight();
    	float x2 = m_externalPoints.get(3).getWidth();
    	float y2 = m_externalPoints.get(3).getHeight();
    	float m = ((y2-y1)/(x2-x1));
    	float q = (y1-(x1*m));
    	float result =((m*x + q)-y);
    	//int result = ((((y2-y1)/(x2-x1))*x) + (y1-(x1*((y2-y1)/(x2-x1)))))-y;
    	return result > 0;
    }

    private boolean searchMinutia(List<Minutia> list, Minutia minutia) {
        if (minutia == null)
            return false;

        for (Minutia m : list)
            if (minutia.getX() == m.getX() && minutia.getY() == m.getY())
                return true;
        return false;
    }
}