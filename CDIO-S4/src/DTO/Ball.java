package DTO;

import java.util.Arrays;

public class Ball extends Point {

	public Ball(double x, double y) {
		super(x, y);
	}

	@Override
	public String toString() {
		return "Ball coordinates = " + Arrays.toString(getCoordinates());
	}
	
	public boolean checkCoords(int x, int y) {
		if(this.x == x && this.y == y)
			return true;
		
		if((this.x-5 <= x && this.x+5 >= x) && (this.y-5 <= y && this.y+5 >= y))
			return true;
		
		return false;
	}
	
	public boolean isCloseToBorder() {
		if(this.x < 18 || this.x > 148 || this.y < 18 || this.y > 104) {
			return true;
		}
			
		return false;
	}
	
	public String getClosestBorder() {
		if(this.x < 18) {
			return "venstre";
		}
		if(this.x > 148) {
			return "højre";
		}
		if(this.y < 18) {
			return "top";
		}
		if(this.y > 104) {
			return "bund";
		}
		return null;
		
	}
	
	public boolean isInCorner() {
		if(this.x < 18 && this.y < 18)
			return true;
		if(this.x < 18 && this.y > 104)
			return true;
		if(this.x > 148 && this.y < 18)
			return true;
		if(this.x > 148 && this.y > 104)
			return true;
		
		return false;
	}
	
	public Point getCircleLineIntersectionPoint(Robot robot, double radius) {
		//double radius = 14;
		
        double baX = this.x - robot.x;
        double baY = this.y - robot.y;
        double caX = x - robot.x;
        double caY = y - robot.y;

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return null;
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point p1 = new Point(robot.x - baX * abScalingFactor1, robot.y
                - baY * abScalingFactor1);
        System.out.println("inside intersectionChecker " + p1);
        
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return p1;
        }
        Point p2 = new Point(robot.x - baX * abScalingFactor2, robot.y
                - baY * abScalingFactor2);

        System.out.println("inside intersectionChecker " + p2);
        
        if(Double.isNaN(p1.x) || Double.isNaN(p2.x))
        	return null;
        
        if(robot.dist(p1) < robot.dist(p2))
        	return p1;
        
        return p2;
    }
}
