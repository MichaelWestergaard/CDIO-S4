package Controller;

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
	List<Obstacles> obstacles;
	Goal goal;
	Robot robot;
	

	int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
	int xSize;
	int ySize;
	
	List<Point> visitedCoordinates = new ArrayList<Point>();
	
	boolean routeFound = false;
	boolean alternativeRouteFound = false;

	public Map<String, Double> getInstruction(List<Ball> balls, List<Obstacles> obstacles, Robot robot, Goal goal){
		
		instructions = new HashMap<String, Double>();
		this.obstacles = obstacles;
		this.goal = goal;
		this.robot = robot;
		robot.setDirectionVector(new Direction(19,19));
		
		Collections.sort(balls, new Sort());
				
		int iterator = balls.size();
		int i = 0;
			
		while(i < iterator) {
			Collections.sort(balls, new Sort());
			
			Ball ball = balls.get(i);
			
			if(getRoute(ball)) {
				balls.remove(ball);
				i++;
			} else {
				System.out.println("try again");
			}
		}
		
		return instructions;
	}
	
	private boolean getRoute(Ball ball) {
		routeFound = true;
		
		System.out.println(ball);
		System.out.println(robot);
		
		List<Obstacles> obstaclesBlocking = new ArrayList<Obstacles>();
		
		for (Obstacles obstacle : obstacles) {
			if(inLine(ball, robot, obstacle)) {
				obstaclesBlocking.add(obstacle);
				System.out.println(obstacle);
			}
		}
		
		if(obstaclesBlocking.isEmpty()) {
			addInstruction("rotate", getRotationValue(ball));
			if(routeFound) {
				addInstruction("travel", robot.dist(ball));
				robot.setCoordinates(ball.x, ball.y);
				return true;
			} else {
				return false;
			}
		} else {
			getAlternativeRoute(ball);
			return true;
		}
	}
	
	private void getAlternativeRoute(Ball ball) {
		List<Point> path = new ArrayList<>();
		xSize = (int) (ball.x >= robot.x ? ball.x : robot.x);
		ySize = (int) (ball.y >= robot.y ? ball.y : robot.y);
		
		if(checkPath((int)robot.x, (int)robot.y, ball, path)) {
			// Found an alternative path
			//TODO: Find den hurtigste vej ud fra path og lav instructions
			for (Point point : path) {
				System.out.println(point.x + ", " + point.y);
			}
		}
	}
	
	private boolean checkPath(int x, int y, Ball ball, List<Point> path) {
		
		if(alternativeRouteFound)
			return true;
		
		// If coordinate is out of map
		if(x > xSize || y > ySize || x < 0 || y < 0)
			return false;
		
		// If coordinate have been visisted
		for(Point visitedPoint : visitedCoordinates) {
			if(visitedPoint.x == x && visitedPoint.y == y) {
				return false;
			}
		}
		
		// If coordinate is obstacle
		for (Obstacles obstacle : obstacles) {
			if(obstacle.x == x && obstacle.y == y) {
				return false;
			}
		}
		
		Point point = new Point(x,y);
		
		path.add(point);
		visitedCoordinates.add(point);
		
		if(ball.x == x && ball.y == y) {
			alternativeRouteFound = true;
			return true;
		}
		
		
		for(int[] direction : directions) {
			if(checkPath(x+direction[0], y+direction[1], ball, path))
				return true;
		}
		
		path.remove(path.size() - 1);
		return false;
	}
	
	private void addInstruction(String operation, double value) {
		instructions.put(operation + operationNum, value);
		operationNum++;
	}
	
	private double getRotationValue(Ball ball) {
		double rotateDegrees = 0;
		int direction = getDirection(robot.getDirectionVector(), ball, robot);
		
		if(direction > 0) {
			rotateDegrees = robot.angleBetween(robot.getDirectionVector(), ball);
		} else if(direction < 0) {
			rotateDegrees = 0-robot.angleBetween(robot.getDirectionVector(), ball);
		} else {
			rotateDegrees = 10.0;
			//TODO: Indsæt Tims nye metode til at bestemme retningsvektor
			double newDirectionX = Math.cos(10.0 * robot.x) - Math.sin(10.0 * robot.y);
			double newDirectionY = Math.sin(10.0 * robot.x) + Math.cos(10.0 * robot.y);
			robot.getDirectionVector().setCoordinates(newDirectionX, newDirectionY);
			routeFound = false;
		}
		
		return rotateDegrees;
	}
	
	private boolean inLine(Point a, Point b, Point c) {
		/*
		if(a.x == c.x)
			return b.x == c.x;
		
		if(a.y == c.y)
			return b.y == c.y;
		
		return (a.x - c.x)*(a.y-c.y) == (c.x - b.x)*(c.y-b.y);
		*/

		return (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y) == 0) ? true : false;
	}
	
	public int getDirection(Point direction, Point ball, Point robot)
	{
		direction.y *= -1;
		ball.y *= -1;
		robot.y *= -1;
		
	    double theta1 = getAngle(direction, ball); 
	    double theta2 = getAngle(ball, robot);
	    double delta = normalizeAngle(theta2 - theta1);

	    if ( delta == 0 || delta == Math.PI) {
	    	return 0;
	    }
	    else if ( delta < Math.PI )
	        return -1;
	    else return 1;
	}

	private Double getAngle(Point p1, Point p2)
	{
	    Double angleFromXAxis = Math.atan((p2.y - p1.y ) / (p2.x - p1.x ) ); // where y = m * x + K
	    return  p2.x - p1.x < 0 ? angleFromXAxis + Math.PI : angleFromXAxis; // The will go to the correct Quadrant
	}

	private Double normalizeAngle(Double angle)
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
