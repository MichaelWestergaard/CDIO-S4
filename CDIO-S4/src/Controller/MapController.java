package Controller;

import java.util.ArrayList;
import java.util.List;

import DTO.Ball;
import DTO.DeliveryPoint;
import DTO.Robot;

public class MapController {

	List<Ball> balls = new ArrayList<Ball>();
	List<DeliveryPoint> deliveryPoints = new ArrayList<DeliveryPoint>();

	List<Ball> shortestPath = new ArrayList<Ball>();

	ArrayList<Ball> coordinates = new ArrayList<Ball>();
	
	Robot robot;
	
	public ArrayList<Ball> findBalls(int[][] map) {
		//int ballcounter;
		boolean ballStatus = false;
		

		for (int x = 0; x < map.length; x++) { //rækkerne
			for (int y = 0; y < map[x].length; y++) { //kolonnerne
				if(map[x][y] == 9) {
					robot = new Robot(x,y);
				}
				if(map[x][y] == 1) {
					if(map[x+1][y] == 1 && map[x][y+1] == 1 && map[x+1][y+1] == 1) {

						ballStatus = true;
						
						//System.out.println(map[x][y]);
						
						map[x][y] = 2;
						map[x+1][y] = 2;
						map[x][y+1] = 2;
						map[x+1][y+1] = 2;

						coordinates.add(new Ball(x,y));
						//System.out.println(x + " , " + y);
					}
				}

			}	
		}
		
		//System.out.println(map);
		System.out.println(robot);
		for(Ball ball: coordinates) {
			System.out.println(ball);
			
		}
		return coordinates;
	}


	public int[][] locateDeliveryPoints(int[][] map) {
		System.out.println(coordinates);
		return coord;
	}

	public void findShortestPath() {

	}

}
