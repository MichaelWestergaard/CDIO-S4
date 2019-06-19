package Controller;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Controller.MapController.Sort;
import DTO.Ball;
import DTO.Direction;
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
	
	Socket socket;
	String readline;
	Map<String, Double> myMap = new HashMap<String, Double>();
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
	
	//TODO: ROTATE SQUARE POINTS + INSTRUCTIONS TIL GOAL

	public Map<String, Double> getInstruction(List<Ball> balls, Obstacles obstacle, Robot robot, Goal goal){
		if(!isConnected) {
			socketInit();
		}
		
		instructions = new HashMap<String, Double>();
		this.obstacle = obstacle;
		this.goal = goal;
		this.robot = robot;
		
		Collections.sort(balls, new Sort());
				
		int iterator = balls.size();
		int i = 0;
			
		System.out.println("Balls = " + balls);
		System.out.println("Obstacle = " + obstacle);
		System.out.println("Diameter = " + obstacle.getDiameter());
		System.out.println("Square = " + obstacle.getSquarePoints().get(0) + " | " + obstacle.getSquarePoints().get(1) + " | " + obstacle.getSquarePoints().get(2) + " | " + obstacle.getSquarePoints().get(3));
		System.out.println("robot = " + robot);		
		System.out.println("Direction = " + robot.getDirectionVector());
		
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
	
	private void sendInstructions(Map<String, Double> instructions) {
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
