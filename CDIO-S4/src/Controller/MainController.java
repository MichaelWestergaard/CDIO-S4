package Controller;

public class MainController {
	
	CamController camController = new CamController(true);
		
	public void start() {
		camController.startUp();
	}
	
}