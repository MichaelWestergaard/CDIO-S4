package DTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Robot extends Point {
	
	List<Ball> collectedBalls = new ArrayList<Ball>();
	Direction directionVector;
	
	public Robot(double x, double y) {
		super(x, y);
	}
	
	public Robot() {
		super(0,0);
	}

	public List<Ball> getCollectedBalls() {
		return collectedBalls;
	}
	
	public int getBallsCollected() {
		return collectedBalls.size();
	}

	public void collectBall(Ball ball) {
		collectedBalls.add(ball);
	}
	
	public Direction getDirectionVector() {
		return directionVector;
	}

	public void setDirectionVector(Direction directionVector) {
		this.directionVector = directionVector;
	}

	@Override
	public String toString() {
		return "Robot coordinates = " + Arrays.toString(getCoordinates()) ;
	}
	
}