import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class QuadTree{

    public static int[][] imageTo1DArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] imageArray = new int[height][width];
        WritableRaster raster = image.getRaster();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                imageArray[i][j] = raster.getSample(j, i, 0); //Get grayscale pixel value.
            }
        }
        return imageArray;
    }

    public QuadTree(int [][] image){

    }

    public static void main(String[] args) {
        try {
            // Load the image (replace with your image path)
            BufferedImage image = ImageIO.read(new File("C:\\Users\\Administrator\\OneDrive\\Desktop\\download1.PNG"));

            // Check for valid image dimensions
            if (image.getWidth() != image.getHeight() || image.getWidth() == 0) {
                throw new IllegalArgumentException("Image must be a square with non-zero dimensions.");
            }
            int[][] imageArray = imageTo1DArray(image);

            // Build the quadtree
            Node root = new Node(0, 0, imageArray.length);
            root.buildQuadTree(imageArray);

            // Create a FileWriter and BufferedWriter
            FileWriter fileWriter = new FileWriter("quadtree_output.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Print the Quadtree to the file
            printQuadTree(root, 0, bufferedWriter);

            // Close the BufferedWriter
            bufferedWriter.close();
            System.out.println("QuadTree written to quadtree_output.txt");

        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }



    //Helper function to print the QuadTree structure (for demonstration purposes).
    static void printQuadTree(Node node, int level, BufferedWriter writer) throws IOException {
        for (int i = 0; i < level; i++) {
            writer.write("  ");
        }
        if (node.children.isEmpty()) {
            writer.write("Leaf: Color = " + node.color + "\n");
        } else {
            writer.write("Node: x=" + node.x + ", y=" + node.y + ", size=" + node.size + ", color=" + node.color + "\n");
            for (Node child : node.children) {
                if (child != null) {
                    printQuadTree(child, level + 1, writer);
                }
            }
        }
    }

}
