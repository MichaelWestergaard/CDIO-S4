import Controller.MainController;
import Controller.MapController;

public class main {

	static int[][] map = { 						//�ndre senere
			{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			{-1, 1, 1, 0, 0, 0, 0, 0, 9, -1},
			{-1, 1, 1, 0, 0, 0, 0, 0, 0, -1},
			{-1, 0, 0, 0, 0, 1, 1, 0, 0, -1},
			{-1, 1, 1, 1, 1, 1, 1, 0, 0, -1},
			{-1, 1, 1, 1, 1, 0, 0, 1, 1, -1},
			{-1, 0, 0, 0, 0, 0, 0, 1, 1, -1},
			{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}
	};
	
	public static void main(String[] args) {
		new MainController().start();
		new MapController().findBalls(map);
		new MapController().locateDeliveryPoints(map);
		
	}

}
