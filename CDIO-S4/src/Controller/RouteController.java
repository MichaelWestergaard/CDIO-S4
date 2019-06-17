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
			
		while(i < iterator) {
			Collections.sort(balls, new Sort());
			
			Ball ball = balls.get(i);
			System.out.println(robot);
			if(getRoute(ball)) {
				balls.remove(ball);
				iterator = balls.size();
				//i++;
			} else {
				System.out.println("try again");
			}
		}
		
		return instructions;
	}
	
	private boolean getRoute(Ball ball) {
		boolean crossBlocking = false;

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

			if(obstacle.isInside(ball)) {
				System.out.println("ball is inside circle");
				
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
				
			} else {
				System.out.println("ball is outside circle");

				System.out.println("Need to go to " + ball);
				// Kør hen til uden for cirklen () rotate -> travel -> rotate travel indtil du har bolden.
			}
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
