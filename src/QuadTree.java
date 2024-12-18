import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class QuadTree{
    public static Node root;

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

    public static void main(String[] args) {
        try {
            // Load the image (replace with your image path)
            BufferedImage image = ImageIO.read(new File("C:\\Users\\Administrator\\OneDrive\\Desktop\\download1.PNG"));

            // Check for valid image dimensions
            if (image.getWidth() != image.getHeight() || image.getWidth() == 0) {
                throw new IllegalArgumentException("Image must be a square with non-zero dimensions.");
            }
            int[][] imageArray = imageTo1DArray(image);
            QuadTree quadTree = new QuadTree(imageArray);


        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }


}
