package Controller;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DTO.Ball;
import DTO.DeliveryPoint;
import DTO.Direction;
import DTO.Goal;
import DTO.Point;
import DTO.Robot;

public class MapController {

	List<Ball> balls = new ArrayList<Ball>();
	List<DeliveryPoint> deliveryPoints = new ArrayList<DeliveryPoint>();
	List<Ball> shortestPath = new ArrayList<Ball>();
	ArrayList<Ball> coordinates = new ArrayList<Ball>();

	Map<String, Double> instructionMap = new HashMap <String, Double>();
	Direction directionVector, goalPoint;
	Goal smallGoal, bigGoal;
	public Robot robot;
	private int[][] map = null;
	private boolean ready = true;
	private boolean isConnected = false;
	BufferedReader reader;
	ObjectOutputStream mapOutputStream;
	OutputStream outputStream;
	
	Socket socket;
	String readline;
	Map<String, Double> myMap = new HashMap<String, Double>();
	boolean moreBalls = true;
	
	public MapController() {
		if(map == null) {
			System.out.println("Map is empty");
		}
	}
	
	public void init() {
		try {
			socket = new Socket("192.168.43.31", 3005);		
			socket.setKeepAlive(true);
			socket.setSoTimeout(0);
			isConnected = true;
			outputStream = socket.getOutputStream();
			mapOutputStream = new ObjectOutputStream(outputStream);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Double> fillMap() {
		Map<String, Double> x = new HashMap<String, Double>();
		
		return x;
	}
	
	public void loadMap(int[][] loadMap) throws IOException {
		if(!isConnected) {
			init();
		}
		
		int sameCounter = 0;
		if(map != null) {
			for (int x = 0; x < map.length; x++) { //r�kkerne
				for (int y = 0; y < map[x].length; y++) { //kolonnerne
					if(map[x][y] != loadMap[x][y]) {
						sameCounter++;
					}

				}
			}
		System.out.println("Same counter: " + sameCounter);	
		}
		map = loadMap;
		ready = false;
		primaryFunc();
	    
	}
	
	private void primaryFunc() throws IOException{
	    
		findBalls();
		findShortestPath();
		
    	if(coordinates.size() == 0) {
    		//Find vej til mål
		    System.out.println("no more balls");
    	}
		
	    String line = null;
	    
		mapOutputStream.writeObject(instructionMap);
	    mapOutputStream.flush();
	    
	    
	    while(true) {
	    	line = reader.readLine();
	    	System.out.println(instructionMap);
	    	System.out.println(line);
	    	
	    	if(line.equals("next")) {
	    		ready = true;
	    		System.out.println("Thank u , next ");
	    		break;
	    	}   
	    }
    	/*while((line = reader.readLine()) != null) {
	    	System.out.println(instructionMap);
	    	System.out.println(line);
	    	
	    	if(line.equals("next")) {
	    		ready = true;
	    		System.out.println("Thank u , next ");
	    		break;
	    	}    	
    	}*/

	    //Send vejen til målet og victory
	    
	    /*if(coordinates.size() == 0) {
	    	Map<String, Double> stopMap = new HashMap<String, Double>();
	    
	    	mapOutputStream.writeObject(stopMap);
	    	mapOutputStream.flush();
	    	mapOutputStream.close();

	    	System.out.println("Alt er sendt");
	    }*/
	    //Send et tomt map når forbindelsen skal lukkes.
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void findBalls() {
		coordinates.clear();
		//int ballcounter;
		boolean ballStatus = false;
    
		for (int x = 0; x < map.length; x++) { //r�kkerne
			for (int y = 0; y < map[x].length; y++) { //kolonnerne

				//	System.out.print(map[x][y]);

				if(map[x][y] == 9) {
					robot = new Robot(x,y);
					System.out.println("Robot:   " +robot);
				}
				if(map[x][y] == 3) {
					directionVector = new Direction(x,y);
					System.out.println("Retning :   " +directionVector);
				}
				if(map[x][y] == 4) {
					goalPoint = new Direction(x,y);
				}

				if(map[x][y] == 5) {
					smallGoal = new Goal(x,y);
				}

				if(map[x][y] == 1) {
					if(x+3 <= map.length && y+3 <= map[x].length) {
						if(map[x+3][y] == 1 && map[x][y+3] == 1 && map[x+3][y+3] == 1) {

							ballStatus = true;

							map[x][y] = 9;
							map[x+1][y] = 0;
							map[x][y+1] = 0;
							map[x+1][y+1] = 0;

							coordinates.add(new Ball(x,y));
						}
					}
				}

			}
		}
		printBallCoordinates();
	}

	private void printBallCoordinates() {
		for(Ball ball : coordinates) {
			System.out.println("x: " + ball.x + " y: " + ball.y);
		}
	}

	public int[][] locateDeliveryPoints(int[][] map) {
		//System.out.println(coordinates);
		return map; 
	}

	public void findShortestPath() {
		Collections.sort(coordinates, new Sort());

		int iterator = coordinates.size();
		int operationNum = 0;
		
		instructionMap.put("rotate" + operationNum, robot.angleBetween(directionVector, coordinates.get(0)));
		System.out.println("rotate" + operationNum + " " + robot.angleBetween(directionVector, coordinates.get(0)));
		instructionMap.put("travel" + (operationNum + 1), robot.dist(coordinates.get(0)));
		System.out.println("travel" + (operationNum + 1) + " " + robot.dist(coordinates.get(0)));
		
		System.out.println("Antal bolde der mangler at blive besøgt: "+ coordinates.size());
		
		//double robotDirectionDistahttps://github.com/MichaelWestergaard/CDIO-S4/pull/4/conflict?name=CDIO-S4%252Fsrc%252FController%252FMapController.java&ancestor_oid=dca31eb1e5528652fc3b942443f450a99f953c27&base_oid=87f8fc831efe2bebb14721ef1b6725f737e20746&head_oid=15b6ad6b433e6eade50320309f8e8828c3620dd0nce = Math.sqrt(Math.abs((robot.x - directionVector.x))*Math.abs((robot.x - directionVector.x)) + Math.abs((robot.y - directionVector.y))*Math.abs((robot.y - directionVector.y)));
		
		
		/*for(int i = 0; i < iterator; i++) {
			Collections.sort(coordinates, new Sort());
			double[] closestBallCoordinates = coordinates.get(0).getCoordinates();
			
			double newDirectionX = 0;
			double newDirectionY = 0;
			Point newDirectionCoordinates = null;
			
			instructionMap.put("rotate" + operationNum, robot.angleBetween(directionVector, coordinates.get(0))*-1);
			
			if(robot.angleBetween(directionVector, coordinates.get(0)) > 0) {
				System.out.println("Roterer til højre");
				System.out.println("rotate" + operationNum + " " + robot.angleBetween(directionVector, coordinates.get(0)));

        //newDirectionCoordinates = rotateDirection("højre");
				newDirectionX = (coordinates.get(0).getCoordinates()[0] * 2) - robot.getCoordinates()[0];
				newDirectionY = (coordinates.get(0).getCoordinates()[1] * 2) - robot.getCoordinates()[1];

			} else if(robot.angleBetween(directionVector, coordinates.get(0)) < 0) {
				System.out.println("Roterer til venstre");
				System.out.println("rotate" + operationNum + " " + robot.angleBetween(directionVector, coordinates.get(0)));
				
				//newDirectionCoordinates = rotateDirection("venstre");
				newDirectionX = (coordinates.get(0).getCoordinates()[0] * 2) - robot.getCoordinates()[0];
				newDirectionY = (coordinates.get(0).getCoordinates()[1] * 2) - robot.getCoordinates()[1];

			} else {
				System.out.println("Roterer 10 grader");
				instructionMap.put("rotate" + operationNum, 10.0);
				System.out.println("rotate" + operationNum + " " + robot.angleBetween(directionVector, coordinates.get(0)));

				newDirectionCoordinates = rotateDirection(coordinates.get(0), true);
				newDirectionX = newDirectionCoordinates.x;
				newDirectionY = newDirectionCoordinates.y;
				
				//directionVector.setCoordinates(newDirectionX, newDirectionY);
				continue;
			}
			
			
			instructionMap.put("travel" + (operationNum + 1), robot.dist(coordinates.get(0)));
			System.out.println("travel" + (operationNum + 1) + " " + robot.dist(coordinates.get(0)));
			
			System.out.println("Antal bolde der mangler at blive besøgt: "+ coordinates.size());

			//directionVector.setCoordinates(newDirectionX + closestBallCoordinates[0], newDirectionY + closestBallCoordinates[1]);
			directionVector.setCoordinates(newDirectionX, newDirectionY);			
			
			robot.setCoordinates(closestBallCoordinates[0], closestBallCoordinates[1]);	
			operationNum += 2;

			coordinates.remove(0);

		}*/

	}

	private Point rotateDirection(Point ballPoint, boolean findLongestDist) {		
		Point returnPoint = new Point(0,0);
		
		double xDiff = Math.abs(directionVector.x - robot.x);
		double yDiff = Math.abs(directionVector.y - robot.y);
		
		double realRadius = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		
		double slope = (ballPoint.y - robot.y)/(ballPoint.x - robot.x);
		double intersect = robot.y - slope * robot.x;
		
		//double circleIntersect = Math.pow((x-90),2) + Math.pow((slope*x+intersect-60), 2) - realRadius*realRadius;
		
		double firstEquationPart = slope*slope + 1;
		double secondEquationPart = 2*slope*(intersect-robot.y)-(2 * robot.x);
		double thirdEquationPart = (intersect-robot.y)*(intersect-robot.y) - realRadius*realRadius + robot.x*robot.x;
		
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
		
		Point pPos = new Point(circleIntersectionPos, yForPos);
		Point pNeg = new Point(circleIntersectionNeg, yForNeg);
		
		double distToPPos = ballPoint.dist(pPos);
		double distToPNeg = ballPoint.dist(pNeg);
/*
		System.out.println("dist pPos: " + distToPPos);
		System.out.println("dist pNeg: " + distToPNeg);*/
		
		if(findLongestDist) {
			if(distToPNeg < distToPPos) {
				returnPoint.setCoordinates(circleIntersectionPos, yForPos);
				
			}else {
				returnPoint.setCoordinates(circleIntersectionNeg, yForNeg);
			}
		} else {
			if(distToPNeg > distToPPos) {
				returnPoint.setCoordinates(circleIntersectionPos, yForPos);
				
			}else {
				returnPoint.setCoordinates(circleIntersectionNeg, yForNeg);
			}
		}
		
		System.out.println("Point coordinates: " + returnPoint.x + " " + returnPoint.y);
		return returnPoint;	

		
				//Gang alle punkternes y-koordinat med -1 for at konvertere
				//det til et 'normalt' koordinat, så det kan beregnes med funktionen.
				//Til sidst i funktionen ganges y resultatet med -1 for at konvertere
				//Det tilbage til det oprindelige format.
				
				
				/*double directionX = directionVector.x;
				double directionY = directionVector.y * -1;
				
				double robotX = robot.x;
				double robotY = robot.y * -1;
				
				double ballX = coordinates.get(0).x;
				double ballY = coordinates.get(0).y * -1;
				
				double angle = robot.angleBetween(directionVector, coordinates.get(0));
				
				
				if(retning.equalsIgnoreCase("venstre")) {
					angle = 0 - angle;
				} else if(retning.equalsIgnoreCase("retry")) {
					angle = 10;
				}

				double newX = ((directionX - robotX) * Math.cos(Math.toRadians(angle))) - ((robotY - directionY) * Math.sin(Math.toRadians(angle))) + robotX;
				
				double slope = (ballY - robotY)/(ballX - robotX);
				double intersect = robotY - slope * robotX;
				
				double newY = slope * newX + intersect;
				
				System.out.println("a: " + slope);
				System.out.println("b: " + intersect);
				
				System.out.println("newX: " + newX);
				System.out.println("newY: " + newY);
		
				double xDiff = Math.abs(newX - robotX);
				
				if(retning.equalsIgnoreCase("venstre")) {
					newX = ballX - xDiff;
				} else {
					newX = ballX + xDiff;
				}
				
				newY = slope * newX + intersect;
				
				newY *= -1;

				System.out.println("Final x: " + newX);
				System.out.println("Final y: " + newY);

				double[] results = {newX, newY};
				
				return results;
				*/
	}
	
	public int GetDirection(Point direction, Point ball, Point robot)
	{
		direction.y *= -1;
		ball.y *= -1;
		robot.y *= -1;
		
	    double theta1 = GetAngle(direction, ball); 
	    double theta2 = GetAngle(ball, robot);
	    double delta = NormalizeAngle(theta2 - theta1);

	    if ( delta == 0 || delta == Math.PI) {
	    	return 0;
	    }
	    else if ( delta < Math.PI )
	        return -1;
	    else return 1;
	}

	private Double GetAngle(Point p1, Point p2)
	{
	    Double angleFromXAxis = Math.atan((p2.y - p1.y ) / (p2.x - p1.x ) ); // where y = m * x + K
	    return  p2.x - p1.x < 0 ? angleFromXAxis + Math.PI : angleFromXAxis; // The will go to the correct Quadrant
	}

	private Double NormalizeAngle(Double angle)
	{
	    return angle < 0 ? angle + 2 * Math.PI : angle; //This will make sure angle is [0..2PI]
	}
	
	class Sort implements Comparator<Point>{

		@Override
		public int compare(Point point1, Point point2) { 

			try {
			if(robot.dist(point1) < robot.dist(point2)) {
				return -1;
			}
			if(robot.dist(point2) < robot.dist(point1)) {
				return 1;
			}

			return 0;
			} catch (Exception e) {
				
			} finally {
				return 0;
			}
		}

	}
	

	
	
	
	
	
	

}
