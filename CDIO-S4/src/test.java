import java.util.ArrayList;
import java.util.List;

import Controller.RouteController;
import DTO.Point;
import DTO.Robot;
import DTO.Ball;
import DTO.Goal;
import DTO.Obstacles;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//System.out.println(GetDirection(new Point(84.0, 50.0), new Point(75.0,79.0), new Point(64.0, 51.0)));
		//projectObject(new Point(157,90));
		
		List<Ball> balls = new ArrayList<Ball>();
		balls.add(new Ball(1,1));
		
		new RouteController().getInstruction(balls, new Obstacles(2,2), new Robot(5,5), new Goal(5,1));
		
	}
	
	public static String GetDirection(Point direction, Point ball, Point robot)
	{
	    double theta1 = GetAngle(direction, ball); 
	    double theta2 = GetAngle(ball, robot);
	    double delta = NormalizeAngle(theta2 - theta1);

	    if ( delta == 0 || delta == Math.PI) {
	    	return "";
	    }
	    else if ( delta < Math.PI )
	        return "Venstre";
	    else return "Højre";
	}

	private static Double GetAngle(Point p1, Point p2)
	{
	    Double angleFromXAxis = Math.atan((p2.y - p1.y ) / (p2.x - p1.x ) ); // where y = m * x + K
	    return  p2.x - p1.x < 0 ? angleFromXAxis + Math.PI : angleFromXAxis; // The will go to the correct Quadrant
	}

	private static Double NormalizeAngle(Double angle)
	{
	    return angle < 0 ? angle + 2 * Math.PI : angle; //This will make sure angle is [0..2PI]
	}

	private static void projectObject(Point point) {
		double xDiff = Math.abs(point.x - 90);
		double yDiff = Math.abs(point.y - 60);
		
		double fakeRadius = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		double cameraAngel = Math.toDegrees(Math.atan((fakeRadius/168.8)));
		
		/* ERROR STARTS HERE maybe*/
		
		double radiusDiff = Math.toDegrees(Math.tan(cameraAngel)) * 37.4;
		double realRadius = fakeRadius - radiusDiff;
		
		double slope = (point.y - 60)/(point.x - 90);
		double intersect = 60 - slope * 90;
		
		//double circleIntersect = Math.pow((x-90),2) + Math.pow((slope*x+intersect-60), 2) - realRadius*realRadius;
		
		double firstEquationPart = slope*slope + 1;
		double secondEquationPart = 2*slope*(intersect-60)-180;
		double thirdEquationPart = (intersect-60)*(intersect-60) - realRadius*realRadius + 8100;
		
		double circleIntersectionPos = (-secondEquationPart + Math.sqrt(Math.pow(secondEquationPart, 2) - 4*firstEquationPart*thirdEquationPart))/2*firstEquationPart;
		double circleIntersectionNeg = (-secondEquationPart - Math.sqrt(Math.pow(secondEquationPart, 2) - 4*firstEquationPart*thirdEquationPart))/2*firstEquationPart;
		
		System.out.println("fakeRadius: " + fakeRadius);
		System.out.println("Camera Angel: " + cameraAngel);
		System.out.println("radiusDiff: " + radiusDiff);
		System.out.println("realRadius: " + realRadius);
		System.out.println("slope: " + slope);
		System.out.println("intersect: " + intersect);
		System.out.println("a: " + firstEquationPart);
		System.out.println("b: " + secondEquationPart);
		System.out.println("c " + thirdEquationPart);
		
		System.out.println("First intersection x-coordinate: " + circleIntersectionPos);
		System.out.println("Second intersection x-coordinate: " + circleIntersectionNeg);
		
	}
	
}
