package DTO;

public class DeliveryPoint {

	Point[] coordinates = new Point[2];
	Point centerCoordinate;
	
	//Lav noget som finder center punktet, viser målets bredde  osv

	public DeliveryPoint(Point startCoordinate, Point endCoordinate) {
		coordinates[0] = startCoordinate;
		coordinates[1] = endCoordinate;
	}

	
	private Point getCenterCoordinate() {
		return centerCoordinate;
	}
	
}
