import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

public class QuadTree{
    public static Node root;
    public int[][] search;

    public int[][] searchSubSpacesWithRange (int x1, int x2, int y1, int y2, int[][] imageArray){
        search = new int[imageArray.length][imageArray.length];
        Node p = root;
        copySubspace(root,x1,y1,x2,y2,imageArray);
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray.length; j++) {
                if(search[i][j] == 0){
                    search[i][j] = 255;
                }
            }
        }
        return search;
    }

    private void copySubspace(Node originalNode, int x1, int y1, int x2, int y2, int[][] imageArray) {
        if(isWithin(originalNode.x, originalNode.y, x1,y1,x2,y2)) {
            if (intersects(originalNode.x, originalNode.y, originalNode.size, x1,y1,x2,y2) ) {

            }
        }
        int halfSize = newNode.size / 2;
        for (int i = 0; i < 4; i++) {
            Node child = new Node(newNode.x + (i % 2) * halfSize, newNode.y + (i / 2) * halfSize, halfSize);
            newNode.addChild(child);
            copySubspace(child, originalNode.children.get(i), x1, y1, x2, y2, imageArray);
        }
    }

    private boolean intersects(int nx, int ny, int nsize, int x1, int y1, int x2, int y2) {
        return !(nx > x2 || ny > y2 || nx + nsize < x1 || ny + nsize < y1);
    }

    private boolean isWithin(int x, int y, int x1, int y1, int x2, int y2) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    public static BufferedImage createImage(int[][] pixelArray) {
        int width = pixelArray.length;
        int height = pixelArray[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int value = pixelArray[x][y];
                int gray = (value << 16) | (value << 8) | value;
                image.setRGB(y, x, gray);
            }
        }
        return image;
    }

    // Method to save the image to a file
    public static void saveImage(BufferedImage image, String filePath) {
        try {
            File outputfile = new File(filePath);
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            System.err.println("Error saving the image: " + e.getMessage());
        }
    }

    public static int[][] createPixelArray(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String pixelCountLine = br.readLine();
        String pixelColorsLine = br.readLine();
        br.close();

        if (pixelCountLine == null || pixelColorsLine == null) {
            throw new IOException("Invalid file format");
        }

        String[] pixelCountStr = pixelCountLine.split(",");
        String[] pixelColorsStr = pixelColorsLine.split(",");

        int pixelCount = pixelCountStr.length;
        int size = (int) Math.sqrt(pixelCount);

        if (size * size != pixelCount) {
            throw new IllegalArgumentException("Pixel count is not a perfect square");
        }

        int[][] imageArray = new int[size][size];

        int[] pixelColors = new int[pixelColorsStr.length];
        for (int i = 0; i < pixelColorsStr.length; i++) {
            pixelColors[i] = Integer.parseInt(pixelColorsStr[i].trim());
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                imageArray[i][j] = pixelColors[i * size + j];
            }
        }

        return imageArray;
    }
    public int getDepth(Node node) {
        if (node.children.isEmpty()) {
            return 1;
        }
        int maxDepth = 0;
        for (Node child : node.children) {
            if (child != null) {
                int childDepth = getDepth(child);
                if (childDepth > maxDepth) {
                    maxDepth = childDepth;
                }
            }
        }
        return 1 + maxDepth;
    }

    public QuadTree (int[][] imageArray){
        // Build the quadtree
        root = new Node(0, 0, imageArray.length);
        root.buildQuadTree(imageArray);
    }
    public int TreeDepth() {
        if (root != null) {
            return getDepth(root);
        }
        return 0;
    }

    public int pixelDepth(int px, int py) {
        if (root == null) {
            throw new IllegalStateException("QuadTree has not been built.");
        }
        return root.getNodeDepth(px, py, 1);
    }


    public static void main(String[] args) {
        int[][] imageArray ;
        try {
            imageArray = createPixelArray("/Users/melikadehestani/Desktop/uni/data structure/final project/project/dataSet/image1_gray.csv");
            QuadTree quadTree = new QuadTree(imageArray);
            BufferedImage image = createImage(imageArray);
            saveImage(image, "output_image.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
