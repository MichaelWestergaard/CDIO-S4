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

	Map<String, Double> instructionMap = new HashMap <String, Double>();
	Direction directionVector, goalPoint;
	Goal smallGoal, bigGoal;
	private Robot robot;
	private int[][] map = null;
	private boolean ready = true;
	
	public MapController() {
		if(map == null) {
			System.out.println("Map is empty");
		}
	}
	
	public void loadMap(int[][] loadMap) {
		map = loadMap;
		ready = false;
		findBalls();
		findShortestPath();
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void findBalls() {
		//int ballcounter;
		boolean ballStatus = false;
    
		for (int x = 0; x < map.length; x++) { //r�kkerne
			for (int y = 0; y < map[x].length; y++) { //kolonnerne

				//	System.out.print(map[x][y]);

				if(map[x][y] == 9) {
					robot = new Robot(x,y);
					System.out.println("Robot:   " +robot);
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
		}
		System.out.println();

		int minusCounter = 0;
		for(int test = 0; test < 120;test++) {
			System.out.println(test);
			if(map[1][test] == -1) {
				minusCounter++;
				System.out.println("Antal forhindringer: " + minusCounter);
			}

		}
		printBallCoordinates();
	}

	private void printBallCoordinates() {
		for(Ball ball : coordinates) {
			System.out.println("x: " + ball.x + " y: " + ball.y);
		}
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
