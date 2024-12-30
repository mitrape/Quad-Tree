import java.util.LinkedList;
import java.util.List;


class Node {
    int color; // -1 indicates non-leaf node, otherwise stores the pixel value.
    List<Node> children;
    int x, y, size;


    Node(int x, int y, int size) {
        this.color = -1;
        this.children= new LinkedList<>();
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public boolean isLeaf (){
        if(this.children.isEmpty())
            return true;
        else return false;
    }
    void addChild(Node child) {
        children.add(child);
    }

    boolean isHomogeneous(int[][] image) {
        if (size == 1) {
            color = image[y][x];
            return true;
        }
        int firstColor = image[y][x];
        for (int i = y; i < y + size; i++) {
            for (int j = x; j < x + size; j++) {
                if (image[i][j] != firstColor) {
                    return false; // Found a different color
                }
            }
        }
        color = firstColor; // All pixels have the same color
        return true;
    }
    int getNodeDepth(int px, int py, int level) {
        if (size == 1 || children.isEmpty()) {
            return level;
        }
        int halfSize = size / 2;
        if (px < x + halfSize) {
            if (py < y + halfSize) {
                return children.get(0).getNodeDepth(px, py, level + 1); // NW
            } else {
                return children.get(2).getNodeDepth(px, py, level + 1); // SW
            }
        } else {
            if (py < y + halfSize) {
                return children.get(1).getNodeDepth(px, py, level + 1); // NE
            } else {
                return children.get(3).getNodeDepth(px, py, level + 1); // SE
            }
        }
    }

    void buildQuadTree(int[][] image){
        if (isHomogeneous(image)) return; //Already homogeneous; don't split further.
        int halfSize = size / 2;
        Node nw = new Node(x, y, halfSize);
        Node ne = new Node(x + halfSize, y, halfSize);
        Node sw = new Node(x, y + halfSize, halfSize);
        Node se = new Node(x + halfSize, y + halfSize, halfSize);
        addChild(nw);
        addChild(ne);
        addChild(sw);
        addChild(se);
        nw.buildQuadTree(image);
        ne.buildQuadTree(image);
        sw.buildQuadTree(image);
        se.buildQuadTree(image);
    }


}



