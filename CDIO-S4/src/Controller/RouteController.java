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
import java.util.List;
import java.util.Map;
import java.lang.System;

import org.opencv.core.Mat;

import DTO.Ball;
import DTO.Goal;
import DTO.Obstacles;
import DTO.Point;
import DTO.Robot;

public class RouteController {
	
	int operationNum = 0;
	Map<String, Double> instructions = new HashMap<String, Double>();
	List<Ball> balls = null;
	Obstacles obstacle = null;
	Goal goal;
	Robot robot;

	private boolean ready = true;
	private boolean isConnected = false;
	BufferedReader reader;
	ObjectOutputStream mapOutputStream;
	OutputStream outputStream;
	private Point goalPointHelper;
	private int noBallsFoundCounter = 0;
	
	Socket socket;
	String readline;
	boolean moreBalls = true;
	int toGoalCounter = 0;
	boolean wentToGoalFirstTime = false;
	boolean wentToGoalSecondTime = false;
	boolean wentToGoalThirdTime = false;
		
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

	public Map<String, Double> getInstruction(List<Ball> loadBalls, Obstacles obstacleLoad, Robot robot, Goal goal){
		operationNum = 0;
		
		if(obstacle == null) {
			this.obstacle = obstacleLoad;
		}
		List<Point> squarePoints = new ArrayList<Point>();
		

		Point point1 = new Point(obstacle.x-25, obstacle.y-25);
		Point point2 = new Point(obstacle.x+25, obstacle.y-25);
		Point point3 = new Point(obstacle.x-25, obstacle.y+25);
		Point point4 = new Point(obstacle.x+25, obstacle.y+25);
		
		squarePoints.add(point1);
		squarePoints.add(point2);
		squarePoints.add(point3);
		squarePoints.add(point4);

		obstacle.setSquarePoints(squarePoints);

		this.goal = goal;
		if(robot != null) {
			this.robot = robot;
		} else {
			robot = this.robot;
		}
		this.balls = loadBalls;
		
		instructions.clear();
		
		goalPointHelper = new Point(goal.x+20,goal.y);
		
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
		Collections.sort(balls, new Sort());
		
		if(goToGoalTime()) {
			if(!wentToGoalFirstTime) {
				wentToGoalFirstTime = true;
				instructionsToGoal();
			} else if(!wentToGoalSecondTime) {
				wentToGoalSecondTime = true;
				instructionsToGoal();
			} else if(!wentToGoalThirdTime) {
				wentToGoalThirdTime = true;
				instructionsToGoal();
			}
		}
		
		/*for(int j = 0; i < balls.size(); j++) {
			if(robot.dist(balls.get(j)) < 10) {
				balls.remove(j);
				continue;
			}
		}*/
		
		while(i < iterator) {
			if(robot.dist(balls.get(i)) < 10 || obstacle.isInside(balls.get(i))) {
				balls.remove(i);
				iterator = balls.size();
			} else {
				i++;
			}
		}
		
		if(!balls.isEmpty()) {
			Ball ball = balls.get(0);
			getRoute(ball);
			iterator = balls.size();
		} 
		System.out.println("" + instructions);
		return instructions;
	}
	
	private void instructionsToGoal() {
		instructions.clear();
		toGoalCounter++;
		operationNum = 0;
		instructions = new HashMap<String, Double>();
		
		System.out.println("instructiontoGoal print: " + goal + " " + goalPointHelper + " " + robot);
		
		boolean crossBlocking = false;
		
		List<Point> intersectionPoints = obstacle.getCircleLineIntersectionPoint(robot, goalPointHelper);

		obstacle.getCircleEquation();
		
		System.out.println("intersectionPoints goal = " + intersectionPoints);
		
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
			addInstruction("travel", 15);
		} else {
			System.out.println("goal Direct way is blocked, need to find an alternative route");	
			
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
				addInstruction("travel", robot.dist(currentPoint));
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
			addInstruction("traveS", robot.dist(goal));
			
			robot.getDirectionVector().setCoordinates((goal.x * 2) - robot.x, (goal.y * 2) - robot.y);
			robot.setCoordinates(goal.x, goal.y);			
			
			System.out.println("NewRobot =" + robot);
		}
		addInstruction("delive", 0.0);
		addInstruction("backwa", 20.0);
		robot.setCoordinates(goal.x + 20, goal.y);
		//sendInstructions();
	}
	
	private boolean goToGoalTime() {
		long endTime = System.currentTimeMillis();
		long secondsTotal = (endTime - robot.getStartTime()) / 1000;
		long secondsSpent = secondsTotal % 60;
		long minutesSpent = (secondsTotal - secondsSpent) / 60;
		
		if(minutesSpent > 3 || minutesSpent > 5 || minutesSpent > 7)
			return true;
		
		return false;
	}
	
	public void sendInstructions() {
		if(!isConnected) {
			socketInit();
		}
	    System.out.println("You entered sendInstructions");
		ready = false;

		if (balls.size() == 0) {
			// Find vej til mål
			if (noBallsFoundCounter > 0) {
				if(toGoalCounter > 2) {
					instructions.clear();
					addInstruction("finish", 0.0);
					long endTime = System.currentTimeMillis();
					robot.setEndTime(endTime);
					long secondsTotal = (endTime - robot.getStartTime()) / 1000;
					long secondsSpent = secondsTotal % 60;
					long minutesSpent = (secondsTotal - secondsSpent) / 60;
					
					System.out.println("Robot stopped, total time spent: " + minutesSpent + " min " + secondsSpent + " s");
				} else {
					System.out.println("Driving to goal");					
					instructionsToGoal();
				}
			} else {
				noBallsFoundCounter++;
				addInstruction("backwa", 10.0);
				System.out.println("no more balls");
			}
		}

		String line = null;
		System.out.println(instructions);

		try {
			mapOutputStream.writeObject(instructions);
		    mapOutputStream.flush();
		    mapOutputStream.reset();

		    if(instructions.containsKey("finish")) {
		    	System.exit(0);
		    }
		    
			instructions.clear();

		    while(true) {
		    	line = reader.readLine();
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
		toGoalCounter = 0;
		boolean crossBlocking = false;

		System.out.println("robot intersect " + robot + " checking ball " + ball);
		obstacle.getCircleEquation();
		
		List<Point> intersectionPoints = obstacle.getCircleLineIntersectionPoint(robot, ball);

		System.out.println(intersectionPoints);
		
		if(!intersectionPoints.isEmpty()) {
			crossBlocking = true;
		}
		
		if(!crossBlocking) {
			if(ball.isCloseToBorder()) {

				Point borderHelper = new Point(0,0);

			//	if(ball.isInCorner()) {
					/* DO SOMETHING ELSE IN THE FUTURE*/

				/*	System.out.println("Kører til en bold i et hjørne");

					addInstruction("travel", robot.dist(ball));

					// Set new robot coordinates and new direction
					robot.getDirectionVector().setCoordinates((ball.x * 2) - robot.x, (ball.y * 2) - robot.y);

					Point shorterRobotPoint = ball.getCircleLineIntersectionPoint(robot, 7);
					robot.setCoordinates(shorterRobotPoint.x, shorterRobotPoint.y);					

				} else {*/
					System.out.println("Kører til en bold ved en bande");
					switch (ball.getClosestBorder()) {
					case "venstre":
						borderHelper.setCoordinates(ball.x + 27.5, ball.y);
						break;
					case "højre":
						borderHelper.setCoordinates(ball.x -21, ball.y);
						break;
					case "top":
						borderHelper.setCoordinates(ball.x, ball.y + 26.5);
						break;
					case "bund":
						borderHelper.setCoordinates(ball.x, ball.y - 26.5);
						break;
					default:
						System.out.println("Entered default in routeController getClosestBorder");
						break;
					}

					addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), borderHelper));
					addInstruction("travel", robot.dist(borderHelper));

					robot.getDirectionVector().setCoordinates((borderHelper.x * 2) - robot.x, (borderHelper.y * 2) - robot.y);
					robot.setCoordinates(borderHelper.x, borderHelper.y);	
				//}
				System.out.println("Helper: " + borderHelper + "ball: " + ball + "robot: " + robot);
				addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), ball));
				addInstruction("traveS", 17);
				addInstruction("backwa", 17);

				robot.getDirectionVector().setCoordinates((ball.x * 2) - robot.x, (ball.y * 2) - robot.y);				
				
			} else {
				addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), ball));
				addInstruction("travel", robot.dist(ball));
			
				robot.getDirectionVector().setCoordinates((ball.x * 2) - robot.x, (ball.y * 2) - robot.y);				
				robot.setCoordinates(ball.x, ball.y);
			}

			return true;

		} else {
			System.out.println("Direct way is blocked, need to find an alternative route");	

			for(Point point : obstacle.getSquarePoints()) {
				System.out.println("getInstruction square: " + point);
			}
			
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
				addInstruction("travel", robot.dist(currentPoint));
				
				robot.getDirectionVector().setCoordinates((currentPoint.x * 2) - robot.x, (currentPoint.y * 2) - robot.y);
				robot.setCoordinates(currentPoint.x, currentPoint.y);
				
				System.out.println("currentPoint = " + currentPoint);
			}
			System.out.println("finalDestination = " + finalDestination);
			
			if(ball.isCloseToBorder()) {

				Point borderHelper = new Point(0,0);

				//if(ball.isInCorner()) {
					/* DO SOMETHING ELSE IN THE FUTURE*/

					/*System.out.println("Kører til en bold i et hjørne");

					addInstruction("travel", robot.dist(ball)-7);

					// Set new robot coordinates and new direction
					robot.getDirectionVector().setCoordinates((ball.x * 2) - robot.x, (ball.y * 2) - robot.y);

					Point shorterRobotPoint = ball.getCircleLineIntersectionPoint(robot, 7);
					robot.setCoordinates(shorterRobotPoint.x, shorterRobotPoint.y);					

				} else {*/
					System.out.println("Kører til en bold ved en bande");
					switch (ball.getClosestBorder()) {
					case "venstre":
						borderHelper.setCoordinates(ball.x + 15, ball.y);
						break;
					case "højre":
						borderHelper.setCoordinates(ball.x -15, ball.y);
						break;
					case "top":
						borderHelper.setCoordinates(ball.x, ball.y + 15);
						break;
					case "bund":
						borderHelper.setCoordinates(ball.x, ball.y - 15);
						break;
					default:
						System.out.println("Entered default in routeController getClosestBorder");
						break;
					}

					addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), borderHelper));
					addInstruction("travel", robot.dist(borderHelper));

					robot.getDirectionVector().setCoordinates((borderHelper.x * 2) - robot.x, (borderHelper.y * 2) - robot.y);
					robot.setCoordinates(borderHelper.x, borderHelper.y);	
				//}
				
				addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), ball));
				addInstruction("traveS", robot.dist(ball));
				addInstruction("backwa", robot.dist(ball));

				robot.getDirectionVector().setCoordinates((ball.x * 2) - robot.x, (ball.y * 2) - robot.y);				
				
			} else {
				addInstruction("rotate", robot.angleBetween(robot.getDirectionVector(), ball));
				addInstruction("travel", robot.dist(ball));
			
				robot.getDirectionVector().setCoordinates((ball.x * 2) - robot.x, (ball.y * 2) - robot.y);				

				if(obstacle.isInside(ball)) {
					addInstruction("travel", (robot.dist(ball))*-1);
				} else {
					robot.setCoordinates(ball.x, ball.y);
				}	
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
	
	public void reverseRobot() {
		instructions.clear();
		instructions.put("backwa", 10.0);
	}
	
	class Sort implements Comparator<Point>{

		@Override
		public int compare(Point point1, Point point2) { 
			return Double.compare(robot.dist(point1), robot.dist(point2));
		}
	}	
}
