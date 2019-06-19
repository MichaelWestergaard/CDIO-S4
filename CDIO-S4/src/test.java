import DTO.Ball;
import DTO.Point;
import DTO.Robot;

public class test<T> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Ball p = null;
		Robot robot = new Robot(157, 90);
		//System.out.println(GetDirection(new Point(84.0, 50.0), new Point(75.0,79.0), new Point(64.0, 51.0)));
		p = (Ball) projectObject(robot, "ball");
		
		System.out.println("P: " + p.x + " " + p.y);
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

	private static Point projectObject(Point point, String objectType) {
		double pointHeight;
		
		Robot robot = new Robot(point.x, point.y);
		Ball ball = new Ball(point.x, point.y);
		
		if(objectType.equalsIgnoreCase("Robot")) {
			pointHeight = 37.4;
		}else {
			pointHeight = 4;
		}
		
		double xDiff = Math.abs(point.x - 90);
		double yDiff = Math.abs(point.y - 60);
		
		double fakeRadius = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		double cameraAngel = Math.atan((fakeRadius/168.8));
		//cameraAngel = Math.toDegrees(cameraAngel);
		/* ERROR STARTS HERE maybe*/
		
		double radiusDiff = Math.tan(cameraAngel)*pointHeight;
		double realRadius = fakeRadius - radiusDiff;
		
		double slope = (point.y - 60)/(point.x - 90);
		double intersect = 60 - slope * 90;
		
		//double circleIntersect = Math.pow((x-90),2) + Math.pow((slope*x+intersect-60), 2) - realRadius*realRadius;
		
		double firstEquationPart = slope*slope + 1;
		double secondEquationPart = 2*slope*(intersect-60)-180;
		double thirdEquationPart = (intersect-60)*(intersect-60) - realRadius*realRadius + 8100;
		
		double circleIntersectionPos = (0-secondEquationPart + (Math.sqrt((secondEquationPart * secondEquationPart - 4*firstEquationPart*thirdEquationPart))))/(2*firstEquationPart);
		double circleIntersectionNeg = (0-secondEquationPart - Math.sqrt(Math.pow(secondEquationPart, 2) - 4*firstEquationPart*thirdEquationPart))/(2*firstEquationPart);
		
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
		
		double yForPos = slope * circleIntersectionPos + intersect;
		double yForNeg = slope * circleIntersectionNeg + intersect;
		System.out.println("y1: " + yForPos);
		System.out.println("y2: " + yForNeg);
		
		Point pPos = new Point(circleIntersectionPos, yForPos);
		Point pNeg = new Point(circleIntersectionNeg, yForNeg);
		
		double distToPPos = robot.dist(pPos);
		double distToPNeg = robot.dist(pNeg);

		System.out.println("dist pPos: " + distToPPos);
		System.out.println("dist pNeg: " + distToPNeg);
		
		if(pointHeight == 37.4) {
			if(distToPNeg > distToPPos) {
				robot.setCoordinates(circleIntersectionPos, yForPos);
			}else {
				robot.setCoordinates(circleIntersectionNeg, yForNeg);
			}
			System.out.println("robot coordinates: " + robot.x + " " + robot.y);
			return robot;
		}else {
			if(distToPNeg > distToPPos) {
				ball.setCoordinates(circleIntersectionPos, yForPos);
			}else {
				ball.setCoordinates(circleIntersectionNeg, yForNeg);
			}
			System.out.println("ball coordinates: " + ball.x + " " + ball.y);
			return ball;
		}
	}
}
