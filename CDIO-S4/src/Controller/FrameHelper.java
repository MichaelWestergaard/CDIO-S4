package Controller;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

public class FrameHelper extends JFrame {
	
	ActionListener btnListener;
	
	JPanel mainPanel;
	JSlider minBallSize, maxBallSize, lowHue, maxHue, lowSat, maxSat, lowVal, maxVal;
	JButton save;
	
	public JFrame calibrationMenu() {
		JFrame menu = new JFrame("Kalibrer Indstillinger");
		
		mainPanel = new JPanel();

		mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		menu.setSize(700, 250);
		
		minBallSize = new JSlider(0, 25);
		minBallSize.setPaintTicks(true);
		addToMainPanel(minBallSize, "Min Bold størrelse");
		
		maxBallSize = new JSlider(0, 25);
		maxBallSize.setPaintTicks(true);
		addToMainPanel(maxBallSize, "Max Bold størrelse");
		
		
		//Hue
		lowHue = new JSlider(0, 255);
		lowHue.setPaintTicks(true);
		addToMainPanel(lowHue, "Low Hue");
		
		maxHue = new JSlider(0, 255);
		maxHue.setPaintTicks(true);
		addToMainPanel(maxHue, "Max Hue");
		
		//Saturation
		lowSat = new JSlider(0, 255);
		lowSat.setPaintTicks(true);
		addToMainPanel(lowSat, "Low Saturation");
		
		maxSat = new JSlider(0, 255);
		maxSat.setPaintTicks(true);
		addToMainPanel(maxSat, "Max Saturation");
		
		//Value
		lowVal = new JSlider(0, 255);
		lowVal.setPaintTicks(true);
		addToMainPanel(lowVal, "Low Value");
		
		maxVal = new JSlider(0, 255);
		maxVal.setPaintTicks(true);
		addToMainPanel(maxVal, "Max Value");
		
		save = new JButton("Gem Indstillinger");
		save.addActionListener(btnListener);
		
		mainPanel.add(save);
		
		menu.add(mainPanel, BorderLayout.CENTER);
		
		return menu;
	}
	
	private void addToMainPanel(JSlider slider, String text) {
		JPanel panel = new JPanel();
		panel.setSize(300, 200);
		panel.add(new JLabel(text));
		panel.add(slider);
		
		mainPanel.add(panel);
	}
	
	//Getters and setters

	public void setListener(ChangeListener listener) {
		minBallSize.addChangeListener(listener);
		maxBallSize.addChangeListener(listener);
		lowHue.addChangeListener(listener);
		maxHue.addChangeListener(listener);
		lowSat.addChangeListener(listener);
		maxSat.addChangeListener(listener);
		lowVal.addChangeListener(listener);
		maxVal.addChangeListener(listener);
	}

	public ActionListener getBtnListener() {
		return btnListener;
	}

	public void setBtnListener(ActionListener btnListener) {
		this.btnListener = btnListener;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public JSlider getMinBallSize() {
		return minBallSize;
	}

	public void setMinBallSize(JSlider minBallSize) {
		this.minBallSize = minBallSize;
	}

	public JSlider getMaxBallSize() {
		return maxBallSize;
	}

	public void setMaxBallSize(JSlider maxBallSize) {
		this.maxBallSize = maxBallSize;
	}

	public JSlider getLowHue() {
		return lowHue;
	}

	public void setLowHue(JSlider lowHue) {
		this.lowHue = lowHue;
	}

	public JSlider getMaxHue() {
		return maxHue;
	}

	public void setMaxHue(JSlider maxHue) {
		this.maxHue = maxHue;
	}

	public JSlider getLowSat() {
		return lowSat;
	}

	public void setLowSat(JSlider lowSat) {
		this.lowSat = lowSat;
	}

	public JSlider getMaxSat() {
		return maxSat;
	}

	public void setMaxSat(JSlider maxSat) {
		this.maxSat = maxSat;
	}

	public JSlider getLowVal() {
		return lowVal;
	}

	public void setLowVal(JSlider lowVal) {
		this.lowVal = lowVal;
	}

	public JSlider getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(JSlider maxVal) {
		this.maxVal = maxVal;
	}

	public JButton getSave() {
		return save;
	}

	public void setSave(JButton save) {
		this.save = save;
	}
	
}
