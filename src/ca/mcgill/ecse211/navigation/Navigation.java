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
	int iterator = 0;
	private Odometer odometer;
	private OdometerData odoData;
	private int startCorner = 0;



	
	public Navigation(Driver nav, double TRACK,
			double WHEEL_RAD) throws OdometerExceptions { // constructor
		this.nav = nav;
		this.odometer = Odometer.getOdometer();
		odoData = OdometerData.getOdometerData();
		odoData.setXYT(TILE_WIDTH, TILE_WIDTH, 0);

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
		
		//go to tunnel
		//go through tunnel
		
		//go to ring holder while avoiding obstacles

	}

	
	

	
	

			
	
}
