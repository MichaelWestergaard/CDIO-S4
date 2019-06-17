package DTO;

import java.util.Arrays;

public class Obstacles extends Point{
	
	double diameter;

	public Obstacles(double x, double y) {
		super(x, y);
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
	
	public double getDiameter() {
		return diameter;
	}

	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}

	@Override
	public String toString() {
		return "Obstacle coordinates = " + Arrays.toString(getCoordinates());
	}
	
	
	
}
