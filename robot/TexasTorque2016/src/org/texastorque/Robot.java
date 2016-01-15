package org.texastorque;

import java.util.ArrayList;

import org.texastorque.auto.AutoManager;
import org.texastorque.feedback.Feedback;
import org.texastorque.input.HumanInput;
import org.texastorque.input.Input;
import org.texastorque.subsystem.Drivebase;
import org.texastorque.subsystem.Subsystem;
import org.texastorque.torquelib.base.TorqueIterative;
import org.texastorque.torquelib.util.Parameters;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TorqueIterative {

	private int numCycles;
	private ArrayList<Subsystem> subsystems;

	private AutoManager autoManager;
	private Input input;

	public void robotInit() {
		Parameters.load();
		numCycles = 0;

		subsystems = new ArrayList<>();
		subsystems.add(Drivebase.getInstance());

		autoManager = AutoManager.getInstance();
	}

	// teleop
	public void teleopInit() {
		numCycles = 0;
		subsystems.forEach((subsystem) -> subsystem.init());

		input = HumanInput.getInstance();
		subsystems.forEach((subsystem) -> subsystem.setInput(input));
	}

	public void teleopContinuous() {
		input.update();
		Feedback.update();
		subsystems.forEach((subsystem) -> subsystem.run());
	}

	public void teleopPeriodic() {
		updateDashboard();
	}

	// auto
	public void autonomousInit() {
		numCycles = 0;
		autoManager.init();
		subsystems.forEach((subsystem) -> subsystem.init());

		input = autoManager.createAutoMode();
		subsystems.forEach((subsystem) -> subsystem.init());
		autoManager.runAutoMode();
	}

	public void autonomousContinuous() {
		input.update();
		Feedback.update();
		subsystems.forEach((subsystem) -> subsystem.run());
	}

	public void autonomousPeriodic() {
		updateDashboard();
	}

	// disabled
	public void disabledInit() {
		numCycles = 0;
	}

	public void disabledContinuous() {
		updateDashboard();
	}

	// private
	private void updateDashboard() {
		subsystems.forEach((subsystem) -> subsystem.pushToDashboard());
		SmartDashboard.putNumber("NumCycles", numCycles++);
		SmartDashboard.putNumber("ThreadCount", Thread.activeCount());
	}
}
