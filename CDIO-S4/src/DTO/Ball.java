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
}
