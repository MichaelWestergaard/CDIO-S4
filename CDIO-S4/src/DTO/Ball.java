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
	
}
