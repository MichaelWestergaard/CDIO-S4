package Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DTO.Ball;
import DTO.DeliveryPoint;
import DTO.Direction;
import DTO.Goal;
import DTO.Point;
import DTO.Robot;

public class MapController {

	List<Ball> balls = new ArrayList<Ball>();
	List<DeliveryPoint> deliveryPoints = new ArrayList<DeliveryPoint>();
	List<Ball> shortestPath = new ArrayList<Ball>();
	ArrayList<Ball> coordinates = new ArrayList<Ball>();

	Robot robot;
	Direction directionVector, goalPoint;
	Goal smallGoal, bigGoal;

	Map<String, Double> instructionMap = new HashMap <String, Double>();

	public void findBalls(int[][] map) {
		//int ballcounter;
		boolean ballStatus = false;
		//lille mål
		/*map[0][56] = 5;
		map[0][57] = 5;
		map[0][58] = 5;
		map[0][59] = 5;
		*/
		map[0][60] = 5; //centrum af mål
		/*map[0][61] = 5;
		map[0][62] = 5;
		map[0][63] = 5;
		map[0][64] = 5;
		 */
		map[3][60] = 3; //retningspunkt foran mål
		map[3][55] = 4; //punktet robotten skal dreje fra
		
		//store mål
		/*map[179][52] = 5;
		map[179][53] = 5;
		map[179][54] = 5;
		map[179][55] = 5;
		map[179][56] = 5;
		map[179][57] = 5;
		map[179][58] = 5;
		map[179][59] = 5;
		map[179][60] = 5; // centrum af mål
		map[179][61] = 5;
		map[179][62] = 5;
		map[179][63] = 5;
		map[179][64] = 5;
		map[179][65] = 5;
		map[179][66] = 5;
		map[179][67] = 5;
		map[179][68] = 5;
		*/
		for (int x = 0; x < map.length; x++) { //r�kkerne
			for (int y = 0; y < map[x].length; y++) { //kolonnerne
				
			//	System.out.print(map[x][y]);

				if(map[x][y] == 9) {
					robot = new Robot(x,y);
<<<<<<< HEAD
					
=======
					System.out.println("Robot:   " +robot);
>>>>>>> branch 'Angle' of https://github.com/MichaelWestergaard/CDIO-S4.git
				}
				if(map[x][y] == 3) {
					directionVector = new Direction(x,y);
					System.out.println("Retning :   " +directionVector);
				}
				if(map[x][y] == 4) {
					goalPoint = new Direction(x,y);
				}

				if(map[x][y] == 5) {
						smallGoal = new Goal(x,y);
					 /*else if (map[x+15][y] == 5) {
						bigGoal = new Goal(x,y);
					}*/
				}

				if(map[x][y] == 1) {
					if(map[x+3][y] == 1 && map[x][y+3] == 1 && map[x+3][y+3] == 1) {

						ballStatus = true;
						
						map[x][y] = 9;
						map[x+1][y] = 0;
						map[x][y+1] = 0;
						map[x+1][y+1] = 0;

						coordinates.add(new Ball(x,y));
					}
				}

			}	
			System.out.println();
		}
		findShortestPath();	
	}

	int[][] map = null;
	public MapController(int[][] loadMap) {
		//det her array er nu bestemt ud fra billedet fra opencv.
		map = loadMap;

		//System.out.println("int[][] map = new int[][]{");
		/*for (int i = 0; i < 180; i++) {
			System.out.print("{ ");
			for (int j = 0; j < 120; j++) {
				//System.out.print(map[i][j] + "");
				if(j+1 != 120) {
					System.out.print(", ");
				}
			}
			System.out.print(" }");
			if(i+1 != 180) {
				System.out.print(",");
			}
			System.out.println();
		}
		System.out.println("};");*/
	}

	public MapController() {
		// TODO Auto-generated constructor stub
	}

	public int[][] locateDeliveryPoints(int[][] map) {
		//System.out.println(coordinates);
		return map; 
	}

	public void findShortestPath() {
		Collections.sort(coordinates, new Sort());

		int iterator = coordinates.size();
		int operationNum = 0;

		
		
		for(int i = 0; i < iterator; i++) {
			Collections.sort(coordinates, new Sort());
			double[] test = coordinates.get(0).getCoordinates();


			for(int j = 0; j < coordinates.size(); j++) {
				System.out.println("robot "+ robot+ "  direction  " + directionVector);
				System.out.println(coordinates.get(j)+ " dist = " + robot.dist(coordinates.get(j)) + " angle = " + robot.angleBetween(directionVector, coordinates.get(j)));
				instructionMap.put("travel " + operationNum, robot.dist(coordinates.get(j)));
				// instructionMap.put("rotate" + (operationNum + 1), robot.angleBetween(directionVector, coordinates.get(j)));
				operationNum += 2;
			}
		
			System.out.println("Antal bolde der mangler at blive besøgt: "+ coordinates.size());

			directionVector.setCoordinates((test[0] + test[0] - robot.x)  , (test[1] + test[1] - robot.y ));
			robot.setCoordinates(test[0], test[1]);	

			
			// drej 180 efter hvert
			//	directionVector.setCoordinates(robot.x * 0.5, robot.y * 0.5);

			coordinates.remove(0);
			
			if(coordinates.size() == 0) {
				
				System.out.println("dist to goalPoint: " + robot.dist(goalPoint) + " angle to goalPoint: " + robot.angleBetween(directionVector, goalPoint));
				System.out.println("Angle turning towards the goal" + robot.angleBetween(goalPoint, smallGoal));
			}
			
		}
		
		System.out.println(instructionMap);
	}

	class Sort implements Comparator<Point>{

		@Override
		public int compare(Point point1, Point point2) { 

			if(robot.dist(point1) < robot.dist(point2)) {
				return -1;
			}
			if(robot.dist(point2) < robot.dist(point1)) {
				return 1;
			}

			return 0;
		}

	}

}
