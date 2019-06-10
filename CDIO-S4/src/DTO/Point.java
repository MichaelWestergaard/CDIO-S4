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
	
}