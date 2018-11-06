package ca.mcgill.ecse211.navigation;

import lejos.hardware.sensor.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;

import java.util.HashMap;
import java.util.Map;

import ca.mcgill.ecse211.odometer.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class Navigation implements Runnable {
	private Driver nav;
	private float[] usData;
	private SampleProvider usDistance;

	private static final double TILE_WIDTH = 30.48;
	double currentT, currentY, currentX;
	double dx, dy, dt;
	double distanceToTravel;
	final double TILE_SIZE = 30.48;
	int iterator = 0;
	private Odometer odometer;
	private OdometerData odoData;
	private double startCorner;
	private double RedTeam = 1;
	private double GreenTeam = 20;
	private double RedCorner = 0;
	private double GreenCorner = 3;
	private double[] Red_LL = { 1, 20 };
	private double[] Red_UR = { 1, 20 };
	private double[] Green_LL = { 1, 20 };
	private double[] Green_UR = { 1, 20 };
	private double[] Island_LL = { 1, 20 };
	private double[] Island_UR = { 1, 20 };
	private double[] TNR_LL = { 1, 20 };
	private double[] TNR_UR = { 1, 20 };
	private double[] TNG_LL = { 1, 20 };
	private double[] TNG_UR = { 1, 20 };
	private double[] TR = { 1, 20 };
	private double[] TG = { 1, 20 };
	private double Team;
	private double[] Start_LL;
	private double[] Start_UR;
	private double[] TN_LL = { 1, 20 };
	private double[] TN_UR = { 1, 20 };
	private double[] Tree;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	Driver driver = new Driver(leftMotor, rightMotor, MainClass.TRACK, MainClass.WHEEL_RAD);
	boolean blck = false;

	public Navigation(Driver nav, double TRACK, double WHEEL_RAD) throws OdometerExceptions { // constructor
		this.nav = nav;
		this.odometer = Odometer.getOdometer();
		odoData = OdometerData.getOdometerData();
		odoData.setXYT(TILE_WIDTH, TILE_WIDTH, 0);

		this.Team = 20; // will be map.getTeam later

		if (Team == 0) {

			this.startCorner = RedCorner;
			this.Start_LL = Red_LL;
			this.Start_UR = Red_UR;
			this.TN_LL = TNR_LL;
			this.TN_UR = TNR_UR;
			this.Tree = TR;

		} else {

			this.startCorner = GreenCorner;
			this.Start_LL = Green_LL;
			this.Start_UR = Green_UR;
			this.TN_LL = TNG_LL;
			this.TN_UR = TNG_UR;
			this.Tree = TG;
		}

		this.Island_LL = Island_LL;
		this.Island_UR = Island_UR;

		SensorModes usSensor = MainClass.usSensor; // usSensor is the instance
		this.usDistance = usSensor.getMode("Distance"); // usDistance provides samples from
		// this instance
		this.usData = new float[usDistance.sampleSize()]; // usData is the buffer in which data are
		// returned

	}

	// run method (required for Thread)
	public void run() {
		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}

		// go to tunnel
		if (Team == 0) {

			while ((Red_LL[0] > odometer.getX() && odometer.getX() < Red_UR[0])
					&& (Red_LL[1] > odometer.getX() && odometer.getX() < Red_UR[0])) {
				driver.travelTo(TN_LL[0] - 1, TN_UR[1] + 1, blck);
				driver.turnTo(90);
				leftMotor.rotate(-convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), true);
				rightMotor.rotate(convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), false);// then travel a
																									// certain distance
				leftMotor.rotate(convertDistance(MainClass.WHEEL_RAD, TILE_SIZE / 2), true);
				rightMotor.rotate(convertDistance(MainClass.WHEEL_RAD, TILE_SIZE / 2), false);
				leftMotor.rotate(convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), true);
				rightMotor.rotate(-convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), false);

			}

			// go through tunnel
			driver.goStraightNB((TN_UR[0] - TN_LL[0] + 2) * TILE_SIZE);
			leftMotor.rotate(-convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), true);
			rightMotor.rotate(convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), false);// then travel a certain
																								// distance
			leftMotor.rotate(convertDistance(MainClass.WHEEL_RAD, TILE_SIZE / 2), true);
			rightMotor.rotate(convertDistance(MainClass.WHEEL_RAD, TILE_SIZE / 2), false);
			leftMotor.rotate(convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), true);
			rightMotor.rotate(-convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), false);

			// go to ring holder while avoiding obstacles
			while ((Island_LL[0] < odometer.getX() && odometer.getX() < Island_UR[0])
					&& (Island_LL[1] < odometer.getX() && odometer.getX() < Island_UR[0])) {

				driver.travelTo(Tree[0] - 1, Tree[1], blck);
			}

		} else {

			while ((Green_LL[0] > odometer.getX() && odometer.getX() < Green_UR[0])
					&& (Green_LL[1] > odometer.getX() && odometer.getX() < Green_UR[0])) {

				driver.travelTo(TN_LL[0] + 1, TN_UR[1] - 1, blck);
				driver.turnTo(0);
				leftMotor.rotate(convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), true);
				rightMotor.rotate(-convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), false);// then travel a
																									// certain distance
				leftMotor.rotate(convertDistance(MainClass.WHEEL_RAD, TILE_SIZE / 2), true);
				rightMotor.rotate(convertDistance(MainClass.WHEEL_RAD, TILE_SIZE / 2), false);
				leftMotor.rotate(-convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), true);
				rightMotor.rotate(convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), false);

			}
			// go through tunnel
			driver.goStraightNB((TN_UR[1] - TN_LL[1] + 2) * TILE_SIZE);
			leftMotor.rotate(-convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), true);
			rightMotor.rotate(convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), false);// then travel a certain
																								// distance
			leftMotor.rotate(convertDistance(MainClass.WHEEL_RAD, TILE_SIZE / 2), true);
			rightMotor.rotate(convertDistance(MainClass.WHEEL_RAD, TILE_SIZE / 2), false);
			leftMotor.rotate(convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), true);
			rightMotor.rotate(-convertAngle(MainClass.WHEEL_RAD, MainClass.TRACK, 90), false);

			// go to ring holder while avoiding obstacles
			while ((Island_LL[0] < odometer.getX() && odometer.getX() < Island_UR[0])
					&& (Island_LL[1] < odometer.getX() && odometer.getX() < Island_UR[0])) {

				driver.travelTo(Tree[0] - 1, Tree[1] - 1, blck);
			}

		}

	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
