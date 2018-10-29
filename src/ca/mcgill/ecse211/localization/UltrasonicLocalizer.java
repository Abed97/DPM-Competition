package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.navigation.MainClass;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/***
 * This class implements the rising and falling edge US localization on the EV3
 * platform.
 * 
 * @authorAbedAtassi
 * @authorHyunSuAn
 */
public class UltrasonicLocalizer implements Runnable {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	private static final int FILTER_OUT = 30;
	public static final int WALL_DISTANCE = 40;
	public static int ROTATION_SPEED = 100;
	private Driver driver;
	private SampleProvider usSensor;
	private Odometer odometer;
	private float[] usData;

	private int distance = 0;
	private int filter_control = 0;
	

	/***
	 * Constructor
	 * 
	 * 
	 * @param leftMotor,
	 *            rightMotor, TRACK, WHEEL_RAD.
	 */
	public UltrasonicLocalizer(Driver drive) throws OdometerExceptions {
		odometer = Odometer.getOdometer();
		SensorModes us_sensor = MainClass.usSensor;
		this.usSensor = us_sensor.getMode("Distance");
		this.usData = new float[usSensor.sampleSize()];

	}

	public void run() {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		
			doFallingEdge();
		

	}

	/*** This method starts the falling edge localization
	   * 
	   * 
	   * */
	public void doFallingEdge() {
		// facing the wall at the start
		double angle;
		fetchUSData();
		if (this.distance < 100) {
			while (this.distance < 100) {
				driver.spinNB(-720);

				fetchUSData();
			}
			driver.stop();

			while (this.distance > WALL_DISTANCE) {
				driver.spinNB(720);
				fetchUSData();
			}
			driver.stop();
			odometer.setTheta(0);

			while (this.distance < 100) {
				driver.spinNB(720);
				fetchUSData();
			}
			driver.stop();

			while (this.distance > WALL_DISTANCE) {
				driver.spinNB(720);
				fetchUSData();
			}
			driver.stop();

			angle = odometer.getXYT()[2];
		} else {// facing away from the walls
			while (this.distance > WALL_DISTANCE) {
				driver.spinNB(-720);
				fetchUSData();
			}
			driver.stop();
			odometer.setTheta(0);

			while (this.distance < 100) {
				driver.spinNB(720);
				fetchUSData();
			}
			driver.stop();

			while (this.distance > WALL_DISTANCE) {
				driver.spinNB(720);
				fetchUSData();
			}
			driver.stop();

			angle = odometer.getXYT()[2];
		}
		
		driver.spinB(driver.convertAngle(45 + (angle / 2.0)));
	
		driver.stop();
		
		odometer.setTheta(0);
	}

	
	private void fetchUSData() {
		usSensor.fetchSample(usData, 0); // acquire data
		int new_distance = (int) Math.abs(usData[0] * 100.0); // extract from buffer, cast to int
		// rudimentary filter - toss out invalid samples corresponding to null
		// signal.
		if (new_distance >= 255 && filter_control < FILTER_OUT) {
			filter_control++;
		} else if (new_distance >= 255) {
			this.distance = new_distance;
		} else {
			filter_control = 0;
			this.distance = new_distance;
		}
	}
}
