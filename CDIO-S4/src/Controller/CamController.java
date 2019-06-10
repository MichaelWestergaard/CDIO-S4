package Controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import DTO.Ball;

public class CamController {

	private static JFrame frame;
    private static JLabel imgCaptureLabelReal;
    private static JLabel imgCaptureLabelMask;
    
    private static Mat img;
    private static int[][] map; 
    
	private static List<Ball> balls = new ArrayList<Ball>();
	private static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	private static List<Ball> triangles = new ArrayList<Ball>();
	
	static JSlider slider = new JSlider(0, 255);
	static JSlider slider1 = new JSlider(0, 255);
	static JSlider slider2 = new JSlider(0, 255);
	static JSlider slider3 = new JSlider(0, 255);
	static JSlider slider4 = new JSlider(0, 255);
	static JSlider slider5 = new JSlider(0, 255);

	static JSlider slider6 = new JSlider(0, 25);
	static JSlider slider7 = new JSlider(0, 25);
	
	static Mat circles;
	static Mat mask;
	
	public CamController() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		img = Imgcodecs.imread("images/2.jpg");
	
	//Imgproc.resize(img, img, new Size(900, 900),0, 0, Imgproc.INTER_AREA);
			
	frame = new JFrame("Video");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    slider.setValue(0);
    slider1.setValue(0);
    slider2.setValue(255);
    slider3.setValue(180);
    slider4.setValue(0);
    slider5.setValue(0);
    slider6.setValue(10);
    slider7.setValue(15);
	
    findWalls();
    //findWallsVirkerIKorrektBelysning();
    /*gg();
    
	JPanel framePanel = new JPanel();

    imgCaptureLabelReal = new JLabel(new ImageIcon(HighGui.toBufferedImage(circles)));
    framePanel.add(imgCaptureLabelReal);
    imgCaptureLabelMask = new JLabel(new ImageIcon(HighGui.toBufferedImage(mask)));
    framePanel.add(imgCaptureLabelMask);
    frame.getContentPane().add(framePanel, BorderLayout.CENTER);
    
    JPanel slidersLower = new JPanel();

    slidersLower.add(slider);
    slidersLower.add(slider1);
    slidersLower.add(slider2);
    slidersLower.add(slider3);
    slidersLower.add(slider4);
    slidersLower.add(slider5);
    slidersLower.add(slider6);
    slidersLower.add(slider7);
    
    JButton button = new JButton("Get Settings");
    
    slidersLower.add(button);
    
    JButton buttonCoord = new JButton("Get Coordinates");
    
    slidersLower.add(buttonCoord);
          
    frame.getContentPane().add(slidersLower, BorderLayout.SOUTH);
    
    ChangeListener listener = new ChangeListener() {
		
		@Override
		public void stateChanged(ChangeEvent e) {
			gg();

			imgCaptureLabelReal.setIcon(new ImageIcon(HighGui.toBufferedImage(circles)));
			imgCaptureLabelMask.setIcon(new ImageIcon(HighGui.toBufferedImage(mask)));
            frame.repaint();
		}
	};
	
    slider.addChangeListener(listener);
    slider1.addChangeListener(listener);
    slider2.addChangeListener(listener);
    slider3.addChangeListener(listener);
    slider4.addChangeListener(listener);
    slider5.addChangeListener(listener);
    slider6.addChangeListener(listener);
    slider7.addChangeListener(listener);
    
    button.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Scalar lower = new Scalar(" + slider.getValue() + ", " + slider1.getValue() + ", " + slider2.getValue() + ");");
			System.out.println("Scalar upper = new Scalar(" + slider3.getValue() + ", " + slider4.getValue() + ", " + slider5.getValue() + ");");
			System.out.println("double minArea = Math.PI * (" + slider6.getValue() + " * 0.9f) * (" + slider6.getValue() + " * 0.9f);");
			System.out.println("double maxArea = Math.PI * (" + slider7.getValue() + " * 1.1f) * (" + slider7.getValue() + " * 1.1f);");
		}
	});
    
    buttonCoord.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String output = "static Waypoint[] coordinates = {";
			for (int i = 0; i < balls.size(); i++) {
				String newWaypoint = " new Waypoint(" + balls.get(i).x + ", " + balls.get(i).y + ")";
				if(!output.contains(newWaypoint)) {
					output += newWaypoint;
					if(i+1 != balls.size())
						output += ",";
				}

			}
			output += "};";
			System.out.println(output);
		}
	});

    frame.pack();
    frame.setVisible(true);
    */
	}
	
	private static void gg() {

		mask = createMask();
		circles = img.clone();
        findBalls(mask);
        findRobot(mask);

		
        for (Ball ball : balls) {
        	//System.out.println(ball.x + ", " + ball.y);
			Imgproc.circle(circles, new Point(ball.x, ball.y), 20, new Scalar(0, 0, 255));
			Imgproc.putText(circles, "bold", new Point(ball.x, ball.y-20), 3, 1.5, new Scalar(0, 0, 255));
		}
        
        Imgproc.putText(circles, "Bolde tilbage: " + balls.size(), new Point(circles.width()/3, circles.height()-20), 3, 1, new Scalar(255, 0, 0));
        
        for (Ball triangle : triangles) {
        	//System.out.println("roboto" + triangle.x + ", " + triangle.y);
			Imgproc.circle(circles, new Point(triangle.x, triangle.y), 50, new Scalar(0, 255, 0));
			Imgproc.putText(circles, "Roboto", new Point(triangle.x, triangle.y), 3, 1.5, new Scalar(0, 255, 0));
		}
	}
	
	private static Mat createMask() {
		Mat mask = new Mat();
		
		Imgproc.cvtColor(img, mask, Imgproc.COLOR_BGR2GRAY);
		
		
		//Imgproc.adaptiveThreshold(mask, mask, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 15, 20);
		
		Scalar lower = new Scalar(slider.getValue(), slider1.getValue(), slider2.getValue());
	    Scalar upper = new Scalar(slider3.getValue(), slider4.getValue(), slider5.getValue());
	    
        Core.inRange(mask, lower, upper, mask);
		
		return mask;
	}
	
	private static Mat createMaskWalls() {
		Mat mask = new Mat();
		
		Imgproc.cvtColor(img, mask, Imgproc.COLOR_BGR2HSV);
		
		//Imgproc.adaptiveThreshold(mask, mask, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 15, 20);
		
		Scalar lower = new Scalar(0, 70, 50);
	    Scalar upper = new Scalar(10, 255, 255);
	    
        Core.inRange(mask, lower, upper, mask);
        //Core.inRange(mask, new Scalar(80, 70, 50), new Scalar(100, 255, 255), mask);
		
		return mask;
	}
	
	private static void findRobot(Mat mask) {
		
		triangles.clear();
		
		List<MatOfPoint> contoursRoboto = new ArrayList<MatOfPoint>();
		Mat canny = new Mat();
		
		Imgproc.Canny(mask, canny, 250, 0);
		Imgproc.findContours(canny, contoursRoboto, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		
		/*
		for (int i = 0; i < contoursRoboto.size(); i++) {
			Imgproc.drawContours(circles, contoursRoboto, i, new Scalar(255, 0, 0));
		}
		*/
		
		for (MatOfPoint contour : contoursRoboto) {
		    double contourArea = Imgproc.contourArea(contour);
		    matOfPoint2f.fromList(contour.toList());
		    Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.01, true);
		    long total = approxCurve.total();
		    
		    Point[] center = approxCurve.toArray();
		    
		    if (total == 3) {
		    	triangles.add(new Ball((int)Math.round(center[0].x), (int)Math.round(center[0].y)));
		    }
		}
		
	}
	
	private static void findBalls(Mat mask) {
		Mat canny = new Mat();

		contours.clear();
		balls.clear();
		
		Imgproc.Canny(mask, canny, 250, 750);
		Imgproc.findContours(canny, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		double minArea = Math.PI * (slider6.getValue() * 0.9f) * (slider6.getValue() * 0.9f); // minimal ball area
		double maxArea = Math.PI * (slider7.getValue() * 1.1f) * (slider7.getValue() * 1.1f); // maximal ball area
		

		for (int i = 0; i < contours.size(); i++) {
			
			
			double area = Imgproc.contourArea(contours.get(i));
			
			if (area > minArea) {		
				if (area < maxArea) {
					// we found a ball
					
					float[] radius = new float[1];
					Point center = new Point();
					Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i).toArray()), center, radius);
										
					boolean contains = false;
					
					for (int j = 0; j < balls.size(); j++) {
						if(balls.get(j).checkCoords((int)Math.round(center.x), (int)Math.round(center.y)) && contains == false) {
							contains = true;
							break;
						}
					}
					
					if(contains == false)
						balls.add(new Ball((int)Math.round(center.x), (int)Math.round(center.y)));
				}
			}
		}
		
		System.out.println("Balls found: " + balls.size());
	}
	
	private static void findWallsVirkerIKorrektBelysning() {
		Mat canny = new Mat();
		List<MatOfPoint> contoursWalls = new ArrayList<MatOfPoint>();
		Mat mask = createMaskWalls();
		
		JFrame frame2 = new JFrame("Video");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		Imgproc.Canny(mask, canny, 200, 750);
		Imgproc.findContours(canny, contoursWalls, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		double areaLast = 0;
		Point[] verticesLast = null;
		RotatedRect rectLast = null;
		
		for (int i = 0; i < contoursWalls.size(); i++) {
			
			double area = Imgproc.contourArea(contoursWalls.get(i));
			
			if(area > 0.0) {
				System.out.println(area + " - " + areaLast);
				if(area > areaLast) {
					RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contoursWalls.get(i).toArray()));
					Point[] vertices = new Point[4];  
			        rect.points(vertices);
			        
			        verticesLast = vertices;
			        rectLast = rect;
				}
				areaLast = area;
			}
			
		}
		
		if(verticesLast != null && rectLast != null) {
			for(int j = 0; j < 4; j++) {
				Imgproc.line(img, verticesLast[j], verticesLast[(j+1)%4], new Scalar(0,255,0));
				Imgproc.putText(img, "Corner", verticesLast[j], 2, 0.5, new Scalar(250,250,250));
			}
			Imgproc.putText(img, "wall", new Point(rectLast.center.x, rectLast.center.y), 0, 1.5, new Scalar(0, 255, 0));
		}
				
		//Warp
		
		Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
	    Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);

	    
	    
	    src_mat.put(0, 0, verticesLast[2].x, verticesLast[2].y, verticesLast[3].x, verticesLast[3].y, verticesLast[1].x, verticesLast[1].y, verticesLast[0].x, verticesLast[0].y);
	    dst_mat.put(0, 0, 0.0, 0.0, rectLast.size.height, 0.0, 0.0, rectLast.size.width, rectLast.size.height, rectLast.size.width);
	    Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src_mat, dst_mat);

	    Mat dst = img.clone();

	    Imgproc.warpPerspective(img, dst, perspectiveTransform, new Size(rectLast.size.height, rectLast.size.width));
	    
        JPanel framePanel = new JPanel();

        imgCaptureLabelMask = new JLabel(new ImageIcon(HighGui.toBufferedImage(img)));
        framePanel.add(imgCaptureLabelMask);
        frame2.getContentPane().add(framePanel, BorderLayout.CENTER);
        
        frame2.repaint();
        frame2.pack();
        frame2.setVisible(true);
        
		
	}
	
	private static void findWallsFarveFredag() {
		Mat canny = new Mat();
		List<MatOfPoint> contoursWalls = new ArrayList<MatOfPoint>();
		Mat mask = createMaskWalls();
		
		JFrame frame2 = new JFrame("Video");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		Imgproc.Canny(mask, canny, 200, 750);
		Imgproc.findContours(canny, contoursWalls, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		double areaLast = 0;
		Point[] verticesLast = null;
		RotatedRect rectLast = null;
		
		for (int i = 0; i < contoursWalls.size(); i++) {
			
			double area = Imgproc.contourArea(contoursWalls.get(i));
			MatOfPoint2f temp = new MatOfPoint2f(contoursWalls.get(i).toArray());
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			Imgproc.approxPolyDP(temp, approxCurve, Imgproc.arcLength(temp, true) * 0.02, true);
			
			System.out.println(approxCurve.total());
			
			if(approxCurve.total() == 4 && area > 0.0) {
				System.out.println(area + " - " + areaLast);
				if(area > areaLast) {
					RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contoursWalls.get(i).toArray()));
					Point[] vertices = new Point[4];  
			        rect.points(vertices);
			        
			        verticesLast = vertices;
			        rectLast = rect;
				}
				areaLast = area;
			}
			
		}
		
		if(verticesLast != null && rectLast != null) {
			for(int j = 0; j < 4; j++) {
				Imgproc.line(img, verticesLast[j], verticesLast[(j+1)%4], new Scalar(0,255,0));
				Imgproc.putText(img, "Corner", verticesLast[j], 2, 0.5, new Scalar(250,250,250));
			}
			Imgproc.putText(img, "wall", new Point(rectLast.center.x, rectLast.center.y), 0, 1.5, new Scalar(0, 255, 0));
		}
				
		//Warp
		/*
		Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
	    Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);

	    
	    
	    src_mat.put(0, 0, verticesLast[2].x, verticesLast[2].y, verticesLast[3].x, verticesLast[3].y, verticesLast[1].x, verticesLast[1].y, verticesLast[0].x, verticesLast[0].y);
	    dst_mat.put(0, 0, 0.0, 0.0, rectLast.size.height, 0.0, 0.0, rectLast.size.width, rectLast.size.height, rectLast.size.width);
	    Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src_mat, dst_mat);

	    Mat dst = img.clone();

	    Imgproc.warpPerspective(img, dst, perspectiveTransform, new Size(rectLast.size.height, rectLast.size.width));
	    */
        JPanel framePanel = new JPanel();

        imgCaptureLabelMask = new JLabel(new ImageIcon(HighGui.toBufferedImage(img)));
        framePanel.add(imgCaptureLabelMask);
        frame2.getContentPane().add(framePanel, BorderLayout.CENTER);
        
        frame2.repaint();
        frame2.pack();
        frame2.setVisible(true);
        
		
	}
	
	private static Mat createMaskWallsEdges() {
		Mat mask = new Mat();
		
		Imgproc.cvtColor(img, mask, Imgproc.COLOR_BGR2GRAY);
		
		Imgproc.adaptiveThreshold(mask, mask, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
		
		return mask;
	}
	
	private static void findWalls() {
		
		Mat dst = new Mat();
		Mat edges = new Mat();
		List<MatOfPoint> contoursWalls = new ArrayList<MatOfPoint>();
		
		Imgproc.GaussianBlur(img, dst, new Size(3,3), 0);
		
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(2 * 8 + 1, 2 * 8 + 1), new Point(8, 8));
		
		int lowThresh = 90;

		Imgproc.morphologyEx(dst, dst, Imgproc.MORPH_CLOSE, element);
		Imgproc.Canny(dst, edges, lowThresh, lowThresh*3, 3, true);

		
		Imgproc.findContours(edges, contoursWalls, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

		
		double areaLast = 0;
		double crossArea = 0.0;
		int crossI = 0;
		Point[] verticesLast = null;
		RotatedRect rectLast = null;
		
		for (int i = 0; i < contoursWalls.size(); i++) {
			
			MatOfPoint2f temp = new MatOfPoint2f(contoursWalls.get(i).toArray());
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			Imgproc.approxPolyDP(temp, approxCurve, Imgproc.arcLength(temp, true) * 0.02, true);
			
			if(approxCurve.total() == 12) {
				double crossAreaLocal = Imgproc.contourArea(approxCurve);
				if(crossAreaLocal > crossArea)
					crossI = i;
			}
			
			RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contoursWalls.get(i).toArray()));
			Point[] vertices = new Point[4];  
	        rect.points(vertices);
	        
			double area = rect.size.width * rect.size.height;
			
			if(area > areaLast) {
	        	verticesLast = vertices;
		        rectLast = rect;
				areaLast = area;
			}
		}
		
		if(verticesLast != null && rectLast != null) {
			for(int j = 0; j < 4; j++) {
				Imgproc.line(img, verticesLast[j], verticesLast[(j+1)%4], new Scalar(0,255,0));
				Imgproc.putText(img, "Corner", verticesLast[j], 2, 0.5, new Scalar(250,250,250));
			}
			//Imgproc.putText(img, "wall", new Point(rectLast.center.x, rectLast.center.y), 0, 1.5, new Scalar(0, 255, 0));
		}
		
		if(crossI > 0) {
			
			System.out.println(contoursWalls.get(crossI).rows());
			Imgproc.drawContours(img, contoursWalls, crossI, new Scalar(255,0,0), Imgproc.FILLED);
		}
		
		//Warp
		
		Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
	    Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);

	    src_mat.put(0, 0, verticesLast[2].x, verticesLast[2].y, verticesLast[3].x, verticesLast[3].y, verticesLast[1].x, verticesLast[1].y, verticesLast[0].x, verticesLast[0].y);
	    dst_mat.put(0, 0, 0.0, 0.0, rectLast.size.height, 0.0, 0.0, rectLast.size.width, rectLast.size.height, rectLast.size.width);
	    Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src_mat, dst_mat);

	    Imgproc.warpPerspective(img, img, perspectiveTransform, new Size(rectLast.size.height, rectLast.size.width));

	    mask = createMask();
		circles = img.clone();
        findBalls(mask);
        //findRobot(mask);
        
        Mat imageWithGrid = img.clone();
        
        double gridSizeHorizontal = img.width()/180;
        double gridSizeVertical = img.height()/120;
        
	    for (int i = 0; i < img.width(); i++) {
			for (int j = 0; j < img.height(); j++) {
				Imgproc.line(imageWithGrid, new Point(i*gridSizeHorizontal,j), new Point(i*gridSizeHorizontal, img.height()), new Scalar(0,4, 0));
				Imgproc.line(imageWithGrid, new Point(i,j*gridSizeVertical), new Point(img.width(), j*gridSizeVertical), new Scalar(0,4, 0));
			}
		}
	    
        for (Ball ball : balls) {
			Imgproc.circle(imageWithGrid, new Point(ball.x, ball.y), 20, new Scalar(0, 0, 255));
			Imgproc.putText(imageWithGrid, "bold " + Math.round(ball.x/gridSizeHorizontal) + ", " + Math.round(ball.y/gridSizeVertical), new Point(ball.x, ball.y-20), 3, 1.5, new Scalar(0, 0, 255));
		}
        
        Imgproc.putText(imageWithGrid, "Bolde tilbage: " + balls.size(), new Point(circles.width()/3, circles.height()-20), 3, 1, new Scalar(255, 0, 0));
        		
		showImage(edges);
		showImage(img);
		showImage(imageWithGrid);
        generateMap(imageWithGrid);
	}
	
	public static int[][] getMap(){
		return map;
	}
	
	private static void generateMap(Mat img) {
		double gridSizeHorizontal = img.width()/180;
        double gridSizeVertical = img.height()/120;
        
        map = new int[180][120];
        
        for (int i = 0; i < 180; i++) {
			for (int j = 0; j < 120; j++) {
				map[i][j] = 0;
			}
		}
        
		ArrayList<Ball> notFound = new ArrayList<Ball>();
		
		for(Ball ball : balls) {
			int i = (int) Math.round(ball.x/gridSizeHorizontal);
			int j = (int) Math.round(ball.y/gridSizeVertical);
			
			System.out.println("i: " + i + " j: " + j);
			
			if(i < 3) {
				i = 3;
				System.out.println("Adjusted i to 3");
			}
			if(i > 179) {
				i = 179;
				System.out.println("Adjusted i to 179");
			}
			if(j < 2) {
				j = 2;
				System.out.println("Adjusted j to 2");
			}
			if(j > 118) {
				j = 118;
				System.out.println("Adjusted j to 118");
			}
			map[i-3][j-2] = 1;
			map[i-3][j-1] = 1;
			map[i-3][j] = 1;
			map[i-3][j+1] = 1;
			
			map[i-2][j-2] = 1;
			map[i-2][j-1] = 1;
			map[i-2][j] = 1;
			map[i-2][j+1] = 1;
			
			map[i-1][j-2] = 1;
			map[i-1][j-1] = 1;
			map[i-1][j] = 1;
			map[i-1][j+1] = 1;
			
			map[i][j-2] = 1;
			map[i][j-1] = 1;
			map[i][j] = 1;
			map[i][j+1] = 1;

			/*if(the ball isnt found) {
				notFound.add(ball);
			}*/
		}
		for(Ball ball : notFound) {
			System.out.println("i: " + ball.x/gridSizeHorizontal + " y: " + ball.y/gridSizeVertical);
		}
		/*for (int i = 0; i < 180; i++) {
			for (int j = 0; j < 120; j++) {
				boolean found = false;
				for (Ball ball : balls) {
					if(!found) {
						counter1++;
						ballX = (int) Math.round(ball.x/gridSizeHorizontal);
						ballY = (int) Math.round(ball.y/gridSizeVertical);
						System.out.println("x: " + ballX + "y: " + ballY);
						if(ballX == i && ballY == j) {
							found = true;
						}
					} else {
						break;
					}
				}
				
				if(found) {
					map[i-3][j-2] = 1;
					map[i-3][j-1] = 1;
					map[i-3][j] = 1;
					map[i-3][j+1] = 1;
					
					map[i-2][j-2] = 1;
					map[i-2][j-1] = 1;
					map[i-2][j] = 1;
					map[i-2][j+1] = 1;
					
					map[i-1][j-2] = 1;
					map[i-1][j-1] = 1;
					map[i-1][j] = 1;
					map[i-1][j+1] = 1;
					
					map[i][j-2] = 1;
					map[i][j-1] = 1;
					map[i][j] = 1;
					map[i][j+1] = 1;
					//System.out.println("x: " + ballX + "y: " + ballY);
				}
			}
		}*/
		
		/*
		for (int i = 0; i < 180; i++) {
			for (int j = 0; j < 120; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
		*/
		/*
		System.out.println("int[][] map = new int[][]{");
		for (int i = 0; i < 180; i++) {
			System.out.print("{ ");
			for (int j = 0; j < 120; j++) {
				System.out.print(map[i][j] + "");
				if(j+1 != 120) {
					System.out.print(", ");
				}
			}
			System.out.print(" }");
			if(i+1 != 180) {
				System.out.print(",");
			}
			System.out.println();
		}
		System.out.println("};");*/
		int counter = 0;
		for(int i = 0; i < 180; i++) {
			for(int j = 0; j < 120; j++) {
				if(map[i][j] == 1) {
					counter++;
					//System.out.println("i: " + i + " j: " + j);
				}
			}
		}
		System.out.println("Number of 1's: " + counter + " which is " + counter + "/16 = " + counter/16 + " balls");
	}
	
	private static void showImage(Mat mat) {
		JFrame f = new JFrame();
		f.setTitle(mat + "");
		f.add(new JPanel().add(new JLabel(new ImageIcon(HighGui.toBufferedImage(mat)))));
		f.setSize((int)mat.size().width, (int)mat.size().height+50);
		f.setVisible(true);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static Image toBufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);  
        return image;

    }
	
}

