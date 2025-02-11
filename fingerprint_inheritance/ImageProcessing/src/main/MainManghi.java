package main;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

import rendering.BoundingBox;

import static rendering.BinaryImages.render;

public class MainManghi {
    static int x = 393, y = 514;
    //x = width and y = height
    static int[][] imageMatrix = new int[y][x];
    static int[] vertices = new int[8];
    static BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    static BufferedImage image = new BufferedImage(y, x, BufferedImage.TYPE_INT_ARGB_PRE);
    static int cutPixel = 10;

    public static void main(String[] args) throws NumberFormatException, IOException {

        String pathName = "./fp_files/";
        String fingerprintName = "012_3_6";
        String fileInName = "/059-valleys-thinned-skeleton.dat";

        int black = new Color(0, 0, 0).getRGB();
        render(y, x, pathName + fingerprintName + fileInName, black);
        detectImperfection();
        createIrregularBoundingBox(cutPixel);
        findBoundingPoints();
        outputMatrixOnAFile(pathName+fingerprintName+"/matrixOf_" + fingerprintName);

        renderResult(fingerprintName, pathName);
    }

    static void detectImperfection() {
        int[] fingerprintDimensions = discoverFirstContactPoint();
        int halfHeight = (fingerprintDimensions[3] - fingerprintDimensions[2]) / 2, halfWidth = (fingerprintDimensions[1] - fingerprintDimensions[0]) / 2;
        int x1 = x, x2 = x, range = 16;
        boolean exclusive = false;

        //first quarter
        for (int j = fingerprintDimensions[2]; j < halfHeight + fingerprintDimensions[2]; j++) {
            for (int i = fingerprintDimensions[0]; i < halfWidth + fingerprintDimensions[0]; i++) {
                if (imageMatrix[j][i] == 1 && imageMatrix[j][i + 1] == 0 && x1 == x) {
                    x1 = i;
                    exclusive = true;
                }
                if (imageMatrix[j][i] == 1 && imageMatrix[j][i + 1] == 0 && x2 == x && !exclusive)
                    x2 = i;
                exclusive = false;
                if (x1 != x && x2 != x)
                    if (x1 < x2 && x1 > x2 - range) {
                        imageMatrix[j][x1] = 0;
                        deleteImperfection(j, x1, 1);
                        x2 = x1;
                        x1 = x;
                        break;                           //		+-----+-----+
                    }                                    //		|  1  |  2	|
            }                                            //		+-----+-----+
            x1 = x2 = x;                                //		|  3  |  4  |
        }                                                //		+-----+-----+
        //second quarter
        for (int j = fingerprintDimensions[2]; j < halfHeight + fingerprintDimensions[2]; j++) {
            for (int i = fingerprintDimensions[1]; i > fingerprintDimensions[0] + halfWidth; i--) {
                if (imageMatrix[j][i] == 1 && imageMatrix[j][i - 1] == 0 && x1 == 0) {
                    x1 = i;
                    exclusive = true;
                }
                if (imageMatrix[j][i] == 1 && imageMatrix[j][i - 1] == 0 && x2 == 0 && !exclusive)
                    x2 = i;
                exclusive = false;
                if (x1 != 0 && x2 != 0)
                    if (x1 > x2 && x1 < x2 + range) {
                        imageMatrix[j][x1] = 0;
                        deleteImperfection(j, x1, 2);
                        x2 = x1;                           //		+-----+-----+
                        x1 = 0;                            //		|  1  |  2	|
                        break;                            //		+-----+-----+
                    }                                     //		|  3  |  4  |
            }                                             //		+-----+-----+
            x1 = x2 = 0;
        }
        //third quarter
        for (int j = fingerprintDimensions[2] + halfHeight; j < fingerprintDimensions[3]; j++) {
            for (int i = fingerprintDimensions[0]; i < fingerprintDimensions[0] + halfWidth; i++) {
                if (imageMatrix[j][i] == 1 && imageMatrix[j][i + 1] == 0 && x1 == x) {
                    x1 = i;
                    exclusive = true;
                }
                if (imageMatrix[j][i] == 1 && imageMatrix[j][i + 1] == 0 && x2 == x && !exclusive)
                    x2 = i;
                exclusive = false;
                if (x1 != x && x2 != x)
                    if (x1 < x2 && x1 > x2 - range) {
                        imageMatrix[j][x1] = 0;
                        deleteImperfection(j, x1, 3);
                        x2 = x1;                     //		+-----+-----+
                        x1 = x;                      //		|  1  |  2	|
                        break;                       //		+-----+-----+
                    }                                //		|  3  |  4  |
            }                                        //		+-----+-----+
            x1 = x2 = x;
        }
        //fourth quarter
        for (int j = fingerprintDimensions[2] + halfHeight; j < fingerprintDimensions[3]; j++) {
            for (int i = fingerprintDimensions[1]; i > fingerprintDimensions[1] - halfWidth; i--) {
                if (imageMatrix[j][i] == 1 && imageMatrix[j][i - 1] == 0 && x1 == 0) {
                    x1 = i;
                    exclusive = true;
                }
                if (imageMatrix[j][i] == 1 && imageMatrix[j][i - 1] == 0 && x2 == 0 && !exclusive)
                    x2 = i;
                exclusive = false;
                if (x1 != 0 && x2 != 0)
                    if (x1 > x2 && x1 < x2 + range) {
                        imageMatrix[j][x1] = 0;
                        deleteImperfection(j, x1, 2);
                        x2 = x1;
                        x1 = 0;
                        break;
                    }
            }
            x1 = x2 = 0;
        }
    }

    static void deleteImperfection(int j, int xf, int quarter) {
        if (quarter == 1 || quarter == 3)
            while (imageMatrix[j][xf] != 0) {
                imageMatrix[j][xf] = 5;
                xf--;
            }
        else
            while (imageMatrix[j][xf] != 0) {
                imageMatrix[j][xf] = 5;
                xf++;
            }
    }

    static void createIrregularBoundingBox(int cutPixel) {
        int[] fingerprintDimensions = discoverFirstContactPoint();
        int halfWidth = (fingerprintDimensions[1] - fingerprintDimensions[0]) / 2;
        int previousColumn = 0;
        boolean pointFound = false;

        for (int j = fingerprintDimensions[2]; j <= fingerprintDimensions[3]; j++) {
            for (int i = fingerprintDimensions[0] - 1; i < fingerprintDimensions[0] + halfWidth; i++) {
                if (imageMatrix[j][i] == 1) {
                    previousColumn = i;
                    if (i + cutPixel >= fingerprintDimensions[0] + halfWidth)
                        imageMatrix[j][fingerprintDimensions[0] + halfWidth - 1] = 8;
                    else
                        imageMatrix[j][i + cutPixel] = 8;
                    pointFound = true;
                    break;
                }
            }
            if (!pointFound && previousColumn >= fingerprintDimensions[0])
                imageMatrix[j][previousColumn + cutPixel] = 8;
            pointFound = false;
        }

        for (int j = fingerprintDimensions[2]; j <= fingerprintDimensions[3]; j++) {
            for (int i = fingerprintDimensions[1]; i > halfWidth; i--) {
                if (imageMatrix[j][i] == 1) {
                    previousColumn = i;
                    if (i - cutPixel <= fingerprintDimensions[0] + halfWidth)
                        imageMatrix[j][fingerprintDimensions[0] + halfWidth + 1] = 8;
                    else
                        imageMatrix[j][i - cutPixel] = 8;
                    pointFound = true;
                    break;
                }

            }
            if (!pointFound && previousColumn <= fingerprintDimensions[1])
                imageMatrix[j][previousColumn - cutPixel] = 8;
            pointFound = false;
        }
    }

    static void findBoundingPoints() {
        int[] fingerprintDimensions = discoverFirstContactPoint();
        int halfWidth = (fingerprintDimensions[1] - fingerprintDimensions[0]) / 2;
        int column = 0, nextColumn = 0;

        for (int j = fingerprintDimensions[2]; j <= fingerprintDimensions[3]; j++) {
            for (int i = fingerprintDimensions[0]; i < fingerprintDimensions[0] + halfWidth; i++) {
                if (imageMatrix[j][i] == 8)
                    column = i;
                if (imageMatrix[j + 1][i] == 8)
                    nextColumn = i;
                if (column != nextColumn && (nextColumn >= fingerprintDimensions[0] && nextColumn <= fingerprintDimensions[1]) && (column >= fingerprintDimensions[0] && column <= fingerprintDimensions[1]))
                    connectBoundingPoints(j, column, nextColumn);
                column = nextColumn;
            }
        }

        column = nextColumn = 0;
        for (int j = fingerprintDimensions[2]; j < fingerprintDimensions[3]; j++) {
            for (int i = fingerprintDimensions[1]; i > fingerprintDimensions[0] + halfWidth; i--) {
                if (imageMatrix[j][i] == 8)
                    column = i;
                if (imageMatrix[j + 1][i] == 8)
                    nextColumn = i;
                if (column != nextColumn && (nextColumn >= fingerprintDimensions[0] && nextColumn <= fingerprintDimensions[1]) && (column >= fingerprintDimensions[0] && column <= fingerprintDimensions[1]))
                    connectBoundingPoints(j, column, nextColumn);
                column = nextColumn;
            }
        }
    }

    static void connectBoundingPoints(int y_current, int x_current, int x_next) {
        if (x_current < x_next)
            for (int i = x_current; i < x_next; i++)
                imageMatrix[y_current][i] = 8;
        if (x_current > x_next)
            for (int i = x_current; i > x_next; i--)
                imageMatrix[y_current][i] = 8;
    }

    static int[] discoverFirstContactPoint() {
        int[] firstContactPoint = new int[4];

        //searching first contact point: on the left
        int externalPoint = 504;
        for (int j = 0; j < y; j++)
            for (int i = 0; i <= x - 1; i++)
                if (imageMatrix[j][i] == 1 && i < externalPoint)
                    externalPoint = i;
        firstContactPoint[0] = externalPoint;

        //searching first contact point: on the right
        externalPoint = 0;
        for (int j = 0; j < y; j++)
            for (int i = x - 1; i >= 0; i--)
                if (imageMatrix[j][i] == 1 && i > externalPoint)
                    externalPoint = i;
        firstContactPoint[1] = externalPoint;

        //searching first contact point: on the top
        externalPoint = 504;
        for (int j = 0; j <= y / 2; j++)
            for (int i = 0; i < x; i++)
                if (imageMatrix[j][i] == 1 && j < externalPoint)
                    externalPoint = j;
        firstContactPoint[2] = externalPoint;

        //searching first contact point: on the bottom
        externalPoint = 0;
        for (int j = y - 1; j > 0; j--)
            for (int i = 0; i < x; i++)
                if (imageMatrix[j][i] == 1 && j > externalPoint)
                    externalPoint = j;
        firstContactPoint[3] = externalPoint;
        return firstContactPoint;
    }

    static void render(int height, int width, String filename, int color) throws IOException {

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

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
                    p = color;
                } else {
                    p = 0;
                }

                img.setRGB(x, y, p);

                x = x + 1;
                if (x == width) {
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

        File f = null;

        try {
            String noExtension = filename.substring(0, filename.lastIndexOf('.'));
            f = new File(noExtension + "_img.png");
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        //	String printName = filename.substring(filename.lastIndexOf('\\') + 1, filename.lastIndexOf('.'));
        //System.out.println(printName + " done.");
        copyImageInAMatrix(img);
    }

    static void copyImageInAMatrix(BufferedImage img) {
        for (int j = 0; j < y; j++)
            for (int i = 0; i < x; i++) {
                if (img.getRGB(i, j) == 0)
                    imageMatrix[j][i] = 0;
                else
                    imageMatrix[j][i] = 1;
            }
    }

    @SuppressWarnings("resource")
    static void outputMatrixOnAFile(String fileOutName) throws IOException {
        FileWriter w = new FileWriter(fileOutName + ".txt", true);

        w.write('\n');
        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                w.write(imageMatrix[j][i] + " ");
            }
            w.write("\n");
        }
        w.write('\n');
        w.flush();

    }

    static void createBoundingBox(int cutPixel) {
        //left and right vertical
        for (int j = vertices[0] + cutPixel; j < vertices[2] - cutPixel; j++) {
            imageMatrix[j][vertices[1] + cutPixel] = 1;
            imageMatrix[j][vertices[5] - cutPixel] = 1;
        }
        //up and down horizontal
        for (int i = vertices[1] + cutPixel; i <= vertices[7] - cutPixel; i++) {
            imageMatrix[vertices[0] + cutPixel][i] = 1;
            imageMatrix[vertices[2] - cutPixel][i] = 1;
        }
    }

    static void findIntersection(int cutPixel) {

        //left and right vertical
        for (int j = vertices[0] + cutPixel + 1; j < vertices[2] - cutPixel; j++) {
            if (imageMatrix[j][vertices[1] + cutPixel + 1] == 1)
                imageMatrix[j][vertices[1] + cutPixel] = 2;
            if (imageMatrix[j][vertices[5] - cutPixel - 1] == 1)
                imageMatrix[j][vertices[5] - cutPixel] = 2;

        }
        //up and down horizontal
        for (int i = vertices[1] + cutPixel + 1; i < vertices[7] - cutPixel; i++) {
            if (imageMatrix[vertices[0] + cutPixel + 1][i] == 1)
                imageMatrix[vertices[0] + cutPixel][i] = 2;
            if (imageMatrix[vertices[2] - cutPixel - 1][i] == 1)
                imageMatrix[vertices[2] - cutPixel][i] = 2;
        }
    }

    static void renderResult(String fingerprintName, String pathName) {
        BufferedImage img = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);

        int red = new Color(204, 0, 0).getRGB();
        int black = new Color(0, 0, 0).getRGB();
        int p;
        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                if (imageMatrix[j][i] == 1) {
                    p = black;
                } else if (imageMatrix[j][i] == 0) {
                    p = 0;
                } else {
                    p = red;
                }

                img.setRGB(i, j, p);
            }
        }


        try {
            File f = new File(pathName+fingerprintName+"/"+fingerprintName + "_img.png");
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

    }
}



