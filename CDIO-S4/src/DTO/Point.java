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
		Point v1, v2;
		v1 = new Point(dpoint.x - x,dpoint.y - y);
		v2 = new Point(ballpoint.x - x,ballpoint.y - y);
		
		return  Math.round(Math.toDegrees(Math.acos((v1.x * v2.x + v1.y * v2.y) / 
				((Math.sqrt(Math.pow(v1.x, 2) + Math.pow(v1.y,2))) 
				* Math.sqrt(Math.pow(v2.x, 2) + Math.pow(v2.y, 2)))))*100)/100;
		}  
	
	public double angleBetween(Point dpoint, Point ballpoint) {
		if(calcAngle(dpoint,ballpoint)>180) {
			return Math.acos(360 - calcAngle(dpoint,ballpoint));
		}
		return calcAngle(dpoint,ballpoint);

	}
}
