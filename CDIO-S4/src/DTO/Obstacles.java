package DTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Obstacles extends Point{
	
	double diameter;
	List<Point> squarePoints;

	public Obstacles(double x, double y) {
		super(x, y);
	}
	
	public void getCircleEquation() {
		System.out.println("(x-"+x+")^2+(y-"+y+")^2 = "+ Math.pow(diameter/2,2));
	}
	
	public boolean isOnCircle(Point point) {
		if(Math.pow(point.x-x, 2) + Math.pow(point.y-y, 2) == Math.pow(diameter/2, 2))
			return true;
		return false;
	}
	
	public boolean isInside(Point point) {
		if(this.dist(point) <= diameter/2)
			return true;
		return false;
	}
	
	public List<Point> getCircleLineIntersectionPoint(Point pointA, Point pointB) {
		double radius = diameter;
		
        double baX = pointB.x - pointA.x;
        double baY = pointB.y - pointA.y;
        double caX = x - pointA.x;
        double caY = y - pointA.y;

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point p1 = new Point(pointA.x - baX * abScalingFactor1, pointA.y
                - baY * abScalingFactor1);
        System.out.println("inside intersectionChecker " + p1);
        
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Point p2 = new Point(pointA.x - baX * abScalingFactor2, pointA.y
                - baY * abScalingFactor2);

        System.out.println("inside intersectionChecker " + p2);
        
        if(Double.isNaN(p1.x) || Double.isNaN(p2.x))
        	return Collections.emptyList();
        
        return Arrays.asList(p1, p2);
    }
		
	public double getDiameter() {
		return diameter;
	}

	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}

	public List<Point> getSquarePoints() {
		return squarePoints;
	}

	public void setSquarePoints(List<Point> squarePoints) {
		this.squarePoints = squarePoints;
	}

	@Override
	public String toString() {
		return "Obstacle coordinates = " + Arrays.toString(getCoordinates());
	}
	
	
	
}
