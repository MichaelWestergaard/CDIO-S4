package Controller;

public class MainController {

	RobotController robotController = new RobotController();

	CamController camController = new CamController(false);
	MapController mapController = new MapController();
		
	public void start() {
		camController.setMapController(mapController);
		camController.startUp();
	}
	
}