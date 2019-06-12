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

	public double dist(Point point) {
		return Math.abs(Math.sqrt(Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2)));
	}

	public double calcAngle(Point dpoint, Point ballpoint) {

		return  Math.acos((dpoint.x * ballpoint.x + dpoint.y * ballpoint.y) / 
				((Math.sqrt(Math.pow(dpoint.x, 2) + Math.pow(dpoint.y,2))) 
				* Math.sqrt(Math.pow(ballpoint.x, 2) + Math.pow(ballpoint.y, 2))));
	}
	
	public double anglebetween(Point dpoint, Point ballpoint) {
		if(calcAngle(dpoint,ballpoint)>180) {
			return 360 - calcAngle(dpoint,ballpoint);
		}
		return calcAngle(dpoint,ballpoint);

	}
}
