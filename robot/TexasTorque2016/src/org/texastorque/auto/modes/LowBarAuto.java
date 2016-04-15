package org.texastorque.auto.modes;

import org.texastorque.auto.AutoMode;
import org.texastorque.constants.Constants;

public class LowBarAuto extends AutoMode {

	@Override
	protected void run() {
		armUp = false;
		drive(Constants.A_DRIVE_FORWARD_AUTO_DISTANCE.getDouble());
		pause(2.0);
		postDefenseVision();
	}

	@Override
	protected double getLinearMaxSpeed() {
		return 50.0;// TODO
	}
}
