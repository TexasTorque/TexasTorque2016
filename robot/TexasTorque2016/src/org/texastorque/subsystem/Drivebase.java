package org.texastorque.subsystem;

import org.texastorque.auto.AutoManager;
import org.texastorque.constants.Constants;
import org.texastorque.torquelib.controlLoop.TorquePID;
import org.texastorque.torquelib.controlLoop.TorquePV;
import org.texastorque.torquelib.controlLoop.TorqueTMP;
import org.texastorque.torquelib.util.TorqueMathUtil;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivebase extends Subsystem {

	private static Drivebase instance;

	public enum DriveControlType {
		MANUAL, LINEAR, TURN, VISION;
	}

	private double leftSpeed;
	private double rightSpeed;

	// sensor
	private double leftPosition;
	private double rightPosition;

	private double leftVelocity;
	private double rightVelocity;

	private double leftAcceleration;
	private double rightAcceleration;

	private double angle;
	private double angularVelocity;

	// generic profile variables
	private double prevTime;
	private DriveControlType driveControlType;

	// linear profile
	private TorqueTMP profile;
	private TorquePV leftPV;
	private TorquePV rightPV;

	private double targetPosition;

	private double targetVelocity;

	private double targetAcceleration;

	private double setpoint;
	private double previousSetpoint;

	// angular profile
	private TorqueTMP angularProfile;
	private TorquePV angularPV;

	private double targetAngle;
	private double targetAngularVelocity;

	private double turnSetpoint;
	private double turnPreviousSetpoint;

	// vision PID control
	private TorquePID visionPID;
	
	// vision
	public boolean deadbanded = false;
	private double pixyOutputScaling = 8.0;
	private double threshold = 0.4;
	private double deadband = .05;
	
	@Override
	public void initSystem() {
		driveControlType = DriveControlType.MANUAL;

		// linear
		if (!driverStation.isAutonomous()) {
			profile = new TorqueTMP(Constants.D_MAX_VELOCITY.getDouble(), Constants.D_MAX_ACCELERATION.getDouble());
		}
		deadbanded = false;
		leftPV = new TorquePV();
		rightPV = new TorquePV();

		feedback.resetDriveEncoders();
		setpoint = 0.0;
		previousSetpoint = 0.0;

		leftPV.setGains(Constants.D_LEFT_PV_P.getDouble(), Constants.D_LEFT_PV_V.getDouble(),
				Constants.D_LEFT_PV_ffV.getDouble(), Constants.D_LEFT_PV_ffA.getDouble());
		leftPV.setTunedVoltage(Constants.TUNED_VOLTAGE.getDouble());

		rightPV.setGains(Constants.D_RIGHT_PV_P.getDouble(), Constants.D_RIGHT_PV_V.getDouble(),
				Constants.D_RIGHT_PV_ffV.getDouble(), Constants.D_RIGHT_PV_ffA.getDouble());
		rightPV.setTunedVoltage(Constants.TUNED_VOLTAGE.getDouble());

		// angular
		angularProfile = new TorqueTMP(Constants.D_MAX_ANGULAR_VELOCITY.getDouble(),
				Constants.D_MAX_ANGULAR_ACCELERATION.getDouble());
		angularPV = new TorquePV();

		feedback.resetGyro();
		turnSetpoint = 0.0;
		turnPreviousSetpoint = 0.0;

		angularPV.setGains(Constants.D_TURN_PV_P.getDouble(), Constants.D_TURN_PV_V.getDouble(),
				Constants.D_TURN_PV_ffV.getDouble(), Constants.D_TURN_PV_ffA.getDouble());
		angularPV.setTunedVoltage(Constants.TUNED_VOLTAGE.getDouble());

		visionPID = new TorquePID();
		visionPID.setPIDGains(Constants.D_VISION_P.getDouble(), Constants.D_VISION_I.getDouble(),
				Constants.D_VISION_D.getDouble());
		visionPID.setSetpoint(0.0);
		visionPID.setEpsilon(0.5);

		// time
		prevTime = Timer.getFPGATimestamp();

		feedback.resetDriveEncoders();
	}

	@Override
	public void runSystem() {
		leftPosition = feedback.getLeftDrivePosition();
		rightPosition = feedback.getRightDrivePosition();

		leftVelocity = feedback.getLeftDriveVelocity();
		rightVelocity = feedback.getRightDriveVelocity();

		leftAcceleration = feedback.getLeftDriveAcceleration();
		rightAcceleration = feedback.getRightDriveAcceleration();

		angle = feedback.getAngle();
		angularVelocity = feedback.getAngularVelocity();

		driveControlType = input.getDriveControlType();

		if (driverStation.isAutonomous()) {
			profile = AutoManager.getInstance().getAutoLinearProfile();
		}

		if (driveControlType == DriveControlType.MANUAL) {
			leftSpeed = input.getLeftDriveSpeed();
			rightSpeed = input.getRightDriveSpeed();

			if (feedback.getLeftDriveVelocity() < -20 && feedback.getRightDriveVelocity() < -20
					&& input.isFlipCheck()) {
				leftSpeed = rightSpeed = 0.0;
			}
		} else if (driveControlType == DriveControlType.LINEAR) {
			setpoint = input.getDriveSetpoint();
			if (setpoint != previousSetpoint) {
				previousSetpoint = setpoint;
				feedback.resetDriveEncoders();
				profile.generateTrapezoid(setpoint, 0.0, 0.0);
			}

			double dt = Timer.getFPGATimestamp() - prevTime;
			prevTime = Timer.getFPGATimestamp();
			profile.calculateNextSituation(dt);

			targetPosition = profile.getCurrentPosition();
			targetVelocity = profile.getCurrentVelocity();
			targetAcceleration = profile.getCurrentAcceleration();

			leftSpeed = leftPV.calculate(profile, leftPosition, leftVelocity);
			rightSpeed = rightPV.calculate(profile, rightPosition, rightVelocity);
		} else if (driveControlType == DriveControlType.TURN) {
			turnSetpoint = input.getTurnSetpoint();
			if (turnSetpoint != turnPreviousSetpoint) {
				turnPreviousSetpoint = turnSetpoint;
				feedback.resetGyro();
				angularProfile.generateTrapezoid(turnSetpoint, 0.0, 0.0);
			}

			double dt = Timer.getFPGATimestamp() - prevTime;
			prevTime = Timer.getFPGATimestamp();
			angularProfile.calculateNextSituation(dt);

			targetAngle = angularProfile.getCurrentPosition();
			targetAngularVelocity = angularProfile.getCurrentVelocity();

			leftSpeed = angularPV.calculate(angularProfile, angle, angularVelocity);
			rightSpeed = -leftSpeed;
		} else if (driveControlType == DriveControlType.VISION) {
			turnSetpoint = (feedback.getRequiredTurn() - Constants.V_PIXY_V.getDouble()) * pixyOutputScaling;
			rightSpeed = -visionPID.calculate(turnSetpoint);
			if(!deadbanded || Constants.DEBUG_DO_DEADBAND_RESET.getBoolean()) {
				if (rightSpeed < threshold && rightSpeed > deadband) {
					rightSpeed = -threshold;
				} else if (rightSpeed > -threshold && rightSpeed < -deadband) {
					rightSpeed = threshold;
				} else if ((rightSpeed < deadband && rightSpeed > 0) || (rightSpeed > -deadband && rightSpeed < 0)) {
					rightSpeed = 0;
				}
			} else {
				rightSpeed = 0;
			}
			leftSpeed = -rightSpeed;
		}
	}

	@Override
	protected void output() {
		leftSpeed = TorqueMathUtil.constrain(leftSpeed, 1.0);
		rightSpeed = TorqueMathUtil.constrain(rightSpeed, 1.0);

		if (input.isBraking()) {
			leftSpeed = 0.0;
			rightSpeed = 0.0;
		}

		output.setDriveSpeeds(leftSpeed, rightSpeed);
	}

	@Override
	public void pushToDashboard() {
		// targets
		SmartDashboard.putNumber("DrivebaseTargetPosition", targetPosition);
		SmartDashboard.putNumber("DrivebaseTargetVelocity", targetVelocity);
		SmartDashboard.putNumber("DrivebaseTargetAcceleration", targetAcceleration);

		SmartDashboard.putNumber("DrivebaseTargetAngle", targetAngle);
		SmartDashboard.putNumber("DrivebaseTargetAngularVelocity", targetAngularVelocity);

		// values
		SmartDashboard.putString("DriveControlType", driveControlType.toString());

		SmartDashboard.putNumber("DrivebaseLeftPosition", leftPosition);
		SmartDashboard.putNumber("DrivebaseRightPosition", rightPosition);

		SmartDashboard.putNumber("DrivebsaeLeftVelocity", leftVelocity);
		SmartDashboard.putNumber("DrivebaseRightVelocity", rightVelocity);

		SmartDashboard.putNumber("DrivebaseLeftAcceleration", leftAcceleration);
		SmartDashboard.putNumber("DrivebaseRightAcceleration", rightAcceleration);

		SmartDashboard.putNumber("DrivebaseAngle", angle);
		SmartDashboard.putNumber("DrivebaseAngularVelocity", angularVelocity);

		// setpoints
		SmartDashboard.putBoolean("VisionLock", input.isVisionLock());
		SmartDashboard.putNumber("DriveSetpoint", setpoint);
		SmartDashboard.putNumber("TurnSetpoint", turnSetpoint);

		// output
		SmartDashboard.putNumber("DrivebaseLeftSpeed", leftSpeed);
		SmartDashboard.putNumber("DrivebaseRightSpeed", rightSpeed);
	}

	// singleton
	public static Drivebase getInstance() {
		return instance == null ? instance = new Drivebase() : instance;
	}
}