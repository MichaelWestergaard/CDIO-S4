package DTO;

import java.util.Arrays;

public class Goal extends Point{

	public Goal(double x, double y) {
		super(x, y);
	}

	@Override
	public String toString() {
		return "Direction coordinates = " + Arrays.toString(getCoordinates());
	}
}
