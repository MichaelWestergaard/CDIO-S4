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
			
			Point newDirectionCoordinates = rotateDirection(ball, true);

			// Set new robot coordinates and new direction
			robot.setCoordinates(ball.x, ball.y);
			robot.getDirectionVector().setCoordinates(newDirectionCoordinates.x, newDirectionCoordinates.y);
			return true;
		} else {
			System.out.println("Direct way is blocked, need to find an alternative route");	
			
			/*
			Point closestIntersectionPoint = null;
			
			double minDist = Integer.MAX_VALUE;
			
			for (Point point : intersectionPoints) {
				double tempDist = point.dist(ball);
				if(tempDist < minDist) {
					closestIntersectionPoint = point;
					minDist = tempDist;
				}
			}
			
			System.out.println("Need to go to " + closestIntersectionPoint.x + ", " + closestIntersectionPoint.y + " and then rotate and travel to " + ball);
			*/
			
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
	
	private Point rotateDirection(Point ballPoint, boolean findLongestDist) {		
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
		
		double yForPos = slope * circleIntersectionPos + intersect;
		double yForNeg = slope * circleIntersectionNeg + intersect;
		
		Point pPos = new Point(circleIntersectionPos, yForPos);
		Point pNeg = new Point(circleIntersectionNeg, yForNeg);
		
		double distToPPos = ballPoint.dist(pPos);
		double distToPNeg = ballPoint.dist(pNeg);
		
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
		
		return returnPoint;
	}
	
	private boolean inLine(Point a, Point b, Point c) {
		return (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y) == 0) ? true : false;
	}
	
	class Sort implements Comparator<Point>{

		@Override
		public int compare(Point point1, Point point2) { 
			return Double.compare(robot.dist(point1), robot.dist(point2));
		}
	}	
}
