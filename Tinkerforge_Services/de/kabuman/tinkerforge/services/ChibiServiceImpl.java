package de.kabuman.tinkerforge.services;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.CountDownService;
import de.kabuman.common.services.CountDownServiceImpl;
import de.kabuman.common.services.FormatterService;

public class ChibiServiceImpl implements ChibiService {

	BrickMaster brickMaster;

	CountDownService countDownService;
	
	public ChibiServiceImpl(BrickMaster brickMaster){
		this.brickMaster = brickMaster;
		countDownService = new CountDownServiceImpl(3);
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getSignalStrength()
	 */
	public String getChibiSignalStrength(){
		countDownService.reset();
		do {
			try {
				return FormatterService.getFormShort(brickMaster.getChibiSignalStrength());
			} catch (TimeoutException e) {
				System.out.println("getChibiSignalStrength:: TimeoutException");
				countDownService.down();
			} catch (NotConnectedException e) {
				System.out.println("getChibiSignalStrength:: NotConnectedException");
				countDownService.down();
			}
		} while (!countDownService.isDown());
		return null;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getChibiAddress()
	 */
	public String getChibiAddress(){
		countDownService.reset();
		do {
			try {
				return FormatterService.getFormShort(brickMaster.getChibiAddress());
			} catch (TimeoutException e) {
				System.out.println("getChibiAddress:: TimeoutException");
				countDownService.down();
			} catch (NotConnectedException e) {
				System.out.println("getChibiAddress:: NotConnectedException");
				countDownService.down();
			}
		} while (!countDownService.isDown());
		return null;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getChibiMasterAddress()
	 */
	public String getChibiMasterAddress(){
		countDownService.reset();
		do {
			try {
				return FormatterService.getFormShort(brickMaster.getChibiMasterAddress());
			} catch (TimeoutException e) {
				System.out.println("getChibiMasterAddress:: TimeoutException");
				countDownService.down();
			} catch (NotConnectedException e) {
				System.out.println("getChibiMasterAddress:: NotConnectedException");
				countDownService.down();
			}
		} while (!countDownService.isDown());
		return null;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getChibiErrorLog()
	 */
	public String getChibiErrorLog(){
		countDownService.reset();
		do {
			try {
				return brickMaster.getChibiErrorLog().toString();
			} catch (TimeoutException e) {
				System.out.println("getChibiErrorLog:: TimeoutException");
				countDownService.down();
			} catch (NotConnectedException e) {
				System.out.println("getChibiErrorLog:: NotConnectedException");
				countDownService.down();
			}
		} while (!countDownService.isDown());
		return null;
	}


	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.RemoteControlService#getChibiErrorNoAck()
	 */
	public Integer getChibiErrorNoAck(){
		countDownService.reset();
		do {
			try {
				return new Integer(brickMaster.getChibiErrorLog().noAck);
			} catch (TimeoutException e) {
				System.out.println("getChibiErrorNoAck:: TimeoutException");
				countDownService.down();
			} catch (NotConnectedException e) {
				System.out.println("getChibiErrorNoAck:: NotConnectedException");
				countDownService.down();
			}
		} while (!countDownService.isDown());
		return null;
	}

}
