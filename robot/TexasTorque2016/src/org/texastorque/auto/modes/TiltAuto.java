package org.texastorque.auto.modes;

import org.texastorque.auto.AutoMode;
import org.texastorque.constants.Constants;

public class TiltAuto extends AutoMode {

	@Override
	protected void run() {
		tiltSetpoint = Constants.A_TILT_AUTO_ANGLE.getDouble();
	}

	@Override
	public double getLinearMaxSpeed() {
		return 0.0;
	}
}
