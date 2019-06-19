package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DTO.Ball;
import DTO.Goal;
import DTO.Obstacles;
import DTO.Point;
import DTO.Robot;

public class RouteController {
	
	int operationNum = 0;
	Map<String, Double> instructions;
	Obstacles obstacle;
	Goal goal;
	Robot robot;

	private boolean ready = true;
	private boolean isConnected = false;
	BufferedReader reader;
	ObjectOutputStream mapOutputStream;
	OutputStream outputStream;
	private Point goalPointHelper;
	
	Socket socket;
	String readline;
	boolean moreBalls = true;
		
	public void socketInit() {
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
	
	//TODO: ROTATE SQUARE POINTS

	public Map<String, Double> getInstruction(List<Ball> balls, Obstacles obstacle, Robot robot, Goal goal){
		
		instructions = new HashMap<String, Double>();
		this.obstacle = obstacle;
		this.goal = goal;
		this.robot = robot;
		
		goalPointHelper = new Point(goal.x-20,goal.y);
		
		Collections.sort(balls, new Sort());
				
		int iterator = balls.size();
		int i = 0;
		
		System.out.println("Balls = " + balls);
		System.out.println("Obstacle = " + obstacle);
		System.out.println("Diameter = " + obstacle.getDiameter());
		System.out.println("Square = " + obstacle.getSquarePoints().get(0) + " | " + obstacle.getSquarePoints().get(1) + " | " + obstacle.getSquarePoints().get(2) + " | " + obstacle.getSquarePoints().get(3));
		System.out.println("robot = " + robot);		
		System.out.println("Direction = " + robot.getDirectionVector());
		
		//Fjern loop hvis den kun skal køre efter én bold
		while(i < iterator) {
			Collections.sort(balls, new Sort());
			
			Ball ball = balls.get(i);
		
			getRoute(ball);
			balls.remove(ball);
			iterator = balls.size();
		}
		
		System.out.println("" + instructions);
		return instructions;
	}
	
	private void instructionsToGoal() {
		instructions = new HashMap<String, Double>();
		
		boolean crossBlocking = false;
		
		List<Point> intersectionPoints = obstacle.getCircleLineIntersectionPoint(robot, goalPointHelper);

		if(!intersectionPoints.isEmpty()) {
			crossBlocking = true;
		}
		
		if(!crossBlocking) {
			addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), goalPointHelper));
			addInstruction("travel", robot.dist(goalPointHelper));
			
			// Set new robot coordinates and new direction
			robot.getDirectionVector().setCoordinates((goalPointHelper.x * 2) - robot.x, (goalPointHelper.y * 2) - robot.y);
			robot.setCoordinates(goalPointHelper.x, goalPointHelper.y);
			
			addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), goal));
			addInstruction("travel", robot.dist(goal));
		} else {
			System.out.println("Direct way is blocked, need to find an alternative route");	
			
			Point finalDestination = null;
			double distance = Double.MAX_VALUE;
			
			Point firstDestination = null;
			double distanceToRobot = Double.MAX_VALUE;
			
			for(Point point : obstacle.getSquarePoints()) {
				if(point.dist(goalPointHelper) < distance) {
					finalDestination = point;
					distance = point.dist(goalPointHelper);
				}
				
				if(point.dist(robot) < distanceToRobot) {
					firstDestination = point;
					distanceToRobot = point.dist(robot);
				}
			}
			
			addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), firstDestination));
			addInstruction("travel", distanceToRobot);
			
			robot.getDirectionVector().setCoordinates((firstDestination.x * 2) - robot.x, (firstDestination.y * 2) - robot.y);
			robot.setCoordinates(firstDestination.x, firstDestination.y);
			
			Point currentPoint = firstDestination;
			
			System.out.println("firstDestination " + firstDestination);
			
			while(!currentPoint.equals(finalDestination)) {
				int index = obstacle.getSquarePoints().indexOf(currentPoint);
				
				switch(index) {
					case 0:
						currentPoint = obstacle.getSquarePoints().get(1);
						break;
					case 1:
						currentPoint = obstacle.getSquarePoints().get(3);
						break;
					case 2:
						currentPoint = obstacle.getSquarePoints().get(0);
						break;
					case 3:
						currentPoint = obstacle.getSquarePoints().get(2);
						break;
				}
				
				addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), currentPoint));
				addInstruction("travel", obstacle.getDiameter());
				
				robot.getDirectionVector().setCoordinates((currentPoint.x * 2) - robot.x, (currentPoint.y * 2) - robot.y);
				robot.setCoordinates(currentPoint.x, currentPoint.y);
				
				System.out.println("currentPoint = " + currentPoint);
			}
			System.out.println("finalDestination = " + finalDestination);
			
			addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), goalPointHelper));
			addInstruction("travel", robot.dist(goalPointHelper));
			
			robot.getDirectionVector().setCoordinates((goalPointHelper.x * 2) - robot.x, (goalPointHelper.y * 2) - robot.y);
			robot.setCoordinates(goalPointHelper.x, goalPointHelper.y);
			
			addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), goal));
			addInstruction("travel", robot.dist(goal));
			
			robot.getDirectionVector().setCoordinates((goal.x * 2) - robot.x, (goal.y * 2) - robot.y);
			robot.setCoordinates(goal.x, goal.y);			
			
			System.out.println("NewRobot =" + robot);
		}
		
		sendInstructions();
	}
	
	public void sendInstructions() {
		if(!isConnected) {
			socketInit();
		}
		
		String line = null;
	    
		ready = false;
		
		try {
			mapOutputStream.writeObject(instructions);
		    mapOutputStream.flush();
		    
		    while(true) {
		    	line = reader.readLine();
		    	System.out.println(instructions);
		    	System.out.println(line);
		    	
		    	if(line.equals("next")) {
		    		ready = true;
		    		System.out.println("Thank u , next ");
		    		break;
		    	}   
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean getRoute(Ball ball) {
		boolean crossBlocking = false;

		System.out.println("ball = " + ball);
		
		List<Point> intersectionPoints = obstacle.getCircleLineIntersectionPoint(robot, ball);

		if(!intersectionPoints.isEmpty()) {
			crossBlocking = true;
		}
		
		if(!crossBlocking) {
			addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), ball));
			addInstruction("travel", robot.dist(ball));

			// Set new robot coordinates and new direction
			robot.getDirectionVector().setCoordinates((ball.x * 2) - robot.x, (ball.y * 2) - robot.y);
			robot.setCoordinates(ball.x, ball.y);
			return true;
		} else {
			System.out.println("Direct way is blocked, need to find an alternative route");	
						
			Point finalDestination = null;
			double distance = Double.MAX_VALUE;
			
			Point firstDestination = null;
			double distanceToRobot = Double.MAX_VALUE;
			
			for(Point point : obstacle.getSquarePoints()) {
				if(point.dist(ball) < distance) {
					finalDestination = point;
					distance = point.dist(ball);
				}
				
				if(point.dist(robot) < distanceToRobot) {
					firstDestination = point;
					distanceToRobot = point.dist(robot);
				}
			}
			
			addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), firstDestination));
			addInstruction("travel", distanceToRobot);
			
			robot.getDirectionVector().setCoordinates((firstDestination.x * 2) - robot.x, (firstDestination.y * 2) - robot.y);
			robot.setCoordinates(firstDestination.x, firstDestination.y);
			
			Point currentPoint = firstDestination;
			
			System.out.println("firstDestination " + firstDestination);
			
			while(!currentPoint.equals(finalDestination)) {
				int index = obstacle.getSquarePoints().indexOf(currentPoint);
				
				switch(index) {
					case 0:
						currentPoint = obstacle.getSquarePoints().get(1);
						break;
					case 1:
						currentPoint = obstacle.getSquarePoints().get(3);
						break;
					case 2:
						currentPoint = obstacle.getSquarePoints().get(0);
						break;
					case 3:
						currentPoint = obstacle.getSquarePoints().get(2);
						break;
				}
				
				addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), currentPoint));
				addInstruction("travel", obstacle.getDiameter());
				
				robot.getDirectionVector().setCoordinates((currentPoint.x * 2) - robot.x, (currentPoint.y * 2) - robot.y);
				robot.setCoordinates(currentPoint.x, currentPoint.y);
				
				System.out.println("currentPoint = " + currentPoint);
			}
			System.out.println("finalDestination = " + finalDestination);
			
			addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), ball));
			addInstruction("travel", robot.dist(ball));
			
			robot.getDirectionVector().setCoordinates((ball.x * 2) - robot.x, (ball.y * 2) - robot.y);
			
			if(obstacle.isInside(ball)) {
				addInstruction("travel", (robot.dist(ball))*-1);
			} else {
				robot.setCoordinates(ball.x, ball.y);
			}	
			
			System.out.println("NewRobot =" + robot);
			
			return true;
		}
	}
	
	private void addInstruction(String operation, double value) {
		instructions.put(operation + operationNum, value);
		operationNum++;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	class Sort implements Comparator<Point>{

		@Override
		public int compare(Point point1, Point point2) { 
			return Double.compare(robot.dist(point1), robot.dist(point2));
		}
	}	
}
