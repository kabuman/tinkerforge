package de.kabuman.tinkerforge.services;

import com.tinkerforge.BrickletJoystick;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class EnsureMotorStop {

	
	/**
	 * Constructor
	 */
	public EnsureMotorStop(final MotorAbstractService motorAbstractService, BrickletJoystick joystick){
//		System.out.println("LostConObserverImpl:: initiated");
//		System.out.println("LostConObserverImpl.run:: joystick="+joystick);
//		System.out.println("LostConObserverImpl.addJoystickListener:: activated");

		try {
			joystick.setDebouncePeriod(100);
			joystick.setPositionCallbackThreshold('i', (short)-1, (short)1, (short)-1, (short)1);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		joystick.addPositionReachedListener(new BrickletJoystick.PositionReachedListener() {
			
			public void positionReached(short x, short y) {
//				System.out.println("EnsureMotorStop.addJoystickListener:: ==> Yes, i was triggered! <==");
				motorAbstractService.stopAllMotors();
				if (StopWatchMotorService.getInstance().isActive()){
					StopWatchMotorService.getInstance().stopOver();
				}

			}
		});
	}

}
