package org.texastorque.input;

import org.texastorque.constants.Constants;
import org.texastorque.subsystem.Drivebase.DriveControlType;

public class Input {

	protected boolean visionLock = false;

	// drivebase
	protected double leftDriveSpeed = 0.0;
	protected double rightDriveSpeed = 0.0;

	protected DriveControlType driveControlType = DriveControlType.MANUAL;

	protected double driveSetpoint = 0.0;
	protected double turnSetpoint = 0.0;
	protected double tiltSetpoint = Constants.S_DOWN_SETPOINT.getDouble();

	protected boolean flipCheck = false;

	// braking
	protected boolean braking = false;
	protected boolean doArmMotors = true;
	
	// intake variables
	protected boolean intaking = false;
	protected boolean outtaking = false;

	// conveyor variables
	protected boolean conveyorIntaking = false;
	protected boolean conveyorOuttaking = false;

	// shooter variable
	protected boolean layupShot = false;
	protected boolean batterShot = false;
	protected boolean longShot = false;

	protected boolean rpmDownshift = false;
	protected boolean rpmUpshift = false;

	protected boolean hoodReady = false;

	protected double tiltOverrideSpeed = 0.0;

	protected boolean tiltOverride = false;

	// compression testing
	protected boolean compressionTesting = false;

	// arm
	protected boolean armUp = true;
	protected double armSetpoint = 0.0;

	protected boolean armOverride = false;
	protected double armOverrideLeftSpeed = 0.0;
	protected double armOverrideRightSpeed = 0.0;

	// flashlight
	protected boolean flashlight = false;
	
	// misc
	protected boolean other = false;

	public void update() {
		// must be overridden
	}

	public boolean getCompressionTesting() {
		return compressionTesting;
	}

	public double getLeftDriveSpeed() {
		return leftDriveSpeed;
	}

	public double getRightDriveSpeed() {
		return rightDriveSpeed;
	}

	public double getDriveSetpoint() {
		return driveSetpoint;
	}

	public double getTurnSetpoint() {
		return turnSetpoint;
	}

	public double getTiltSetpoint() {
		return tiltSetpoint;
	}

	public double getArmSetpoint() {
		return armSetpoint;
	}

	public boolean isLayupShot() {
		return layupShot;
	}

	public boolean isBatterShot() {
		return batterShot;
	}

	public boolean isLongShot() {
		return longShot;
	}

	public boolean isShooting() {
		return longShot || batterShot || layupShot;
	}

	public boolean isArmUp() {
		return armUp;
	}

	public boolean isVisionLock() {
		return visionLock;
	}
	
	public boolean doArmMotors() {
		return doArmMotors;
	}

	public double getTiltOverrideSpeed() {
		return tiltOverrideSpeed;
	}

	public boolean isRPMDownshift() {
		return rpmDownshift;
	}

	public boolean isRPMUpshift() {
		return rpmUpshift;
	}

	public boolean isFlipCheck() {
		return flipCheck;
	}

	public boolean isBraking() {
		return braking;
	}

	public boolean isIntaking() {
		return intaking;
	}

	public boolean isOuttaking() {
		return outtaking;
	}

	public boolean isConveyorIntaking() {
		return conveyorIntaking;
	}

	public boolean isHoodReady() {
		return hoodReady;
	}

	public boolean isConveyorOuttaking() {
		return conveyorOuttaking;
	}

	public boolean isTiltOverride() {
		return tiltOverride;
	}

	public boolean isArmOverride() {
		return armOverride;
	}
	
	public boolean isOther() {
		return other;
	}

	public double getArmLeftOverrideSpeed() {
		return armOverrideLeftSpeed;
	}

	public double getArmRightOverrideSpeed() {
		return armOverrideRightSpeed;
	}

	public boolean isFlashlightOn() {
		return flashlight;
	}

	public DriveControlType getDriveControlType() {
		return driveControlType;
	}
}