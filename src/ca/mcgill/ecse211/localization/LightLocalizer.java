package ca.mcgill.ecse211.localization;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import ca.mcgill.ecse211.navigation.*;
import ca.mcgill.ecse211.odometer.*;

/***
 * This class implements the light loclization in Lab4 on the EV3 platform.
 * 
 * @authorAbedAtassi
 * @authorHyunSuAn
 * @emmaeagles
 */
public class LightLocalizer implements Runnable {

	private SampleProvider color_sample_provider;
	private float[] color_samples;
	private float light_value;
	private Driver driver;
	private static final int d = 12; // distance between the center of the robot and the light sensor
	private static final double TILE_WIDTH = 30.48;

	private Odometer odoData;
	private int startCorner;
	private double dX;
	private double dY;
	
	/***
	 * Constructor
	 * 
	 * 
	 * @param leftMotor,
	 *            rightMotor, TRACK, WHEEL_RAD
	 */
	public LightLocalizer(Driver driver) {
		this.driver = driver;
		EV3ColorSensor colour_sensor = MainClass.lineSensor;
		this.color_sample_provider = colour_sensor.getMode("Red");
		this.color_samples = new float[colour_sensor.sampleSize()];
	}

	public void run() {
		try {
			this.odoData = Odometer.getOdometer();
		} catch (OdometerExceptions e1) {
		}
		// wait
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}
		driver.stop();
		
		do_localization();
	}

	/***
	 * This method starts the light localization on the robot
	 *
	 * 
	 * 
	 */
	public void do_localization() {
		fetchUSData();
		while (light_value > 0.3) { // If no black line is detected move forward
			driver.goStraightNB(30);
			fetchUSData();
		}
		driver.stop();
		odoData.setY(d); // Correct Y
		odoData.setTheta(0);
		// Turn 90 degrees
		driver.spinB(-90);

		fetchUSData();
		while (light_value > 0.3) {
			driver.goStraightNB(30);
			fetchUSData();
		}
		driver.stop();
		odoData.setX(d);
		odoData.setTheta(90);

		// Go backwards to the center of the line
		driver.goStraightB(-d);
		driver.spinB(90);
		driver.goStraightB(-d);
		
		double[] position = {TILE_WIDTH, TILE_WIDTH, 0};
		odoData.setPosition(position);
	}

	/***
	 * This method fetches the US data
	 * 
	 */
	public void fetchUSData() {
		color_sample_provider.fetchSample(color_samples, 0);
		this.light_value = color_samples[0];
		
	}
}