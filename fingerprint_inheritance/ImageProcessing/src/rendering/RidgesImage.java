package rendering;

import rendering.Utility.FilesName;
import rendering.Utility.MyEdgeSCC;
import rendering.Utility.MyPoint;
import rendering.Utility.Ridge;
import rendering.Utility.json.RidgesParser;
import rendering.Utility.Triangle;
import rendering.Utility.json.TrianglesParser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RidgesImage {

    public static void drawColoredRidges(BufferedImage img) throws IOException {

        List<Ridge> ridges = new ArrayList<>();
        ridges = RidgesParser.getRidges(FilesName.getInstance().getRidgesFile());

        for (int i = 0; i < ridges.size(); i++) {
            Ridge temp = ridges.get(i);

            drawRidgeColor(img, temp.getPixels(), temp.getColorCode(), temp.getTotalColoredRidges());
        }
    }

    public static void drawRidgeColor(BufferedImage img, List<MyPoint> pixels, int colorId, int totalRidges) {

        Color color;

        //Start Color = (64, 128, 192)
        //End Color =  (255, 255, 255)
        //Difference = (191, 127, 63)

      /*  int startR = 255;
        int startG = 0;
        int startB = 0;

        int endR = 0;
        int endG = 255;
        int endB = 255;

        int thisR = computeNextColorComponent(startR, endR, colorId, totalRidges);
        int thisG = computeNextColorComponent(startG, endG, colorId, totalRidges);
        int thisB = computeNextColorComponent(startB, endB, colorId, totalRidges);

        color = new Color(thisR, thisG, thisB);*/



        if (colorId == 1) {
            color = Color.CYAN;
        } else if (colorId == 2) {
            color = Color.YELLOW;
        } else if (colorId == 3) {
            color = Color.GRAY;
        } else if (colorId == 4) {
            color = Color.GREEN;
        } else if (colorId == 5) {
            color = Color.ORANGE;
        } else if (colorId == 6) {
            color = Color.BLUE;
        } else if (colorId == 7) {
            color = Color.PINK;
        } else if (colorId == 8) {
            color = Color.MAGENTA;
        } else if (colorId == 9) {
            color = Color.DARK_GRAY;
        }
        else if (colorId == 10) {
            color = Color.LIGHT_GRAY;
        }
        else if (colorId == 11) {
            color = Color.BLACK;
        }
        else {
            color = Color.RED;
        }

        if(colorId!=0) {

            System.out.println("Id Color is: " + colorId);


        Graphics graphics = img.getGraphics();
        graphics.setColor(color);
            for (int i = 0; i < pixels.size(); ++i) {
                graphics.drawRect(pixels.get(i).getX(), pixels.get(i).getY(), 2, 2);
            }
        }

    }


    public static int computeNextColorComponent(int ini, int end, int step, int total) {
        int res;
        if (ini < end) {
            res = ini + (((end - ini) / total) * step);
            if (res > 255) {
                res = ini - (((end - ini) / total) * step);
            }
        } else {
            res = computeNextColorComponent(end, ini, step, total);
        }
        return res;
    }


    public static void drawTriangles(BufferedImage img) throws IOException {

        List<Triangle> triangles = new ArrayList<>();
        triangles = TrianglesParser.getTriangles(FilesName.getInstance().getTrianglesFile());

        for (int i = 0; i < triangles.size(); i++) {
            Triangle temp = triangles.get(i);

            drawTriangle(img, temp.getPoints());
        }
    }

    public static void drawTriangle(BufferedImage img, List<MyPoint> points) {
        Graphics graphics = img.getGraphics();
        // 65535 (-1 convertito in int) è il valore di errore delle coordinate del terzo punto e si ottiene quando il punto non è stato trovato
        if (points.get(0).getX() != 65535 && points.get(1).getX() != 65535 && points.get(2).getX() != 65535 &&
            points.get(0).getY() != 65535 && points.get(1).getY() != 65535 && points.get(2).getY() != 65535) {
            int[] points_x = {points.get(0).getX(), points.get(1).getX(), points.get(2).getX()};
            int[] points_y = {points.get(0).getY(), points.get(1).getY(), points.get(2).getY()};
            graphics.drawPolygon(points_x, points_y, 3);
        }
    }
}
