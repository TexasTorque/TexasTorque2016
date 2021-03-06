package org.texastorque.auto;

import org.texastorque.auto.modes.CDFAuto;
import org.texastorque.auto.modes.CenterAuto;
import org.texastorque.auto.modes.DoNothingAuto;
import org.texastorque.auto.modes.DriveForwardAuto;
import org.texastorque.auto.modes.LowBarAuto;
import org.texastorque.auto.modes.LowBarBackAuto;
import org.texastorque.auto.modes.MoatAuto;
import org.texastorque.auto.modes.PortDeCullisAuto;
import org.texastorque.auto.modes.RampartsAuto;
import org.texastorque.auto.modes.RockWallAuto;
import org.texastorque.auto.modes.RoughTerrainAuto;
import org.texastorque.auto.modes.TestAuto;
import org.texastorque.auto.modes.TiltAuto;
import org.texastorque.auto.modes.TurnAuto;

public enum AutoModes {
	DO_NOTHING_AUTO(0, DoNothingAuto.class),
	DRIVE_FORWARD_AUTO(1, DriveForwardAuto.class),
	TURN_AUTO(2, TurnAuto.class),
	TILT_AUTO(3, TiltAuto.class),
	LOW_BAR_AUTO(4, LowBarAuto.class),
	RAMPARTS_AUTO(5, RampartsAuto.class),
	ROCK_WALL_AUTO(6, RockWallAuto.class),
	ROUGH_TERRAIN_AUTO(7, RoughTerrainAuto.class),
	LOW_BAR_BACK_AUTO(8, LowBarBackAuto.class),
	PORT_DE_CULLIS_AUTO(9, PortDeCullisAuto.class),
	CDF_AUTO(10, CDFAuto.class),
	MOAT_AUTO(11, MoatAuto.class),
	TEST_AUTO(12, TestAuto.class),
	CENTER_AUTO(13, CenterAuto.class);

	enum DefensePosition {
		ZERO, ONE, TWO, THREE, FOUR, FIVE;
	}

	public int pass;
	public Class<?> call;

	public int defense;

	AutoModes(int _pass, Class<?> _call) {
		pass = _pass;
		call = _call;
	}

	public static AutoMode create(int pass, int defense) {
		AutoMode.currentDefense = convertDefense(defense);

		try {
			for (AutoModes m : AutoModes.values()) {
				if (pass == m.pass) {
					return (AutoMode) m.call.newInstance();
				}
			}
		} catch (Exception e) {
		}
		return new DoNothingAuto();
	}

	public static DefensePosition convertDefense(int pass) {
		for (int i = 0; i <= 5; i++) {
			if (i == pass) {
				return DefensePosition.values()[i];
			}
		}
		return DefensePosition.ZERO;
	}

	public static String autoModeToString(int pass) {
		for (AutoModes m : AutoModes.values()) {
			if (pass == m.pass) {
				return m.call.getSimpleName();
			}
		}
		return DO_NOTHING_AUTO.call.getSimpleName();
	}

	public static String defenseToString(int pass) {
		for (int i = 0; i <= 5; i++) {
			if (i == pass) {
				return DefensePosition.values()[i].toString();
			}
		}
		return DefensePosition.ZERO.toString();
	}
}
