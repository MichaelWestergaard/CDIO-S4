package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.text.DecimalFormat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DTO.Ball;
import DTO.Direction;
import DTO.Point;
import DTO.Robot;

class PointTest {

	Robot robot;
	Ball ball;
	
	@BeforeEach
	void setUp() throws Exception {
		robot = new Robot(0,0);
		robot.setDirectionVector(new Direction(1, 0));
		ball = new Ball(20,5);
	}

	@Test
	void distanceTest() {
		double dist = robot.dist(ball);

		double expected = 20.61;
		double actual = (int)(dist*100) / 100.0; // afrunding
		
		//System.out.println(actual);
		
		assertEquals(expected, actual);
	}
	
	@Test
	void distanceTestReverse() {
		double dist = ball.dist(robot);

		double expected = 20.61;
		double actual = (int)(dist*100) / 100.0; // afrunding
		
		//System.out.println(actual);
		
		assertEquals(expected, actual);
	}
	
	@Test
	void angleTest() {
		robot = new Robot(0,0);
		robot.setDirectionVector(new Direction(1,0));
		ball = new Ball(-5,-5);
		
		double angle = robot.calcAngle(robot.getDirectionVector(), ball);

		double expected = 135.0;
		double actual = (int)(angle*100) / 100.0; // afrunding
		
		System.out.println(actual);
		
		assertEquals(expected, actual);
	}
	
	@Test
	void angleTestLeft() {
		
		double angle = robot.calcAngle(robot.getDirectionVector(), ball);

		double expected = -14.03;
		double actual = (int)(angle*100) / 100.0; // afrunding
		
		System.out.println(actual);
		
		assertEquals(expected, actual);
	}

}
