import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.*;



public class QuadTree {
    public static Node root;
    public int[][] search;

    public QuadTree(int[][] imageArray) {
        root = new Node(0, 0, imageArray.length);
        root.buildQuadTree(imageArray);
    }


    public int TreeDepth() {
        if (root != null) {
            return getDepth(root);
        }
        return 0;
    }
    public int getDepth(Node node) {
        if (node.children.isEmpty()) {
            return 1;
        }
        int maxDepth = 0;
        for (int i = 0; i < 4; i++) {
            Node child = node.children.get(i);
            if (child != null) {
                int childDepth = getDepth(child);
                if (childDepth > maxDepth) {
                    maxDepth = childDepth;
                }
            }
        }
        return 1 + maxDepth;
    }



    public int pixelDepth(int px, int py) {
        if (root == null) {
            throw new IllegalStateException("QuadTree has not been built.");
        }
        return root.getNodeDepth(px, py, 1);
    }




    public int[][] searchSubSpacesWithRange(int x1, int x2, int y1, int y2, int[][] imageArray) {
        this.search = new int[imageArray.length][imageArray[0].length];
        findSubSpaces(root, x1, x2, y1, y2, imageArray);
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                if (this.search[i][j] == 0) {
                    this.search[i][j] = 255;
                }
            }
        }
        return this.search;
    }

    private void findSubSpaces(Node originalNode, int x1, int x2, int y1, int y2, int[][] imageArray) {
        if (originalNode == null)
            return;
        if (intersects(originalNode.x, originalNode.y, originalNode.size, x1, y1, x2, y2)) {
            if (isWithin(originalNode.x, originalNode.y, originalNode.size, x1, y1, x2, y2)) {
                for (int i = originalNode.y; i < originalNode.y + originalNode.size; i++) {
                    for (int j = originalNode.x; j < originalNode.x + originalNode.size; j++) {
                        this.search[i][j] = imageArray[i][j];
                    }
                }
            }
            else if (originalNode.isLeaf()) {
                for (int i = originalNode.y; i < originalNode.y + originalNode.size; i++) {
                    for (int j = originalNode.x; j < originalNode.x + originalNode.size; j++) {
                        this.search[i][j] = imageArray[i][j];
                    }
                }
            }
            else {
                for (int i = 0; i < 4; i++) {
                    findSubSpaces(originalNode.children.get(i), x1, x2, y1, y2, imageArray);
                }
            }
        }

    }

    private boolean isWithin(int nx, int ny, int nsize, int x1, int y1, int x2, int y2) {
        return nx >= x1 && nx <= x2 && ny >= y1 && ny <= y2 && nx+nsize >= x1 && nx+nsize <= x2 && ny+nsize >= y1 && ny+nsize <= y2 ;
    }

    private boolean intersects(int nx, int ny, int nsize, int x1, int y1, int x2, int y2) {
        int nodeEndX = nx + nsize;
        int nodeEndY = ny + nsize;
        return !(nodeEndX <= x1 || nx >= x2 || nodeEndY <= y1 || ny >= y2);
    }


    public int[][] mask(int x1, int x2, int y1, int y2, int[][] imageArray) {
        this.search = new int[imageArray.length][imageArray[0].length];
        findSubspaceForMask(root, x1, x2, y1, y2, imageArray);
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                if (this.search[i][j] == 0) {
                    this.search[i][j] = imageArray[i][j];
                }
            }
        }
        return this.search;
    }
    private void findSubspaceForMask(Node originalNode, int x1, int x2, int y1, int y2, int[][] imageArray) {
        if (originalNode == null) return;
        if (intersects(originalNode.x, originalNode.y, originalNode.size, x1, y1, x2, y2)) {
            if (isWithin(originalNode.x, originalNode.y, originalNode.size, x1, y1, x2, y2)) {
                for (int i = originalNode.y; i < originalNode.y + originalNode.size; i++) {
                    for (int j = originalNode.x; j < originalNode.x + originalNode.size; j++) {
                        this.search[i][j] = 255;
                    }
                }
            } else if (originalNode.isLeaf()) {
                for (int i = originalNode.y; i < originalNode.y + originalNode.size; i++) {
                    for (int j = originalNode.x; j < originalNode.x + originalNode.size; j++) {
                        this.search[i][j] = 255;
                    }
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    findSubspaceForMask(originalNode.children.get(i), x1, x2, y1, y2, imageArray);
                }
            }
        }
    }




    public int[][] compress(int newSize, int[][] imageArray) {
        int oldSize = root.size;
        int subspaceSize = oldSize / newSize;
        int[][] compressedImage = new int[newSize][newSize];
        for (int i = 0; i < newSize; i++) {
            for (int j = 0; j < newSize; j++) {
                compressedImage[i][j] = getAverageColor(root, j * subspaceSize, i * subspaceSize, subspaceSize, imageArray);
            }
        }
        return compressedImage;
    }

    private int getAverageColor(Node node, int x, int y, int size, int[][] imageArray) {
        if (node == null) return 0;
        if (node.size == size) {
            return getNodeAverageColor(node, imageArray);
        }
        else if (node.isLeaf()) {
            return node.color;
        }
        else {
            int sum = 0;
            int count = 0;
            for (int i = 0 ; i < 4 ; i++) {
                Node child = node.children.get(i);
                if (child != null && intersects(child.x, child.y, child.size, x, y, x + size, y + size)) {
                    sum += getAverageColor(child, x, y, size, imageArray);
                    count++;
                }
            }
            return count == 0 ? 0 : sum / count;
        }
    }
    private int getNodeAverageColor(Node node, int [][] imageArray) {
        int sum = 0;
        for (int i = node.y; i < node.y + node.size; i++) {
            for (int j = node.x; j < node.x + node.size; j++) {
                sum += imageArray[i][j];
            }
        }
        return sum / (node.size * node.size);
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

    public static BufferedImage createImage(int[][] pixelArray) {
        int height = pixelArray.length;
        int width = pixelArray[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = pixelArray[y][x];
                int gray = (value << 16) | (value << 8) | value;
                image.setRGB(x, y, gray);
            }
        }
        return image;
    }

    public static void saveImage(BufferedImage image, String filePath) {
        try {
            File outputfile = new File(filePath);
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            System.err.println("Error saving the image: " + e.getMessage());
        }
    }





    public void printAndSaveQuadTree(Node node, PrintWriter writer, String prefix) {
        if (node == null) return;
        writer.println(prefix + "Node: x=" + node.x + ", y=" + node.y + ", size=" + node.size + ", color=" + node.color);
        for (int i = 0; i < node.children.size(); i++) {
            printAndSaveQuadTree(node.children.get(i), writer, prefix + "    ");
        }
    }

    public void printAndSaveQuadTree(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            printAndSaveQuadTree(root, writer, "");
        } catch (IOException e) {
            System.err.println("Error saving the quadtree to a file: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        int[][] imageArray;
        try {
            imageArray = createPixelArray("D:\\programming projects\\QuadTree\\dataSet\\test.csv");

            QuadTree quadTree = new QuadTree(imageArray);
            BufferedImage image = createImage(quadTree.compress(2,imageArray));
            saveImage(image,"output.png");

            QuadTree search = new QuadTree(quadTree.searchSubSpacesWithRange(0,2,0,2,imageArray));
            search.printAndSaveQuadTree("quadtree.csv");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}