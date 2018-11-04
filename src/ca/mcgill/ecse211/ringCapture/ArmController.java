package ca.mcgill.ecse211.ringCapture;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class ArmController {
	private static final double ARMLENGTH = 16.1;
	private static final double MOTORHEIGHT = 8.5;
	private static final double FIRSTLEVEL = 10.0;
	private static final double SECONDLEVEL = 20.0;
	private static final double AVGRINGRADIUS = 1.0;
	private static final int FORWARD_SPEED = 100;
	private static double WHEELRADIUS;
	private static double TRACK;
	private double theta;
	private NXTRegulatedMotor armMotor;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private static final Port csPort = LocalEV3.get().getPort("S1");
	private float[] csData;
	private SampleProvider ColorID;

	public ArmController(NXTRegulatedMotor armMotor, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double wheelRadius, double track) {
		this.armMotor = armMotor;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		WHEELRADIUS = wheelRadius;
		TRACK = track;
		SensorModes ColorSensor = new EV3ColorSensor(csPort);
		ColorID = ColorSensor.getMode("Red");
		this.csData = new float[ColorID.sampleSize()];
		this.armMotor.stop();
	}

	public void moveArm() {

		//first level
		double a = FIRSTLEVEL - MOTORHEIGHT - AVGRINGRADIUS;
		theta = Math.atan(a/ARMLENGTH);
		int thetaInDeg = (int) (theta * (180/Math.PI));
		armMotor.setSpeed(10.0F);
		armMotor.rotate(-thetaInDeg); //sign depends on the orientation of the motor. need to check
		armMotor.stop();
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.rotate(convertDistance(WHEELRADIUS, 10), true);
		rightMotor.rotate(convertDistance(WHEELRADIUS, 10), false);

		leftMotor.rotate(-convertAngle(WHEELRADIUS, TRACK, 40.0), true);
		rightMotor.rotate(convertAngle(WHEELRADIUS, TRACK, 40.0), false);
		armMotor.setSpeed(130.0F);
		armMotor.rotate(-45);
		
		ColorID.fetchSample(csData, 0);
		float intensity = csData[0]; //last value sent by sensor

	}

	/**
	 * This method allows the conversion of a distance to the total rotation of each wheel need to
	 * cover that distance.
	 * 
	 * @param radius
	 * @param distance
	 * @return
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
