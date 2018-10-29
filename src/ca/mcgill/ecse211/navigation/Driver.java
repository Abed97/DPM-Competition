package ca.mcgill.ecse211.navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import ca.mcgill.ecse211.odometer.*;

public class Driver {
	private Odometer odometer;
	private OdometerData odoData;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	
	private final double TRACK;
	private final double WHEEL_RAD;
	
	public static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	
	private double angle;
	double dx, dy, dt;
	double distance;
	
	public Driver(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
		      final double TRACK, final double WHEEL_RAD) throws OdometerExceptions {
		this.odometer = Odometer.getOdometer();
	    this.leftMotor = leftMotor;
	    this.rightMotor = rightMotor;
	    odoData = OdometerData.getOdometerData();
	    odoData.setXYT(0 , 0 , 0);
	    this.TRACK = TRACK;
	    this.WHEEL_RAD = WHEEL_RAD;
	    
	    for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(250); // reduced the acceleration to make it smooth
		}
	}

	/**
	 * Given a set of points the robot will calculate the angle 
	 * and distance to travel
	 * Can be blocking or non blocking depending on value of boolean
	 * 
	 * @param x
	 * @param y
	 * @param blocking
	 */
	public void travelTo(double x, double y, boolean nonblocking) {
		double position[] = odometer.getXYT(); //get current position
		this.dx = x - position[0]; //distance to travel in x
		this.dy = y - position[1]; //distance to travel in y
		this.distance = Math.hypot(dx, dy); //diagonal distance to travel
		this.angle = Math.toDegrees(Math.atan2(dx, dy)); //calculate the angle 
														//needed to travel at
		turnTo(angle); //Function that turns the robot to the correct angle

		//travel to waypoint
		if(nonblocking == false) {
			goStraightB(distance);
		}
		else {
			goStraightNB(distance);
		}
		//reset the odometer values to account for the motor speed difference
		odometer.setTheta(angle);
		odometer.setX(x);
		odometer.setY(y);
	}
	/** 
	 * Blocking travel method that will move the robot the given distance
	 * 
	 * @param distance
	 */
	public void goStraightB(double distance) {
		distance = convertDistance(distance);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(distance), true);
		rightMotor.rotate(convertDistance(distance), false);
	}
	/** 
	 * Non-Blocking travel method that will move the robot the given distance
	 * and allow the next line to be executed
	 * 
	 * @param distance
	 */
	public void goStraightNB(double distance) {
		distance = convertDistance(distance);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(distance), true);
		rightMotor.rotate(convertDistance(distance), true);
	}
 /**
  * Method that turns the robot to a specific angle 
  * @param theta
  */
	public void turnTo(double angle) {
		double position[] = odometer.getXYT();
		this.angle = angle - position[2];
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		if (angle > 180) {
			angle -= 360;
		} else if (angle < -180) {
			angle += 360;
		}

		leftMotor.rotate(convertAngle(angle), true);
		rightMotor.rotate(-convertAngle(angle), false);
	}
	/**
	 * Non-blocking spin method. Allows next line to be executed
	 * @param angle
	 */
	public void spinNB(int angle) {
		leftMotor.setSpeed(ROTATE_SPEED);
	    rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(-convertAngle(angle), true);
		rightMotor.rotate(convertAngle(angle), true);
	}
	/**
	 * Blocking spin method, stops next line from being executed
	 * @param angle
	 */
	public void spinB(int angle) {
		leftMotor.setSpeed(ROTATE_SPEED);
	    rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(-convertAngle(angle), true);
		rightMotor.rotate(convertAngle(angle), false);
	}
	/**
	 * Stops Motors
	 * @param angle
	 */
	public void stop() {
		rightMotor.stop(true);
		leftMotor.stop();
	}
	
	/**
	 * Method that returns true if navigation is in progress 
	 * @return
	 */
	boolean isNavigating() {
	 if((leftMotor.isMoving()) || (rightMotor.isMoving()))
		 return true;
	 else 
		 return false;

	}
	/**
	   * These methods allow the conversion of a distance to the total rotation of each wheel needed to
	   * cover that distance.
	   * 
	   * @param radius of the wheel
	   * @param distance to be traveled
	   * @return returns total wheel rotations
	   */
	  public int convertDistance(double distance) {
		    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
		  }
	  public int convertAngle(double angle) {
		    return convertDistance(Math.PI * TRACK * angle / 360.0);
	}
}
