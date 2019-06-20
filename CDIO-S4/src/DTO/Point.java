package DTO;

public class Point {

	public double x;
	public double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double[] getCoordinates() {
		return new double[] {x, y};
	}

	public void setCoordinates(double x, double y) {
		this.x = x;
		this.y = y;
	}
		
	public double[] createLine(Point point) {
		
		double a = point.y - y;
		double b = x - point.x;
		double c = a*x + b*y;
		
		double[] returnArray = {a, b, c};
		
		return returnArray;
	}

	public double dist(Point point) {
		return Math.abs(Math.sqrt(Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2)));
	}

	public double calcAngle(Point directionPoint, Point ballPoint) {
		Point directionVector, ballVector;
		directionVector = new Point(directionPoint.x - x,directionPoint.y - y);
		ballVector = new Point(ballPoint.x - x,ballPoint.y - y);
		
		/*return  Math.round(Math.toDegrees(Math.acos((directionVector.x * ballVector.x + directionVector.y * ballVector.y) / 
				((Math.sqrt(Math.pow(directionVector.x, 2) + Math.pow(directionVector.y,2))) 
				* Math.sqrt(Math.pow(ballVector.x, 2) + Math.pow(ballVector.y, 2)))))*100)/100;*/
		
		double directionAngle = Math.toDegrees(Math.atan2(directionVector.y, directionVector.x));		
		if(directionAngle < 0.0) {
			directionAngle += 360.0;
		} 
		
		double ballAngle = Math.toDegrees(Math.atan2(ballVector.y, ballVector.x));		
		if(ballAngle < 0.0) {
			ballAngle += 360.0;
		}
		
		double finalAngle = directionAngle - ballAngle;
		
		if(finalAngle > 180.0) {
			finalAngle -= 360.0;
		} else if(finalAngle < -180.0) {
			finalAngle += 360.0;
		}
		
		return finalAngle;
	}  
	
	public double angleBetween(Point dpoint, Point ballpoint) {
		/*if(calcAngle(dpoint,ballpoint)>180) {
			return Math.acos(360 - calcAngle(dpoint,ballpoint));
		}*/
		return calcAngle(dpoint,ballpoint)*-1;

	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
}
