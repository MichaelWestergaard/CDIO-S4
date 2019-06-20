package Controller;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

public class FrameHelper extends JFrame {
	
	ActionListener btnListener;
	
	JPanel mainPanel;
	JSlider minBallSize, maxBallSize, lowHueWalls, maxHueWalls, lowSatWalls, maxSatWalls, lowValWalls, maxValWalls;
	JSlider lowHueBalls, maxHueBalls, lowSatBalls, maxSatBalls, lowValBalls, maxValBalls;
	JSpinner minCrossArea, maxCrossArea;
	JButton save;
	
	public JFrame calibrationMenu() {
		JFrame menu = new JFrame("Kalibrer Indstillinger");
		
		mainPanel = new JPanel();

		mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		menu.setSize(700, 400);
		
		//Cross in the middle
		minCrossArea = new JSpinner();
		minCrossArea.setModel(new SpinnerNumberModel(2300, 0, 10000, 100));
		addToMainPanel(minCrossArea, "Min Cross Area");
		
		maxCrossArea = new JSpinner();
		maxCrossArea.setModel(new SpinnerNumberModel(2000, 0, 10000, 100));
		addToMainPanel(maxCrossArea, "Max Cross Area");
		
		//Walls		
		//Hue
		lowHueWalls = new JSlider(0, 255);
		lowHueWalls.setPaintTicks(true);
		addToMainPanel(lowHueWalls, "Low Hue Walls");
		
		maxHueWalls = new JSlider(0, 255);
		maxHueWalls.setPaintTicks(true);
		addToMainPanel(maxHueWalls, "Max Hue Walls");
		
		//Saturation
		lowSatWalls = new JSlider(0, 255);
		lowSatWalls.setPaintTicks(true);
		addToMainPanel(lowSatWalls, "Low Saturation Walls");
		
		maxSatWalls = new JSlider(0, 255);
		maxSatWalls.setPaintTicks(true);
		addToMainPanel(maxSatWalls, "Max Saturation Walls");
		
		//Value
		lowValWalls = new JSlider(0, 255);
		lowValWalls.setPaintTicks(true);
		addToMainPanel(lowValWalls, "Low Value Walls");
		
		maxValWalls = new JSlider(0, 255);
		maxValWalls.setPaintTicks(true);
		addToMainPanel(maxValWalls, "Max Value Walls");
		
		// Balls
		minBallSize = new JSlider(0, 25);
		minBallSize.setPaintTicks(true);
		addToMainPanel(minBallSize, "Min Bold størrelse");
		
		maxBallSize = new JSlider(0, 25);
		maxBallSize.setPaintTicks(true);
		addToMainPanel(maxBallSize, "Max Bold størrelse");
		
		// Hue
		lowHueBalls = new JSlider(0, 255);
		lowHueBalls.setPaintTicks(true);
		addToMainPanel(lowHueBalls, "Low Hue Balls");
		
		maxHueBalls = new JSlider(0, 255);
		maxHueBalls.setPaintTicks(true);
		addToMainPanel(maxHueBalls, "Max Hue balls");
		
		//Saturation
		lowSatBalls = new JSlider(0, 255);
		lowSatBalls.setPaintTicks(true);
		addToMainPanel(lowSatBalls, "Low Saturation Balls");
		
		maxSatBalls = new JSlider(0, 255);
		maxSatBalls.setPaintTicks(true);
		addToMainPanel(maxSatBalls, "Max Saturation Balls");
		
		//Value
		lowValBalls = new JSlider(0, 255);
		lowValBalls.setPaintTicks(true);
		addToMainPanel(lowValBalls, "Low Value Balls");
		
		maxValBalls = new JSlider(0, 255);
		maxValBalls.setPaintTicks(true);
		addToMainPanel(maxValBalls, "Max Value Balls");
		
		save = new JButton("Gem Indstillinger");
		save.addActionListener(btnListener);
		
		mainPanel.add(save);
		
		menu.add(mainPanel, BorderLayout.CENTER);
		
		return menu;
	}
	
	private void addToMainPanel(JComponent component, String text) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(text));
		panel.add(component);
		
		mainPanel.add(panel);
	}
	
	//Getters and setters

	public void setListener(ChangeListener listener) {
		minCrossArea.addChangeListener(listener);
		maxCrossArea.addChangeListener(listener);
		minBallSize.addChangeListener(listener);
		maxBallSize.addChangeListener(listener);
		lowHueWalls.addChangeListener(listener);
		maxHueWalls.addChangeListener(listener);
		lowSatWalls.addChangeListener(listener);
		maxSatWalls.addChangeListener(listener);
		lowValWalls.addChangeListener(listener);
		maxValWalls.addChangeListener(listener);
		lowHueBalls.addChangeListener(listener);
		maxHueBalls.addChangeListener(listener);
		lowSatBalls.addChangeListener(listener);
		maxSatBalls.addChangeListener(listener);
		lowValBalls.addChangeListener(listener);
		maxValBalls.addChangeListener(listener);
	}
	
}