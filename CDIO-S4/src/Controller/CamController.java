package Controller;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

import DTO.Ball;
import DTO.Camera;
import DTO.Direction;
import DTO.Goal;
import DTO.Obstacles;
import DTO.Robot;

public class CamController {

	private static JFrame frame;
	private JFrame videoFrame;
	
    private JLabel imgCaptureLabel;
    private JLabel imgDetectionLabel;
    private JLabel ballDetectionLabel;
	
    private int[][] map = null;
    
	private static List<Ball> balls = new ArrayList<Ball>();
	private static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	private static List<Ball> triangles = new ArrayList<Ball>();

	static Mat circles;
	Mat imageWithGrid;
	
	private VideoCapture videoCapture;
    private Mat matFrame, realImg;
    private CaptureTask captureTask;
    private Camera cameraSettings;
    private boolean useCam = true;
    private Mat mask, inRange, edges, ballsMask, robotMask;
    private MapController mapController;
    private RouteController routeController = new RouteController();
    private boolean run = false;
    private boolean firstFrame = true;
    private Point topLeft, topRight, bottomLeft, bottomRight;
    private Vector<Point> corners, target;
    private double minXVal, maxXVal, width, minYVal, maxYVal, height;
    private Mat perspectiveTransform;
    private double cameraHeight = 168.8;
    int counter = 0 ;
    double distRobot = 0.0;
    
    private FrameHelper frameHelper = new FrameHelper();
    
    String imagePath = "Images/croosNotBlocking.jpg";
    
    DTO.Point directionPoint;
    Robot robot;
	
	public CamController(boolean useCam) {
		this.useCam = useCam;
		loadSettingsFile();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		matFrame = new Mat();

		if(useCam) {
			videoCapture = new VideoCapture(0);
	        if (!videoCapture.isOpened()) {
	            System.err.println("Cannot open camera");
	            System.exit(0);
	        }
	        if (!videoCapture.read(matFrame)) {
	            System.err.println("Cannot read camera stream.");
	            System.exit(0);
	        }
		} else {
			matFrame = Imgcodecs.imread(imagePath);
		}
	}
	
	private void loadSettingsFile() {
		try {
			File file = new File("camera_settings.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(Camera.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			cameraSettings = (Camera) jaxbUnmarshaller.unmarshal(file);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startUp() {
		videoFrame = new JFrame("Video");
		videoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame = new JFrame("CamController");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JButton recalibrateBtn = new JButton("Kalibrer Kamera");
	    JButton useDefaultBtn = new JButton("Brug Standard");
	    
	    recalibrateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				calibrateCamera();
				frame.setVisible(false);
				frame.dispose();
			}
		});
	    
	    useDefaultBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(useCam) {
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
			            @Override
			            public void run() {
			                captureTask = new CaptureTask();
			                captureTask.execute();
			            }
			        });
				}

				frame.setVisible(false);
				frame.dispose();
				
				Image img = HighGui.toBufferedImage(matFrame);
		        
		        JPanel framePanel = new JPanel();
		        imgCaptureLabel = new JLabel(new ImageIcon(img));
		        framePanel.add(imgCaptureLabel);
		        imgDetectionLabel = new JLabel(new ImageIcon(img));
		        framePanel.add(imgDetectionLabel);
		        ballDetectionLabel = new JLabel(new ImageIcon(img));
		        framePanel.add(ballDetectionLabel);
		        videoFrame.getContentPane().add(framePanel, BorderLayout.CENTER);
				updateFrame();
				videoFrame.pack();
				videoFrame.setVisible(true);
			}
		});
	    
	    JPanel panel = new JPanel();
	    panel.add(recalibrateBtn);
	    panel.add(useDefaultBtn);
	    frame.add(panel, BorderLayout.CENTER);
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
	
	private void calibrateCamera() {
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(matFrame);
        
        JPanel framePanel = new JPanel();
        imgCaptureLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgCaptureLabel);
        imgDetectionLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgDetectionLabel);
        videoFrame.getContentPane().add(framePanel, BorderLayout.CENTER);
        
        JFrame menu = frameHelper.calibrationMenu();
        
        frameHelper.minBallSize.setValue(cameraSettings.getMinBallSize());
        frameHelper.maxBallSize.setValue(cameraSettings.getMaxBallSize());
        frameHelper.lowHueWalls.setValue(cameraSettings.getLowHueWalls());
        frameHelper.maxHueWalls.setValue(cameraSettings.getMaxHueWalls());
        frameHelper.lowSatWalls.setValue(cameraSettings.getLowSatWalls());
        frameHelper.maxSatWalls.setValue(cameraSettings.getMaxSatWalls());
        frameHelper.lowValWalls.setValue(cameraSettings.getLowValWalls());
        frameHelper.maxValWalls.setValue(cameraSettings.getMaxValWalls());
        frameHelper.lowHueBalls.setValue(cameraSettings.getLowHueBalls());
        frameHelper.maxHueBalls.setValue(cameraSettings.getMaxHueBalls());
        frameHelper.lowSatBalls.setValue(cameraSettings.getLowSatBalls());
        frameHelper.maxSatBalls.setValue(cameraSettings.getMaxSatBalls());
        frameHelper.lowValBalls.setValue(cameraSettings.getLowValBalls());
        frameHelper.maxValBalls.setValue(cameraSettings.getMaxValBalls());
        updateFrame();

        ChangeListener changeListener = new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				cameraSettings.setMinBallSize(frameHelper.minBallSize.getValue());
				cameraSettings.setMaxBallSize(frameHelper.maxBallSize.getValue());
				cameraSettings.setLowHueWalls(frameHelper.lowHueWalls.getValue());
				cameraSettings.setMaxHueWalls(frameHelper.maxHueWalls.getValue());
				cameraSettings.setLowSatWalls(frameHelper.lowSatWalls.getValue());
				cameraSettings.setMaxSatWalls(frameHelper.maxSatWalls.getValue());
				cameraSettings.setLowValWalls(frameHelper.lowValWalls.getValue());
				cameraSettings.setMaxValWalls(frameHelper.maxValWalls.getValue());
				cameraSettings.setLowHueBalls(frameHelper.lowHueBalls.getValue());
				cameraSettings.setMaxHueBalls(frameHelper.maxHueBalls.getValue());
				cameraSettings.setLowSatBalls(frameHelper.lowSatBalls.getValue());
				cameraSettings.setMaxSatBalls(frameHelper.maxSatBalls.getValue());
				cameraSettings.setLowValBalls(frameHelper.lowValBalls.getValue());
				cameraSettings.setMaxValBalls(frameHelper.maxValBalls.getValue());
				
				if(!useCam) {
					matFrame = Imgcodecs.imread(imagePath);
				}
				
				updateFrame();
			}
			
		};
		
		frameHelper.setListener(changeListener);
        
        frameHelper.save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Camera camera = new Camera(frameHelper.minBallSize.getValue(), frameHelper.maxBallSize.getValue(), frameHelper.lowHueWalls.getValue(), frameHelper.maxHueWalls.getValue(), frameHelper.lowSatWalls.getValue(), frameHelper.maxSatWalls.getValue(), frameHelper.lowValWalls.getValue(), frameHelper.maxValWalls.getValue(), frameHelper.lowHueBalls.getValue(), frameHelper.maxHueBalls.getValue(), frameHelper.lowSatBalls.getValue(), frameHelper.maxSatBalls.getValue(), frameHelper.lowValBalls.getValue(), frameHelper.maxValBalls.getValue());
				
				try {
					File file = new File("camera_settings.xml");
					JAXBContext jaxbContext = JAXBContext.newInstance(Camera.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

					// output pretty printed
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

					jaxbMarshaller.marshal(camera, file);
					jaxbMarshaller.marshal(camera, System.out);
					run = true;
				} catch (JAXBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
        
        menu.setVisible(true);
        
        videoFrame.pack();
        videoFrame.setLocationRelativeTo(null);
        videoFrame.setVisible(true);
        
		if(useCam) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                captureTask = new CaptureTask();
	                captureTask.execute();
	            }
	        });
		}
	}
	
	private void updateFrame() {
		realImg = matFrame.clone();
		Mat capturedFrame = matFrame.clone();
		mask = new Mat();
		Mat threshold = new Mat();
		edges = new Mat();
		
		Imgproc.blur(capturedFrame, capturedFrame, new Size(7,7));
		
		Imgproc.cvtColor(capturedFrame, mask, Imgproc.COLOR_BGR2HSV);
		
		//Imgproc.adaptiveThreshold(mask, threshold, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

		inRange = new Mat();
		
		Scalar lower = new Scalar(cameraSettings.getLowHueWalls(), cameraSettings.getLowSatWalls(), cameraSettings.getLowValWalls());
	    Scalar upper = new Scalar(cameraSettings.getMaxHueWalls(), cameraSettings.getMaxSatWalls(), cameraSettings.getMaxValWalls());
	    
        Core.inRange(mask, lower, upper, inRange);
                
        int lowThresh = 90;

		Imgproc.Canny(inRange, edges, lowThresh, lowThresh*3, 3, true);
		

		List<MatOfPoint> contoursWalls = new ArrayList<MatOfPoint>();

		Imgproc.findContours(edges, contoursWalls, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

		double areaLast = 0;
		Point[] verticesLast = null;
		RotatedRect rectLast = null;
		
		for (int i = 0; i < contoursWalls.size(); i++) {
			MatOfPoint2f temp = new MatOfPoint2f(contoursWalls.get(i).toArray());
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			Imgproc.approxPolyDP(temp, approxCurve, Imgproc.arcLength(temp, true) * 0.004, true);
			
			if(approxCurve.total() == 4) {
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
		}
		
		
		
		if(verticesLast != null && rectLast != null) {
			for(int j = 0; j < 4; j++) {
				Imgproc.line(matFrame, verticesLast[j], verticesLast[(j+1)%4], new Scalar(0,255,0));
				Imgproc.putText(matFrame, verticesLast[j] + "", verticesLast[j], 2, 0.5, new Scalar(250,250,250));
			}
			/*
			Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
		    Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);

		    src_mat.put(0, 0, verticesLast[2].x, verticesLast[2].y, verticesLast[3].x, verticesLast[3].y, verticesLast[1].x, verticesLast[1].y, verticesLast[0].x, verticesLast[0].y);
		    dst_mat.put(0, 0, 0.0, 0.0, rectLast.size.height, 0.0, 0.0, rectLast.size.width, rectLast.size.height, rectLast.size.width);
		    Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src_mat, dst_mat);

		    Imgproc.warpPerspective(matFrame, matFrame, perspectiveTransform, new Size(rectLast.size.height, rectLast.size.width));
		    */
			//undistortImage();
			warpImage(verticesLast);
		}
		
		//undistortImage();
		//warpImage();
		
		findRobot(matFrame);
		findBalls(matFrame);
		
		if(robot != null && directionPoint != null) {
			mapController.robot = robot;
			Imgproc.circle(matFrame, new Point(robot.x, robot.y), 2, new Scalar(0,0,255), Imgproc.FILLED);

			Imgproc.line(matFrame, new Point(robot.x, robot.y), new Point(directionPoint.x, directionPoint.y), new Scalar(0,0,255));
		}
		
		// Efter warpimage skal vi finde cross igen
				
		Mat capturedFrameCross = matFrame.clone();
		mask = new Mat();
		edges = new Mat();
		
		Imgproc.blur(capturedFrameCross, capturedFrameCross, new Size(7,7));
		
		Imgproc.cvtColor(capturedFrameCross, mask, Imgproc.COLOR_BGR2HSV);
		
		//Imgproc.adaptiveThreshold(mask, threshold, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

		inRange = new Mat();
		
        Core.inRange(mask, lower, upper, inRange);

		Imgproc.Canny(inRange, edges, lowThresh, lowThresh*3, 3, true);
		
		List<MatOfPoint> contoursCross = new ArrayList<MatOfPoint>();

		Imgproc.findContours(edges, contoursCross, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
		
		double crossArea = 0.0;
		int crossI = -1;
		for (int i = 0; i < contoursCross.size(); i++) {

			MatOfPoint2f temp = new MatOfPoint2f(contoursCross.get(i).toArray());
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			Imgproc.approxPolyDP(temp, approxCurve, Imgproc.arcLength(temp, true) * 0.004, true);
			
			double crossAreaLocal = Imgproc.contourArea(approxCurve);
			if(crossAreaLocal > crossArea && crossAreaLocal >= (int)frameHelper.minCrossArea.getValue() && crossAreaLocal <= (int)frameHelper.maxCrossArea.getValue()) {
				crossArea = crossAreaLocal;
				crossI = i;
			}
		}
		
		if(crossI > 0) {
			float[] radius = new float[1];
			Point center = new Point();
			Imgproc.minEnclosingCircle(new MatOfPoint2f(contoursCross.get(crossI).toArray()), center, radius);

			RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contoursCross.get(crossI).toArray()));
			Point[] vertices = new Point[4];  
	        rect.points(vertices);
			
	        double diameter = 0.0;
	        double gridSizeHorizontal = matFrame.width()/180;
			double gridSizeVertical = matFrame.height()/120;
	        if(vertices != null) {
				for(int i = 0; i < 4; i++) {
					for(int j = 0; j < 4; j++) {						
						double dist = Math.abs(Math.sqrt(Math.pow((vertices[j].x/gridSizeHorizontal) - (vertices[i].x/gridSizeHorizontal), 2) + Math.pow((vertices[j].y/gridSizeVertical) - (vertices[i].y/gridSizeHorizontal), 2)));
						
						if(dist > diameter)
							diameter = dist;
					}
				}
	        }
	        
	        Obstacles obstacle = new Obstacles(center.x, center.y);
	        obstacle.setDiameter(diameter*2);
	        
	        
	        // Lav firkant rundt om cirkel
	        
	        // Find vinkel mellem toppunkt og x, y-radius vector
	        Point topL = null;
	        
            //Skal måske ændres lidt
	        /*
	        for(Point point : contoursWalls.get(crossI).toList()) {
	        	if(topL == null) {
	        		topL = point;
	        	} else {
	        		if(point.y < topL.y) {
	        			topL = point;
	        		}
	        	}
	        }
	        */
	        
	        //Imgproc.circle(matFrame, topL, 3, new Scalar(0,255, 0), Imgproc.FILLED);
	        
	        List<DTO.Point> squarePoints = new ArrayList<DTO.Point>();
	        //Index0 = øverst venstre, index1 = øverst højre, index2 = nederst venstre, index3 = nederst højre
	        squarePoints.add(new DTO.Point((center.x - (obstacle.getDiameter()/2)), (center.y - (obstacle.getDiameter()/2))));
	        squarePoints.add(new DTO.Point(squarePoints.get(0).x + obstacle.getDiameter(), squarePoints.get(0).y));
	        squarePoints.add(new DTO.Point(squarePoints.get(0).x, squarePoints.get(0).y + obstacle.getDiameter()));
	        squarePoints.add(new DTO.Point(squarePoints.get(0).x + obstacle.getDiameter(), squarePoints.get(0).y + obstacle.getDiameter()));
	        
	        obstacle.setSquarePoints(squarePoints);	        

	        robot.setDirectionVector(new Direction(directionPoint.x, directionPoint.y));
	        Goal goal = new Goal(matFrame.width()-5,matFrame.height()/2);
	        fixCoordinates(balls, obstacle, robot, goal);

	        System.out.println("Getting instructions");
			routeController.getInstruction(balls, obstacle, robot, goal);
	        
			Imgproc.circle(matFrame, center, (int) obstacle.getDiameter()/2, new Scalar(255,255,255));			
		}
		
		if(areaLast > 0 && crossArea > 0) {
			if(routeController.isReady() && run) {
				routeController.sendInstructions();
			}
		}
		
        imgCaptureLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(matFrame)));
        imgDetectionLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(edges)));

        if(firstFrame)
        	videoFrame.repaint();
	}
	
	private void fixCoordinates(List<Ball> balls, Obstacles obstacle, Robot robot, Goal goal) {
		double gridSizeHorizontal = matFrame.width()/180;
		double gridSizeVertical = matFrame.height()/120;
		
		if(robot != null) {
			int botX = (int) Math.round(robot.x/gridSizeHorizontal);
			int botY = (int) Math.round(robot.y/gridSizeVertical);
			
			DTO.Point testPoint = new DTO.Point(botX, botY);
			DTO.Point newRobot = new DTO.Point(0,0);
			DTO.Point newDirection = new DTO.Point(0,0);
			
			newRobot = projectObject(testPoint, "robot");
			int directionX = (int) Math.round(directionPoint.x/gridSizeHorizontal);
			int directionY = (int) Math.round(directionPoint.y/gridSizeVertical);
			testPoint = new DTO.Point(directionX, directionY);
			newDirection = projectObject(testPoint, "robot");
			
			robot.setCoordinates(newRobot.x, newRobot.y);
			robot.getDirectionVector().setCoordinates(newDirection.x,newDirection.y);
		}
		
		if(obstacle != null) {
			int x = (int) Math.round(obstacle.x/gridSizeHorizontal);
			int y = (int) Math.round(obstacle.y/gridSizeVertical);
			obstacle.setCoordinates(x, y);
			
			for (DTO.Point point : obstacle.getSquarePoints()) {
				x = (int) Math.round(point.x/gridSizeHorizontal);
				y = (int) Math.round(point.y/gridSizeVertical);
				
				point.setCoordinates(x, y);
			}
		}
		
		if(goal != null) {
			int x = (int) Math.round(goal.x/gridSizeHorizontal);
			int y = (int) Math.round(goal.y/gridSizeVertical);
			
			goal.setCoordinates(x, y);
		}
		
		for(Ball ball : balls) {
			int x = (int) Math.round(ball.x/gridSizeHorizontal);
			int y = (int) Math.round(ball.y/gridSizeVertical);
			if(x < 3) {
				x = 3;
			}
			if(x > 179) {
				x = 179;
			}
			if(y < 2) {
				y = 2;
			}
			if(y > 118) {
				y = 118;
			}
			ball.setCoordinates(x, y);
		}
	}
	
	private void findRobot(Mat matFrame) {
		Mat matFrameCopy = matFrame.clone();
		
		robotMask = new Mat();
		
		triangles.clear();

		Imgproc.blur(matFrameCopy, matFrameCopy, new Size(7,7));
		
		List<MatOfPoint> contoursRoboto = new ArrayList<MatOfPoint>();
				
		Imgproc.cvtColor(matFrameCopy, robotMask, Imgproc.COLOR_BGR2GRAY);
		
		Scalar lower = new Scalar(cameraSettings.getLowHueBalls(), cameraSettings.getLowSatBalls(), cameraSettings.getLowValBalls());
	    Scalar upper = new Scalar(cameraSettings.getMaxHueBalls(), cameraSettings.getMaxSatBalls(), cameraSettings.getMaxValBalls());
	    
        Core.inRange(robotMask, lower, upper, robotMask);

		Mat canny = new Mat();
		
		int lowThresh = 100;
		
        Imgproc.Canny(robotMask, canny, lowThresh, lowThresh * 3, 3, false);
		Imgproc.findContours(canny, contoursRoboto, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		
		Imgproc.drawContours(matFrame, contoursRoboto, -1, new Scalar(255,0,0));
		
		double areaLast = 0;
		MatOfPoint contourLast = null;
		int contourI = -1;
		
		for (int i = 0; i < contoursRoboto.size(); i++) {
			MatOfPoint contour = contoursRoboto.get(i);
		    double contourArea = Imgproc.contourArea(contour);
		    
		    if(contourArea > areaLast && contourArea > 400) {
		    	areaLast = contourArea;
		    	contourLast = contour;
		    	contourI = i;
			}
		}

		if(contourI != -1) {
		
			List<Point> pointsContour = contourLast.toList();
		
	        Collections.sort(pointsContour, new SortCoordinates());
	        
	        List<Point> triangle = new ArrayList<Point>();
	        
	        Point top = null, left = null, right = null;
	        
	        boolean upward = true;

	        for(Point point : pointsContour) {
	        	if(top == null) {
	        		top = point;
	        		left = point;
	        		right = point;
	        	} else {
	        		if(left.x > point.x) {
	        			left = point;
	        		} else if(right.x < point.x) {
	        			right = point;
	        		} else if(point.y < top.y) {
	        			top = point;
	        		}
	        	}
	        }

	        if(left.y-50 > top.y || right.y-50 > top.y) {
        		upward = true;
        	} else {
        		upward = false;
        	}
	        
	        for(Point point : pointsContour) {
	        	
	        	if(upward) {
	        		if(point.y < top.y) {
	        			top = point;
	        		}
	        	} else {
	        		if(point.y > top.y) {
	        			top = point;
	        		}
	        	}
	        }
	        
	        Imgproc.circle(matFrame, left, 3, new Scalar(0,0, 255), Imgproc.FILLED);
	        Imgproc.circle(matFrame, right, 3, new Scalar(0,0, 255), Imgproc.FILLED);
	        Imgproc.circle(matFrame, top, 3, new Scalar(0,0, 255), Imgproc.FILLED);
	        
	        double distTopRight = Math.hypot(top.x-right.x, top.y-right.y);
	        double distTopLeft = Math.hypot(top.x-left.x, top.y-left.y);
	        double distLeftRight = Math.hypot(left.x-right.x, left.y-right.y);
	        
	        if(distTopRight > 30 && distTopLeft > 30 && distLeftRight > 30) {
	        	double sumTop = distTopLeft + distTopRight;
	        	double sumLeft = distTopLeft + distLeftRight;
	        	double sumRight = distLeftRight + distTopRight;
	        	
	        	if(sumTop > sumLeft && sumTop > sumRight) {
	        		directionPoint = new DTO.Point(top.x, top.y);
	        	} else if(sumLeft > sumTop && sumLeft > sumRight) {
	        		directionPoint = new DTO.Point(left.x, left.y);
	        	} else if(sumRight > sumTop && sumRight > sumLeft) {
	        		directionPoint = new DTO.Point(right.x, right.y);
	        	}
	        	
	        	robot = new Robot((top.x+right.x+left.x)/3, (top.y+right.y+left.y)/3);
	        	System.out.println("Robot coord: " + robot.x + " " + robot.y);
	        	DTO.Point p = null;
	        	p = projectObject(robot, "robot");
	        	System.out.println("findRobot new p: " + p.x + " " + p.y);
	        }
		}
		
	}
	private static DTO.Point projectObject(DTO.Point point, String objectType) {
		double pointHeight;
		
		DTO.Point returnPoint = new DTO.Point(0,0);
		
		if(objectType.equalsIgnoreCase("Robot")) {
			pointHeight = 37.4;
		}else {
			pointHeight = 4;
		}
		
		double xDiff = Math.abs(point.x - 90);
		double yDiff = Math.abs(point.y - 60);
		
		double fakeRadius = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		double cameraAngel = Math.atan((fakeRadius/170));
		//cameraAngel = Math.toDegrees(cameraAngel);
		/* ERROR STARTS HERE maybe*/
		
		double radiusDiff = Math.tan(cameraAngel)*pointHeight;
		double realRadius = fakeRadius - radiusDiff;
		
		double slope = (point.y - 60)/(point.x - 90);
		double intersect = 60 - slope * 90;
		
		//double circleIntersect = Math.pow((x-90),2) + Math.pow((slope*x+intersect-60), 2) - realRadius*realRadius;
		
		double firstEquationPart = slope*slope + 1;
		double secondEquationPart = 2*slope*(intersect-60)-180;
		double thirdEquationPart = (intersect-60)*(intersect-60) - realRadius*realRadius + 8100;
		
		double circleIntersectionPos = (0-secondEquationPart + (Math.sqrt((secondEquationPart * secondEquationPart - 4*firstEquationPart*thirdEquationPart))))/(2*firstEquationPart);
		double circleIntersectionNeg = (0-secondEquationPart - Math.sqrt(Math.pow(secondEquationPart, 2) - 4*firstEquationPart*thirdEquationPart))/(2*firstEquationPart);
		/*
		System.out.println("fakeRadius: " + fakeRadius);
		System.out.println("Camera Angel: " + cameraAngel);
		System.out.println("radiusDiff: " + radiusDiff);
		System.out.println("realRadius: " + realRadius);
		System.out.println("slope: " + slope);
		System.out.println("intersect: " + intersect);
		System.out.println("a: " + firstEquationPart);
		System.out.println("b: " + secondEquationPart);
		System.out.println("c " + thirdEquationPart);
		
		System.out.println("First intersection x-coordinate: " + circleIntersectionPos);
		System.out.println("Second intersection x-coordinate: " + circleIntersectionNeg);*/
		
		double yForPos = slope * circleIntersectionPos + intersect;
		double yForNeg = slope * circleIntersectionNeg + intersect;
		/*
		System.out.println("y1: " + yForPos);
		System.out.println("y2: " + yForNeg);*/
		
		DTO.Point pPos = new DTO.Point(circleIntersectionPos, yForPos);
		DTO.Point pNeg = new DTO.Point(circleIntersectionNeg, yForNeg);
		
		double distToPPos = point.dist(pPos);
		double distToPNeg = point.dist(pNeg);
/*
		System.out.println("dist pPos: " + distToPPos);
		System.out.println("dist pNeg: " + distToPNeg);*/
		
		if(distToPNeg > distToPPos) {
			returnPoint.setCoordinates(circleIntersectionPos, yForPos);
			
		}else {
			returnPoint.setCoordinates(circleIntersectionNeg, yForNeg);
		}
		System.out.println("Point coordinates: " + returnPoint.x + " " + returnPoint.y);
		return returnPoint;	
		
	}

	
	class SortCoordinates implements Comparator<Point>{

		@Override
		public int compare(Point point1, Point point2) {
			int result = Double.compare(point1.x, point2.x);
	         if ( result == 0 ) {
	           // both X are equal -> compare Y too
	           result = Double.compare(point1.y, point2.y);
	         } 
	         return result;
		}
		
	}
	
	private void findBalls(Mat matFrame) {
		Mat matFrameCopy = matFrame.clone();
		Mat mask = new Mat();
		ballsMask = new Mat();
		

		Imgproc.blur(matFrameCopy, matFrameCopy, new Size(3,3));
		
		Imgproc.cvtColor(matFrameCopy, ballsMask, Imgproc.COLOR_BGR2GRAY);
		
		Scalar lower = new Scalar(cameraSettings.getLowHueBalls(), cameraSettings.getLowSatBalls(), cameraSettings.getLowValBalls());
	    Scalar upper = new Scalar(cameraSettings.getMaxHueBalls(), cameraSettings.getMaxSatBalls(), cameraSettings.getMaxValBalls());
	    
        Core.inRange(ballsMask, lower, upper, ballsMask);

		Mat canny = new Mat();

		contours.clear();
		balls.clear();
		
		int lowThresh = 100;
		
        Imgproc.Canny(ballsMask, canny, lowThresh, lowThresh * 3, 3, false);
		
		Imgproc.findContours(canny, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		double minArea = Math.PI * (cameraSettings.getMinBallSize() * 0.9f) * (cameraSettings.getMinBallSize() * 0.9f); // minimal ball area
		double maxArea = Math.PI * (cameraSettings.getMaxBallSize() * 1.1f) * (cameraSettings.getMaxBallSize() * 1.1f); // maximal ball area
		
		

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
		
		for (Ball ball : balls) {
			Imgproc.circle(matFrame, new Point(ball.x, ball.y), 20, new Scalar(0, 0, 255));
			Imgproc.putText(matFrame, "bold", new Point(ball.x, ball.y-20), 3, 1.5, new Scalar(0, 0, 255));
		}
		
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
	
	public MapController getMapController() {
		return mapController;
	}

	public void setMapController(MapController mapController) {
		this.mapController = mapController;
	}



	private class CaptureTask extends SwingWorker<Void, Mat> {
        @Override
        protected Void doInBackground() {
            Mat matFrame = new Mat();
            while (!isCancelled()) {
                if (!videoCapture.read(matFrame)) {
                    break;
                }
                publish(matFrame.clone());
            }
            return null;
        }
        @Override
        protected void process(List<Mat> frames) {
        	
            Mat imgCapture = frames.get(frames.size() - 1);
            matFrame = imgCapture;

            
            //Findwalls etc her
            updateFrame();
            //findWalls();
            
            imgCaptureLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(matFrame)));
            imgDetectionLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(edges)));
            
            videoFrame.repaint();
            
            
        }
    }
	
	private void warpImage(Point[] verticesLast) {
		
		if(verticesLast == null) {
			System.out.println("Billedet kan ikke warpes da der ikke blev fundet nogle hjørner");
		} else if(verticesLast.length != 4) {
			System.out.println("Billedet kan ikke warpes da der ikke blev fundet præcis 4 hjørner");
		} else {
			if(firstFrame) {
				firstFrame = false;
				
				for(int i = 0; i < verticesLast.length; i++) {
					int countX = 0, countY = 0;
					Point vertex = verticesLast[i];
					if(vertex.x > verticesLast[(i+1)%4].x) {
						countX++;
					}
					if(vertex.y > verticesLast[(i+1)%4].y) {
						countY++;
					}
					if(vertex.x > verticesLast[(i+2)%4].x) {
						countX++;
					}
					if(vertex.y > verticesLast[(i+2)%4].y) {
						countY++;
					}
					if(vertex.x > verticesLast[(i+3)%4].x) {
						countX++;
					}
					if(vertex.y > verticesLast[(i+3)%4].y) {
						countY++;
					}
					
					if(countX <= 1 && countY <= 1 ) {
						topLeft = vertex;
					} else if(countX >= 2 && countY <= 1) {
						topRight = vertex;
					} else if(countX <= 1 && countY >= 2) {
						bottomLeft = vertex;
					} else if(countX >= 2 && countY >= 2) {
						bottomRight = vertex;
					}
					
				}

				corners = new Vector<Point>();
				corners.add(topLeft);
				corners.add(topRight);
				corners.add(bottomLeft);
				corners.add(bottomRight);
				
				minXVal = Math.min(topLeft.x, bottomLeft.x);
				maxXVal = Math.max(topRight.x, bottomRight.x);
				width = maxXVal - minXVal;
				
				minYVal = Math.min(topLeft.y, topRight.y);
				maxYVal = Math.max(bottomLeft.y, bottomRight.y);
				height = maxYVal - minYVal;
				
				target = new Vector<Point>();
				target.add(new Point(0,0));
				target.add(new Point(width-1,0));
				target.add(new Point(0, height-1));
				target.add(new Point(width-1,height-1));
				
				perspectiveTransform = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(corners), Converters.vector_Point2f_to_Mat(target));
					
			}
			
			
			Imgproc.warpPerspective(matFrame, matFrame, perspectiveTransform, new Size(width, height));
				
		}
		
	}
	
	private void undistortImage() {
		Mat srcImg = matFrame.clone();
		Mat cameraMatrix = new Mat(3,3,CvType.CV_32F);
		cameraMatrix.put(0, 0, 1372.53986);
		cameraMatrix.put(0, 1, 0);
		cameraMatrix.put(0, 2, 1131.69058);
		cameraMatrix.put(1, 0, 0);
		cameraMatrix.put(1, 1, 1371.81273);
		cameraMatrix.put(1, 2, 660.589411);
		cameraMatrix.put(2, 0, 0);
		cameraMatrix.put(2, 1, 0);
		cameraMatrix.put(2, 2, 1);
		
		Mat distCoeffs = new Mat(1,5,CvType.CV_32F);
		distCoeffs.put(0, 0, 0.08984192);
		distCoeffs.put(0, 1, -0.24672395);
		distCoeffs.put(0, 2, 0.00120224);
		distCoeffs.put(0, 3, -0.00039666);
		distCoeffs.put(0, 4, 0.11864747);
		
		//Mat newCameraMtx = Calib3d.getOptimalNewCameraMatrix(cameraMatrix, distCoeffs, new Size(srcImg.width(), srcImg.height()), 1, new Size(srcImg.width(), srcImg.height()));
		//Calib3d.undistort(srcImg, img, cameraMatrix, distCoeffs, newCameraMtx);
		
		//Calib3d.undistort(srcImg, matFrame, cameraMatrix, distCoeffs);
		
	}
	
	private void projectObject(Point point) {
		double xDiff = Math.abs(point.x - 90);
		double yDiff = Math.abs(point.y - 60);
		
		double fakeRadius = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		double cameraAngel = Math.toDegrees(Math.atan((fakeRadius/cameraHeight)));
		double radiusDiff = Math.toDegrees(Math.tan(cameraAngel)) * 37.4;
		double realRadius = fakeRadius - radiusDiff;
		
		double slope = (point.y - 60)/(point.x - 90);
		double intersect = 60 - slope * 90;
		
		//double circleIntersect = Math.pow((x-90),2) + Math.pow((slope*x+intersect-60), 2) - realRadius*realRadius;
		
		double firstEquationPart = slope*slope + 1;
		double secondEquationPart = 2*slope*(intersect-60)-180;
		double thirdEquationPart = (intersect-60)*(intersect-60) - realRadius*realRadius + 8100;
		
		double circleIntersectionPos = (-secondEquationPart + Math.sqrt(Math.pow(secondEquationPart, 2) - 4*firstEquationPart*thirdEquationPart))/2*firstEquationPart;
		double circleIntersectionNeg = (-secondEquationPart - Math.sqrt(Math.pow(secondEquationPart, 2) - 4*firstEquationPart*thirdEquationPart))/2*firstEquationPart;
		
		System.out.println("First intersection x-coordinate" + circleIntersectionPos);
		System.out.println("Second intersection x-coordinate" + circleIntersectionNeg);
		
		
	}
	
}

