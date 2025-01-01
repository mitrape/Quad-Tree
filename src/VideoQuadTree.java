import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;

public class VideoQuadTree {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); } //لود میکنه اوپن سی وی

    public static void main(String[] args) {
        String videoPath = "D:\\programming projects\\QuadTree\\dataSet\\vid1.mov";

        VideoCapture capture = new VideoCapture(videoPath);
        if (!capture.isOpened()) {
            System.err.println("Error: Cannot open video file.");
            return;
        }

        double frameCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT);
        int frameRate = (int) capture.get(Videoio.CAP_PROP_FPS);

        Mat frame = new Mat();
        int frameNumber = 0;

        while (capture.read(frame)) {
            frameNumber++;
            System.out.println("Processing frame " + frameNumber + " of " + (int)frameCount);

            Mat grayFrame = new Mat();
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);//کانورت میکنه به گری اسکیل

            int[][] pixelArray = convertMatToPixelArray(grayFrame);
            QuadTree quadTree = new QuadTree(pixelArray);
            int[][] compressedPixels = quadTree.compress(256, pixelArray);

            Mat compressedFrame = convertPixelArrayToMat(compressedPixels);
            HighGui.imshow("Compressed Video", compressedFrame); // نشون میده فریم رو

            if (HighGui.waitKey(1000 / frameRate) >= 0) {
                break;
            }
        }

        capture.release();//اون منبعی که ویدیو رو میخوند رو میبنده
        HighGui.destroyAllWindows();//تمام فایل های اوپن سی وی که بازن رو میبنده
    }

    private static int[][] convertMatToPixelArray(Mat mat) {
        int rows = mat.rows();
        int cols = mat.cols();
        int[][] pixelArray = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                pixelArray[i][j] = (int) mat.get(i, j)[0];
            }
        }
        return pixelArray;
    }

    private static Mat convertPixelArrayToMat(int[][] pixelArray) {
        int rows = pixelArray.length;
        int cols = pixelArray[0].length;
        Mat mat = new Mat(rows, cols, CvType.CV_8UC1);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mat.put(i, j, pixelArray[i][j]);
            }
        }
        return mat;
    }
}
