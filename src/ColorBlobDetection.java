import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class ColorBlobDetection {
	private static ColorBlobDetector colorBlobDetector;
	// private static Scalar blobColorRgb = new Scalar(255, 255, 255, 255);
	private static Scalar blobColorHsv;
	private static Scalar CONTOUR_COLOR = new Scalar(0, 0, 255, 255);
	private static String imgPath = "C:/Users/Nikhil/Documents/politicalMap.png";
	private static String savePath = "C:/Users/Nikhil/Documents/contoured.png";
	
	public static Scalar convertScalarRgba2Hsv(Scalar rgbColor) {
        Mat pointMatHsv = new Mat();
        Mat pointMatRgba = new Mat(1, 1, CvType.CV_8UC3, rgbColor);
        Imgproc.cvtColor(pointMatRgba, pointMatHsv, Imgproc.COLOR_RGB2HSV_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
	
	private static Mat readImage(String filePath) {
		BufferedImage bImage = null;
		try {
			bImage = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Mat mat = new Mat(bImage.getHeight(), bImage.getWidth(), CvType.CV_8UC3);
		byte[] pixels = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, pixels);
		
		return mat;
	}
	
	private static Mat drawContours(Mat image) {
		colorBlobDetector = new ColorBlobDetector();
		
		// blobColorHsv = convertScalarRgba2Hsv(blobColorRgb);
		colorBlobDetector.setHsvColor(blobColorHsv);
		
		colorBlobDetector.process(image);
        // mBlackDetector.process(mRgba);
        List<MatOfPoint> contours = colorBlobDetector.getContours();
        System.out.println("NumberOfContours: " + contours.size());
        // List<MatOfPoint> blackContours = mBlackDetector.getContours();
        // Log.e(TAG, "Contours count: " + contours.size());
        Imgproc.drawContours(image, contours, -1, CONTOUR_COLOR);
        
        return image;
	}
	
	public static void getContouredImage(String imgPath, String savePath) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat contouredImage = drawContours(readImage(imgPath));
		Highgui.imwrite(savePath, contouredImage);
	}
	
	public static void onTouch(int x, int y) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = readImage(imgPath);
		
		Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < image.cols()) ? x + 4 - touchedRect.x : image.cols() - touchedRect.x;
        touchedRect.height = (y+4 < image.rows()) ? y + 4 - touchedRect.y : image.rows() - touchedRect.y;

        System.out.println("touchedRect1: " + touchedRect.x + " " + touchedRect.y);
        System.out.println("touchedRect2: " + touchedRect.width + " " + touchedRect.height);
        
        Mat touchedRegionRgba = image.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        
        blobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < blobColorHsv.val.length; i++) {
        	blobColorHsv.val[i] /= pointCount;
        }
        
        System.out.println("hsvColor: " + blobColorHsv);
        
        Mat contouredImage = drawContours(image);
		Highgui.imwrite(savePath, contouredImage);
		
		return;
	}
}
