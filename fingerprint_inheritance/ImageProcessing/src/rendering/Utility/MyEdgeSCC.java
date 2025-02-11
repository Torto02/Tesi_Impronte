package rendering.Utility;

import rendering.EdgesImages;
import rendering.Utility.json.EdgeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyEdgeSCC {

    public int getId1() {
        return id1;
    }

    public void setId1(int id1) {
        this.id1 = id1;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setCoordinates() throws IOException {
        List<MyEdge> edges = new ArrayList<>();
        edges = EdgeParser.getEdges(FilesName.getInstance().getEdgesFile());

        boolean first_set = false;
        boolean second_set = false;

        for (int i = 0; i < edges.size(); i++) {
            MyEdge temp = edges.get(i);
            if (temp.getId1() == id1 && !first_set) {
                x1 = temp.getX1();
                y1 = temp.getY1();
                first_set = true;
            } else if (temp.getId2() == id1 && !first_set) {
                x1 = temp.getX2();
                y1 = temp.getY2();
                first_set = true;
            }

            if (temp.getId1() == id2 && !second_set) {
                x2 = temp.getX1();
                y2 = temp.getY1();
                second_set = true;
            } else if (temp.getId2() == id2 && !second_set) {
                x2 = temp.getX2();
                y2 = temp.getY2();
                second_set = true;
            }

            if (first_set && second_set) {
                i = edges.size();
            }

        }
    }


    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }


    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }


    private int x1;
    private int y1;
    private int x2;
    private int y2;

    private int id1;
    private int id2;
    private int color;

    //thoose two constructors are probably never used
    public MyEdgeSCC(int id1, int id2, int color) {
        this.id1 = id1;
        this.id2 = id2;
        this.color = color;
        try {
            setCoordinates();
        } catch (Exception e) {
            System.out.println("File Edges not found");
            System.exit(-1);
        }
    }
}

