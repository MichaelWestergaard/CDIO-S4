package Controller;

import java.util.ArrayList;
import java.util.List;

import DTO.Ball;
import DTO.DeliveryPoint;

public class MapController {

	List<Ball> balls = new ArrayList<Ball>();
	List<DeliveryPoint> deliveryPoints = new ArrayList<DeliveryPoint>();
	
	List<Ball> shortestPath = new ArrayList<Ball>();
	int[][] map = null;
	
	public MapController(int[][] loadMap) {
		//det her array er nu bestemt ud fra billedet fra opencv.
		map = loadMap;
		
		System.out.println("int[][] map = new int[][]{");
		for (int i = 0; i < 180; i++) {
			System.out.print("{ ");
			for (int j = 0; j < 120; j++) {
				System.out.print(map[i][j] + "");
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
		System.out.println("};");
	}
	
	private void locateBalls() {
		//indsæt dem i balls arraylist
	}
	
	private void locateDeliveryPoints() {
		
	}
	
	private void findShortestPath() {
		
	}
}
