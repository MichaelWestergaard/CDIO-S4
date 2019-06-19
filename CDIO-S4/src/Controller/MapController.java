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
	
	Socket socket;
	String readline;
	Map<String, Double> myMap = new HashMap<String, Double>();
	boolean moreBalls;
	
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
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Double> fillMap() {
		Map<String, Double> x = new HashMap<String, Double>();
/*		for(int i = 20; i < 30; i++) {
		x.put("rotate" + i, (double) i);
		x.put("travel" + i , (double) i-15);
		}
*/		
	//	x.put("rotate1", 360.0*2);
	
		
		return x;
	}
	
	public void loadMap(int[][] loadMap) throws IOException {
		
		map = loadMap;
		ready = false;
		findBalls();
		findShortestPath();
		
		moreBalls = true;
		init();
		
		OutputStream outputStream = socket.getOutputStream();
	    ObjectOutputStream mapOutputStream = new ObjectOutputStream(outputStream);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	    mapOutputStream.writeObject(instructionMap);
	    mapOutputStream.flush();		    	
   
	    int tempClose = 0;
	    
	    System.out.println(instructionMap);
	    
	    while(moreBalls) {
		    String line = null;

	    	while((line = reader.readLine()) != null) {

			    //Find flere bolde med kameraet og sæt dem ind i et HashMap
	    		//De kan ikke have samme key, så de må klades f.eks. travel1, travel2, travel3 osv. og så fjerne disse tal når mappet er blevet sendt.

			    System.out.println(instructionMap);
	    		
	    		mapOutputStream.writeObject(instructionMap);
			    mapOutputStream.flush();
			    
			    tempClose++;
			    
			    break;
	    	}
	    	
		    //En eller anden betingelse der styrer hvornår robotten ikke skal køre mere, e.g. at kameratet ikke kan se flere bolde.
		    if(tempClose == 1) {
		    	moreBalls = false;
		    }
	    	
	    }

	    //Send et tomt map når forbindelsen skal lukkes.
	    Map<String, Double> stopMap = new HashMap<String, Double>();
	    
	    mapOutputStream.writeObject(stopMap);
	    mapOutputStream.flush();
	    mapOutputStream.close();

	    System.out.println("Alt er sendt");
	    
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void findBalls() {
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
					/*else if (map[x+15][y] == 5) {
						bigGoal = new Goal(x,y);
					}*/
				}

				if(map[x][y] == 1) {
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
		//System.out.println();

		/*int minusCounter = 0;
		for(int test = 0; test < 120;test++) {
			System.out.println(test);
			if(map[1][test] == -1) {
				minusCounter++;
				System.out.println("Antal forhindringer: " + minusCounter);
			}

		}*/
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

		
		double robotDirectionDistance = Math.sqrt(Math.abs((robot.x - directionVector.x))*Math.abs((robot.x - directionVector.x)) + Math.abs((robot.y - directionVector.y))*Math.abs((robot.y - directionVector.y)));
		//double robotDirectionYDiff = Math.abs(robot.y - directionVector.y);
		//double robotDirectionXDiff = Math.abs(robot.x - directionVector.x);
		
		
		for(int i = 0; i < iterator; i++) {
			Collections.sort(coordinates, new Sort());
			double[] closestBallCoordinates = coordinates.get(0).getCoordinates();

			/*for(int j = 0; j < coordinates.size(); j++) {
				System.out.println("robot "+ robot+ "  direction  " + directionVector);
				System.out.println(coordinates.get(j)+ " dist = " + robot.dist(coordinates.get(j)) + " angle = " + robot.angleBetween(directionVector, coordinates.get(j)));
				instructionMap.put("rotate" + operationNum, robot.angleBetween(directionVector, coordinates.get(j)));
				System.out.println("rotate" + operationNum + " " + robot.angleBetween(directionVector, coordinates.get(j)));
				instructionMap.put("travel" + (operationNum + 1), robot.dist(coordinates.get(j)));
				System.out.println("travel" + (operationNum + 1) + " " + robot.dist(coordinates.get(j)));
				operationNum += 2;
			}*/
			
			
			
			//instructionMap.put("rotate" + operationNum, robot.angleBetween(directionVector, coordinates.get(0)));
			int direction = GetDirection(directionVector, coordinates.get(0), robot);
			if(direction > 0) {
				System.out.println("Roterer til højre");
				instructionMap.put("rotate" + operationNum, robot.angleBetween(directionVector, coordinates.get(0)));
				System.out.println("rotate" + operationNum + " " + robot.angleBetween(directionVector, coordinates.get(0)));
			} else if(direction < 0) {
				System.out.println("Roterer til venstre");
				instructionMap.put("rotate" + operationNum, 0-(robot.angleBetween(directionVector, coordinates.get(0))));
				System.out.println("rotate" + operationNum + " " + (0-(robot.angleBetween(directionVector, coordinates.get(0)))));
			} else {
				System.out.println("Roterer 10 grader");
				instructionMap.put("rotate" + operationNum, 10.0);
				System.out.println("rotate" + operationNum + " " + robot.angleBetween(directionVector, coordinates.get(0)));
				double newDirectionX = Math.cos(10.0 * robot.x) - Math.sin(10.0 * robot.y);
				double newDirectionY = Math.sin(10.0 * robot.x) + Math.cos(10.0 * robot.y);
				directionVector.setCoordinates(newDirectionX, newDirectionY);
				continue;
			}
			
			instructionMap.put("travel" + (operationNum + 1), robot.dist(coordinates.get(0)));
			System.out.println("travel" + (operationNum + 1) + " " + robot.dist(coordinates.get(0)));
			

			double newDirectionX = Math.cos(robot.angleBetween(directionVector, coordinates.get(0)) * robot.x) - Math.sin(robot.angleBetween(directionVector, coordinates.get(0)) * robot.y);
			double newDirectionY = Math.sin(robot.angleBetween(directionVector, coordinates.get(0)) * robot.x) + Math.cos(robot.angleBetween(directionVector, coordinates.get(0)) * robot.y);
			
			//double slope = (closestBallCoordinates[1] - robot.y)/(closestBallCoordinates[0] - robot.x);   
			//double intersect = robot.y - (slope * robot.x);
			
			//double distanceFormula = Math.abs(slope*closestBallCoordinates[0]+intersect-closestBallCoordinates[1])/Math.sqrt(slope*slope+1);
			
			
			
			
			System.out.println("Antal bolde der mangler at blive besøgt: "+ coordinates.size());

			//directionVector.setCoordinates(newDirectionX + closestBallCoordinates[0], newDirectionY + closestBallCoordinates[1]);
			directionVector.setCoordinates(newDirectionX, newDirectionY);

			
			
			robot.setCoordinates(closestBallCoordinates[0], closestBallCoordinates[1]);	
			operationNum += 2;

			coordinates.remove(0);

		}

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
