package DTO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Camera {

	int minBallSize, maxBallSize;

	int lowHue, maxHue;
	int lowSat, maxSat;
	int lowVal, maxVal;
	
	public Camera() {}
	
	public Camera(int minBallSize, int maxBallSize, int lowHue, int maxHue, int lowSat, int maxSat, int lowVal, int maxVal) {
		this.minBallSize = minBallSize;
		this.maxBallSize = maxBallSize;
		this.lowHue = lowHue;
		this.maxHue = maxHue;
		this.lowSat = lowSat;
		this.maxSat = maxSat;
		this.lowVal = lowVal;
		this.maxVal = maxVal;
	}

	public int getMinBallSize() {
		return minBallSize;
	}

	@XmlElement
	public void setMinBallSize(int minBallSize) {
		this.minBallSize = minBallSize;
	}

	public int getMaxBallSize() {
		return maxBallSize;
	}
	
	@XmlElement
	public void setMaxBallSize(int maxBallSize) {
		this.maxBallSize = maxBallSize;
	}

	public int getLowHue() {
		return lowHue;
	}

	@XmlElement
	public void setLowHue(int lowHue) {
		this.lowHue = lowHue;
	}

	public int getMaxHue() {
		return maxHue;
	}

	@XmlElement
	public void setMaxHue(int maxHue) {
		this.maxHue = maxHue;
	}

	public int getLowSat() {
		return lowSat;
	}

	@XmlElement
	public void setLowSat(int lowSat) {
		this.lowSat = lowSat;
	}

	public int getMaxSat() {
		return maxSat;
	}

	@XmlElement
	public void setMaxSat(int maxSat) {
		this.maxSat = maxSat;
	}

	public int getLowVal() {
		return lowVal;
	}

	@XmlElement
	public void setLowVal(int lowVal) {
		this.lowVal = lowVal;
	}

	public int getMaxVal() {
		return maxVal;
	}

	@XmlElement
	public void setMaxVal(int maxVal) {
		this.maxVal = maxVal;
	}
	
}
