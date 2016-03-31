package org.texastorque.input;

public abstract class Input {

	// drivebase
	protected double leftDriveSpeed = 0.0;
	protected double rightDriveSpeed = 0.0;

	protected double driveSetpoint = 0.0;
	protected double turnSetpoint = 0.0;
	protected double tiltSetpoint = -6.0;

	// brakeing
	protected boolean braking = false;

	// override variables
	protected boolean override = false;
	protected boolean visionLock = false;
	protected boolean prevVisionLock = false;
	protected boolean overrideReset = false;

	// intake variables
	protected boolean intaking = false;
	protected boolean outtaking = false;

	// conveyor variables
	protected boolean conveyorIntaking = false;
	protected boolean conveyorOuttaking = false;

	// shooter variable
	protected boolean flywheelActive = false;
	protected boolean layupShot = false;

	protected double tiltMotorSpeed = 0.0;
	
	// compression testing
	protected boolean compressionTesting = false;
	
	// mechanism
	protected double armSetpoint = 0.0;
	protected double armSpeed = 0.0;
	
	public abstract void update();

	public boolean getCompressionTesting(){
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

	public boolean isOverride() {
		return override;
	}

	public boolean isVisionLock() {
		return visionLock;
	}

	public double getTiltOverrideSpeed() {
		return tiltMotorSpeed;
	}
	
	public double getArmOverrideSpeed() {
		return armSpeed;
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

	public boolean isConveyorOuttaking() {
		return conveyorOuttaking;
	}

	public boolean isFlywheelActive() {
		return flywheelActive;
	}
	
	public boolean isOverrideReset() {
		return overrideReset;
	}
}
