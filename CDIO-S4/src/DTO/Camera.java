package DTO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Camera {

	int minBallSize, maxBallSize;

	int lowHueWalls, maxHueWalls;
	int lowSatWalls, maxSatWalls;
	int lowValWalls, maxValWalls;
	
	int lowHueBalls, maxHueBalls;
	int lowSatBalls, maxSatBalls;
	int lowValBalls, maxValBalls;
	
	public Camera() {}

	public Camera(int minBallSize, int maxBallSize, int lowHueWalls, int maxHueWalls, int lowSatWalls, int maxSatWalls, int lowValWalls, int maxValWalls, int lowHueBalls, int maxHueBalls, int lowSatBalls, int maxSatBalls, int lowValBalls, int maxValBalls) {
		this.minBallSize = minBallSize;
		this.maxBallSize = maxBallSize;
		this.lowHueWalls = lowHueWalls;
		this.maxHueWalls = maxHueWalls;
		this.lowSatWalls = lowSatWalls;
		this.maxSatWalls = maxSatWalls;
		this.lowValWalls = lowValWalls;
		this.maxValWalls = maxValWalls;
		this.lowHueBalls = lowHueBalls;
		this.maxHueBalls = maxHueBalls;
		this.lowSatBalls = lowSatBalls;
		this.maxSatBalls = maxSatBalls;
		this.lowValBalls = lowValBalls;
		this.maxValBalls = maxValBalls;
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

	public int getLowHueWalls() {
		return lowHueWalls;
	}

	@XmlElement
	public void setLowHueWalls(int lowHueWalls) {
		this.lowHueWalls = lowHueWalls;
	}

	public int getMaxHueWalls() {
		return maxHueWalls;
	}

	@XmlElement
	public void setMaxHueWalls(int maxHueWalls) {
		this.maxHueWalls = maxHueWalls;
	}

	public int getLowSatWalls() {
		return lowSatWalls;
	}

	@XmlElement
	public void setLowSatWalls(int lowSatWalls) {
		this.lowSatWalls = lowSatWalls;
	}

	public int getMaxSatWalls() {
		return maxSatWalls;
	}

	@XmlElement
	public void setMaxSatWalls(int maxSatWalls) {
		this.maxSatWalls = maxSatWalls;
	}

	public int getLowValWalls() {
		return lowValWalls;
	}

	@XmlElement
	public void setLowValWalls(int lowValWalls) {
		this.lowValWalls = lowValWalls;
	}

	public int getMaxValWalls() {
		return maxValWalls;
	}

	@XmlElement
	public void setMaxValWalls(int maxValWalls) {
		this.maxValWalls = maxValWalls;
	}

	public int getLowHueBalls() {
		return lowHueBalls;
	}

	@XmlElement
	public void setLowHueBalls(int lowHueBalls) {
		this.lowHueBalls = lowHueBalls;
	}

	public int getMaxHueBalls() {
		return maxHueBalls;
	}

	@XmlElement
	public void setMaxHueBalls(int maxHueBalls) {
		this.maxHueBalls = maxHueBalls;
	}

	public int getLowSatBalls() {
		return lowSatBalls;
	}

	@XmlElement
	public void setLowSatBalls(int lowSatBalls) {
		this.lowSatBalls = lowSatBalls;
	}

	public int getMaxSatBalls() {
		return maxSatBalls;
	}

	@XmlElement
	public void setMaxSatBalls(int maxSatBalls) {
		this.maxSatBalls = maxSatBalls;
	}

	public int getLowValBalls() {
		return lowValBalls;
	}

	@XmlElement
	public void setLowValBalls(int lowValBalls) {
		this.lowValBalls = lowValBalls;
	}

	public int getMaxValBalls() {
		return maxValBalls;
	}

	@XmlElement
	public void setMaxValBalls(int maxValBalls) {
		this.maxValBalls = maxValBalls;
	}

	@Override
	public String toString() {
		return "Camera [minBallSize=" + minBallSize + ", maxBallSize=" + maxBallSize + ", lowHueWalls=" + lowHueWalls
				+ ", maxHueWalls=" + maxHueWalls + ", lowSatWalls=" + lowSatWalls + ", maxSatWalls=" + maxSatWalls
				+ ", lowValWalls=" + lowValWalls + ", maxValWalls=" + maxValWalls + ", lowHueBalls=" + lowHueBalls
				+ ", maxHueBalls=" + maxHueBalls + ", lowSatBalls=" + lowSatBalls + ", maxSatBalls=" + maxSatBalls
				+ ", lowValBalls=" + lowValBalls + ", maxValBalls=" + maxValBalls + "]";
	}
	
	
	
}