package Controller;

public class MainController {

	RobotController robotController = new RobotController();
	CamController camController = new CamController(true);
	MapController mapController = new MapController();
		
	public void start() {
		camController.setMapController(mapController);
		camController.startUp();
	}
	
}