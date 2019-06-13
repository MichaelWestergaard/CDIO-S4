package DTO;

import java.util.Arrays;

public class Obstacles extends Point{

	public Obstacles(double x, double y) {
		super(x, y);
	}
	
	@Override
	public String toString() {
		return "Obstacle coordinates = " + Arrays.toString(getCoordinates());
	}
}
