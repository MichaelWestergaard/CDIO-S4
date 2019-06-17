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
	Obstacles obstacle;
	Goal goal;
	Robot robot;
	

	int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
	int xSize;
	int ySize;
	
	List<Point> visitedCoordinates = new ArrayList<Point>();
	
	boolean routeFound = false;
	boolean alternativeRouteFound = false;

	public Map<String, Double> getInstruction(List<Ball> balls, Obstacles obstacle, Robot robot, Goal goal){
		
		instructions = new HashMap<String, Double>();
		this.obstacle = obstacle;
		this.goal = goal;
		this.robot = robot;
		robot.setDirectionVector(new Direction(4,4));
		
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
		boolean crossBlocking = false;
		
		System.out.println(ball);
		System.out.println(robot);
		
		if(inLine(ball, robot, obstacle)) {
			crossBlocking = true;
		}
		
		if(!crossBlocking && !obstacle.isInside(ball)) {
			addInstruction("rotate", getRotationValue(ball));
			if(routeFound) {
				addInstruction("travel", robot.dist(ball));
				robot.setCoordinates(ball.x, ball.y);
				return true;
			} else {
				return false;
			}
		} else {
			//getAlternativeRoute(ball);
			//Find circle
			System.out.println(newDirectionOnCircle(ball));
			return true;
		}
	}
	
	private Point newDirectionOnCircle(Point ballPoint) {
		Point returnPoint = new Point(0,0);

		double xDiff = Math.abs(robot.getDirectionVector().x - robot.x);
		double yDiff = Math.abs(robot.getDirectionVector().y - robot.y);

		double realRadius = Math.sqrt(xDiff*xDiff + yDiff*yDiff);

		double slope = (ballPoint.y - robot.y)/(ballPoint.x - robot.x);
		double intersect = robot.y - slope * robot.x;

		double firstEquationPart = slope*slope + 1;
		double secondEquationPart = 2*slope*(intersect-robot.y)-(2 * robot.x);
		double thirdEquationPart = (intersect-robot.y)*(intersect-robot.y) - realRadius*realRadius + robot.x*robot.x;

		double circleIntersectionPos = (0-secondEquationPart + (Math.sqrt((secondEquationPart * secondEquationPart - 4*firstEquationPart*thirdEquationPart))))/(2*firstEquationPart);
		double circleIntersectionNeg = (0-secondEquationPart - Math.sqrt(Math.pow(secondEquationPart, 2) - 4*firstEquationPart*thirdEquationPart))/(2*firstEquationPart);


		System.out.println("slope: " + slope);
		System.out.println("intersect: " + intersect);
		System.out.println("a: " + firstEquationPart);
		System.out.println("b: " + secondEquationPart);
		System.out.println("c " + thirdEquationPart);
		
		System.out.println("First intersection x-coordinate: " + circleIntersectionPos);
		System.out.println("Second intersection x-coordinate: " + circleIntersectionNeg);

		double yForPos = slope * circleIntersectionPos + intersect;
		double yForNeg = slope * circleIntersectionNeg + intersect;

		Point pPos = new Point(circleIntersectionPos, yForPos);
		Point pNeg = new Point(circleIntersectionNeg, yForNeg);

		double distToPPos = ballPoint.dist(pPos);
		double distToPNeg = ballPoint.dist(pNeg);

		if(distToPNeg < distToPPos) {
			returnPoint.setCoordinates(circleIntersectionPos, yForPos);

		}else {
			returnPoint.setCoordinates(circleIntersectionNeg, yForNeg);
		}
		System.out.println("Point coordinates: " + returnPoint.x + " " + returnPoint.y);
		return returnPoint;
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
