package DTO;

import java.util.ArrayList;
import java.util.List;

public class Robot extends Point {

	List<Ball> collectedBalls = new ArrayList<Ball>();
	
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
	
}