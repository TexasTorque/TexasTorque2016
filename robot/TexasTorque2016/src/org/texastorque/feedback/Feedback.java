package org.texastorque.feedback;

import org.texastorque.constants.Constants;
import org.texastorque.constants.Ports;
import org.texastorque.input.Input;
import org.texastorque.torquelib.component.TorqueEncoder;
import org.texastorque.torquelib.util.TorqueMathUtil;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.interfaces.Accelerometer.Range;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Feedback {

	private static Feedback instance;

	private static final double DRIVEBASE_DISTANCE_CONVERSION = 0.084883;
	private static final double FLYWHEEL_VELOCITY_CONVERSION = .24;

	private Input currentInput;

	// sensors
	private VisionFeedback vision;

	private TorqueEncoder leftDriveEncoder;
	private TorqueEncoder rightDriveEncoder;
	private TorqueEncoder leftArmEncoder;
	private TorqueEncoder rightArmEncoder;
	private TorqueEncoder flywheelEncoder;
	private TorqueEncoder tiltEncoder;
	
	private BuiltInAccelerometer accel;
	
	// drivebase values
	private double leftDrivePosition;
	private double leftDriveVelocity;
	private double leftDriveAcceleration;

	private double rightDrivePosition;
	private double rightDriveVelocity;
	private double rightDriveAcceleration;

	private double angle;
	private double prevAngle;
	private double prevTime;
	private double angularVelocity;

	// shooter values
	private double flywheelVelocity;

	private double tiltAngle;

	// a mechanism values
	private double leftArmAngle;
	private double rightArmAngle;

	public Feedback() {
		vision = VisionFeedback.getInstance();

		leftDriveEncoder = new TorqueEncoder(Ports.DRIVE_LEFT_ENCODER_A, Ports.DRIVE_LEFT_ENCODER_B, false,
				EncodingType.k4X);
		rightDriveEncoder = new TorqueEncoder(Ports.DRIVE_RIGHT_ENCODER_A, Ports.DRIVE_RIGHT_ENCODER_B, false,
				EncodingType.k4X);
		leftArmEncoder = new TorqueEncoder(Ports.ARM_LEFT_ENCODER_A, Ports.ARM_LEFT_ENCODER_B, false,
				EncodingType.k4X);
		rightArmEncoder = new TorqueEncoder(Ports.ARM_RIGHT_ENCODER_A, Ports.ARM_RIGHT_ENCODER_B, false,
				EncodingType.k4X);
		flywheelEncoder = new TorqueEncoder(Ports.FLYWHEEL_ENCODER_A, Ports.FLYWHEEL_ENCODER_B, false,
				EncodingType.k4X);
		tiltEncoder = new TorqueEncoder(Ports.TILT_ENCODER_A, Ports.TILT_ENCODER_B, false, EncodingType.k4X);
		
		accel = new BuiltInAccelerometer();
	}

	public void setInput(Input input) {
		prevTime = Timer.getFPGATimestamp();
		currentInput = input;
	}

	public void update() {
		leftDriveEncoder.calc();
		rightDriveEncoder.calc();
		flywheelEncoder.calc();

		leftDrivePosition = leftDriveEncoder.get() * DRIVEBASE_DISTANCE_CONVERSION;
		leftDriveVelocity = leftDriveEncoder.getRate() * DRIVEBASE_DISTANCE_CONVERSION;
		leftDriveAcceleration = leftDriveEncoder.getAcceleration() * DRIVEBASE_DISTANCE_CONVERSION;

		rightDrivePosition = rightDriveEncoder.get() * DRIVEBASE_DISTANCE_CONVERSION;
		rightDriveVelocity = rightDriveEncoder.getRate() * DRIVEBASE_DISTANCE_CONVERSION;
		rightDriveAcceleration = rightDriveEncoder.getAcceleration() * DRIVEBASE_DISTANCE_CONVERSION;

		prevAngle = angle;
		angle = accel.getZ() / Math.sqrt(accel.getY() * accel.getY() + accel.getZ() + accel.getZ());
		angle = Math.toDegrees(Math.atan(angle));
		angularVelocity = (angle - prevAngle) / (Timer.getFPGATimestamp() - prevTime);
		prevTime = Timer.getFPGATimestamp();

		flywheelVelocity = flywheelEncoder.getRate() * FLYWHEEL_VELOCITY_CONVERSION;

		// down setpoint is negative
		tiltAngle = (tiltEncoder.getRaw() * .04234) + Constants.S_DOWN_SETPOINT.getDouble();

		leftArmAngle = leftArmEncoder.get();
		rightArmAngle = rightArmEncoder.get();

		if (currentInput.isVisionLock() && !visionShotReady()) {
			vision.calc();
		}
	}

	public void resetDriveEncoders() {
		leftDriveEncoder.reset();
		rightDriveEncoder.reset();
	}

	public void resetGyro() {
		angle = 0;
		prevAngle = 0;
		prevTime = Timer.getFPGATimestamp();
	}

	public void resetTiltEncoder() {
		tiltEncoder.reset();
	}

	// getters
	public double getLeftDrivePosition() {
		return leftDrivePosition;
	}

	public double getLeftDriveVelocity() {
		return leftDriveVelocity;
	}

	public double getLeftDriveAcceleration() {
		return leftDriveAcceleration;
	}

	public double getRightDrivePosition() {
		return rightDrivePosition;
	}

	public double getRightDriveVelocity() {
		return rightDriveVelocity;
	}

	public double getRightDriveAcceleration() {
		return rightDriveAcceleration;
	}

	public double getAngle() {
		return angle;
	}

	public double getAngularVelocity() {
		return angularVelocity;
	}

	public double getFlywheelVelocity() {
		return flywheelVelocity;
	}

	public double getTiltAngle() {
		return tiltAngle;
	}

	public double getLeftArmAngle() {
		return leftArmAngle;
	}

	public double getRightArmAngle() {
		return rightArmAngle;
	}

	public double getRequiredTurn() {
		return vision.getTurn();
	}

	public double getRequiredTilt() {
		return vision.getTilt();
	}

	public double getVisionDistance() {
		return vision.getDistance();
	}

	public int getVisionState() {
		return vision.getVisionState();
	}

	public boolean visionShotReady() {
		if (!TorqueMathUtil.near(getRequiredTurn(), 0, 4.0)) {
			return false;
		}
		if (!TorqueMathUtil.near(getTiltAngle(), getRequiredTilt(), 1.0)) {
			return false;
		}
		if (!TorqueMathUtil.near(getFlywheelVelocity(), Constants.S_VISION_FLYWHEEL.getDouble(), 1000)) {
			return false;
		}
		return true;
	}
	
	public void pushToDashboard() {
		SmartDashboard.putNumber("VISION_STATE", getVisionState());

		SmartDashboard.putNumber("Turn", vision.getTurn());
		SmartDashboard.putNumber("Tilt", vision.getTilt());
		SmartDashboard.putNumber("Distance", vision.getDistance());
		SmartDashboard.putNumber("Tilt1", vision.getTilt1());
		SmartDashboard.putNumber("Tilt2", vision.getTilt2());
		SmartDashboard.putBoolean("VisionShotReady", visionShotReady());
	}

	// singleton
	public static Feedback getInstance() {
		return instance == null ? instance = new Feedback() : instance;
	}
}
