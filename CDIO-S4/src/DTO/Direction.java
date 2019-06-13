package DTO;

import java.util.Arrays;

public class Direction extends Point{

	public Direction(double x, double y) {
		super(x, y);
	}

	@Override
	public String toString() {
		return "Direction coordinates = " + Arrays.toString(getCoordinates());
	}
	
	

}
