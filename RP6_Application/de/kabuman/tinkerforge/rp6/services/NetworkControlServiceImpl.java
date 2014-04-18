package de.kabuman.tinkerforge.rp6.services;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.FormatterService;

public class NetworkControlServiceImpl implements NetworkControlService {

	BrickMaster brickMasterCP;
	BrickMaster brickMasterRC;
	BrickMaster brickMasterRP6;
	
	public NetworkControlServiceImpl(BrickMaster brickMasterCP, BrickMaster brickMasterRC, BrickMaster brickMasterRP6){
		this.brickMasterCP = brickMasterCP;
		this.brickMasterRC = brickMasterRC;
		this.brickMasterRP6 = brickMasterRP6;
	}
	
	public String getSignalStrengthCP(){
		try {
			return FormatterService.getFormShort(brickMasterCP.getChibiSignalStrength());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getSignalStrengthRC(){
		try {
			return FormatterService.getFormShort(brickMasterRC.getChibiSignalStrength());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getSignalStrengthRP6(){
		try {
			return FormatterService.getFormShort(brickMasterRP6.getChibiSignalStrength());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String getChibiAddressCP(){
		try {
			return FormatterService.getFormShort(brickMasterCP.getChibiAddress());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getChibiAddressRP6(){
		try {
			return FormatterService.getFormShort(brickMasterRP6.getChibiAddress());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getChibiAddressRC(){
		try {
			return FormatterService.getFormShort(brickMasterRC.getChibiAddress());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getChibiMasterAddressCP(){
		try {
			return FormatterService.getFormShort(brickMasterCP.getChibiMasterAddress());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getChibiMasterAddressRP6(){
		try {
			return FormatterService.getFormShort(brickMasterRP6.getChibiMasterAddress());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getChibiMasterAddressRC(){
		try {
			return FormatterService.getFormShort(brickMasterRC.getChibiMasterAddress());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getChibiErrorLogCP(){
		try {
			return brickMasterCP.getChibiErrorLog().toString();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getChibiErrorLogRP6(){
		try {
			return brickMasterRP6.getChibiErrorLog().toString();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getChibiErrorLogRC(){
		try {
			return brickMasterRC.getChibiErrorLog().toString();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public boolean isSignalToWeakRP6(short thresholdSignalStrength) {
		try {
			if (brickMasterRP6.getChibiSignalStrength() <= thresholdSignalStrength){
				return true;
			} else {
				return false;
			}
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	


}
