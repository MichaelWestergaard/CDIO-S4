package Controller;

public class MainController {

	RobotController robotController = new RobotController();
	CamController camController = new CamController(false);
	MapController mapController = new MapController(CamController.getMap());

	//Ny klasse der henter input fra kamera - skal bruges i robot og map
		
	public void start() {
		//kør setup og derefter kør i et loop indtil alle bolde er fundet
	}
	
	private void setup() {
		//setup map controller, robot controller
	}
}
