package ca.mcgill.ecse211.ringCapture;

import java.util.concurrent.TimeUnit;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


/**
 * 
 * @author Noam Suissa
 *
 */
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

	private RingColours ringColours;
	
	/**
	 * Initializes motors and hardware constants
	 * 
	 * @param armMotor
	 * @param leftMotor
	 * @param rightMotor
	 * @param wheelRadius
	 * @param track
	 */
	public ArmController(NXTRegulatedMotor armMotor, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double wheelRadius, double track) {
		this.armMotor = armMotor;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		WHEELRADIUS = wheelRadius;
		TRACK = track;
		this.armMotor.stop();
	}

	/**
	 * perform moveArm() and moveRobot() routines for first level. If no ring was detected, move back and repeat same process for second level.
	 */
	public void run() {

		int degreeMoved = moveArm(FIRSTLEVEL);

		boolean firstLevelDetected = moveRobot();

		//need to move arm to second level if first level has no ring
		if(!firstLevelDetected) {

			//rotate back to face tree
			leftMotor.rotate(convertAngle(WHEELRADIUS, TRACK, 40.0), true);
			rightMotor.rotate(-convertAngle(WHEELRADIUS, TRACK, 40.0), false);

			//move back
			leftMotor.rotate(convertDistance(WHEELRADIUS, 10), true);
			rightMotor.rotate(convertDistance(WHEELRADIUS, 10), false);

			armMotor.setSpeed(10.0F);
			//move arm back to 0 deg
			armMotor.rotate(-(degreeMoved+45)); //sign depends on the orientation of the motor. need to check

			moveArm(SECONDLEVEL);

			moveRobot(); //same process, ignore returned boolean

		}

	}

	/**
	 * This method moves the robot carefully in a certain manner to retrieve and detect a ring coulour
	 * @return true or false depending on if a first level ring was detected 
	 */
	private boolean moveRobot() {

		boolean firstLevelDetected = false;
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		//advance robot to insert arm in ring hole
		leftMotor.rotate(convertDistance(WHEELRADIUS, 10), true); 
		rightMotor.rotate(convertDistance(WHEELRADIUS, 10), false);

		//create new ring detection object
		ringColours = new RingColours(); //change place in code depending on when you want to activate light sensor.

		//dont know if this will work. want to let color sensor activate before continuing robot movement 
		try {
			TimeUnit.SECONDS.sleep(7);
		}catch(InterruptedException e) {
			
		}

		//rotate counter clockwise to unhook the ring from the tree
		leftMotor.rotate(-convertAngle(WHEELRADIUS, TRACK, 40.0), true);
		rightMotor.rotate(convertAngle(WHEELRADIUS, TRACK, 40.0), false);

		//bring arm up quickly to allow captured ring to fall down
		armMotor.setSpeed(130.0F);
		armMotor.rotate(-45);
		armMotor.stop();

		//detect color and beep accordingly
		if(ringColours.colourDetected("Red")) {
			beep(4);
			firstLevelDetected = true;
		}else if(ringColours.colourDetected("Blue")) {
			beep(1);
			firstLevelDetected = true;
		}else if(ringColours.colourDetected("Green")) {
			beep(2);
			firstLevelDetected = true;
		}else if(ringColours.colourDetected("Yellow")) {
			beep(3);
			firstLevelDetected = true;
		}

		return firstLevelDetected;
	}

	/**
	 * Uses trigonometry to move the arm to a precise height; either first or second ring level
	 * @param level height in cm of the level
	 * @return returns the degrees that the arm moved
	 */
	private int moveArm(double level) {
		double a = FIRSTLEVEL - MOTORHEIGHT - AVGRINGRADIUS;
		theta = Math.atan(a/ARMLENGTH);
		int thetaInDeg = (int) (theta * (180/Math.PI));
		armMotor.setSpeed(10.0F);
		armMotor.rotate(-thetaInDeg); //sign depends on the orientation of the motor. need to check
		armMotor.stop();
		return thetaInDeg;
	}

	/**
	 * Method to beep <code>times</code> times
	 * @param times
	 */
	private void beep(int times) {
		for(int i=0; i<times; i++) {
			Sound.beep();
		}
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

	/**
	 * Calls convertDistance() when wanting to rotate base of robot
	 * @param radius
	 * @param width
	 * @param angle
	 * @return
	 */
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
