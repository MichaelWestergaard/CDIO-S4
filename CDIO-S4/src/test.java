import DTO.Point;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(GetDirection(new Point(84.0, 50.0), new Point(75.0,79.0), new Point(64.0, 51.0)));
		
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

}
