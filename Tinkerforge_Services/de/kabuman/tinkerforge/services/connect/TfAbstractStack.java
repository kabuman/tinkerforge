package de.kabuman.tinkerforge.services.connect;
import com.tinkerforge.IPConnection;

import de.kabuman.common.services.CommonCallback;
import de.kabuman.common.services.CommonObserver;
import de.kabuman.common.services.CommonObserverImpl;
import de.kabuman.common.services.LogControllerImpl;

/**
 * Abstract Listener for Tinkerforge Auto-Reconnect via enumerate <br>
 *  <br>
 *  This class is to extend by an TfStack Implementation. <br>
 *   <br>
 *  It provides the logic to handle the stack like: <br>
 *  - triggering the TfStack Implementation in the case of an ReConnect or Disconnect
 */
public abstract class TfAbstractStack implements IPConnection.EnumerateListener, IPConnection.ConnectedListener, CommonCallback {
	
	
	private IPConnection ipcon = null;
	
	private TfStackCallbackApp tfCallback;
	
	private boolean connected = false;

	CommonObserver commonObserver;	

	/**
	 * Constructor 
	 */
	public TfAbstractStack(TfStackCallbackApp tfCallback) {
		this.tfCallback = tfCallback;
		commonObserver = new CommonObserverImpl(this, null, 1000,tfCallback.getTfStackName());
	}
	
	
	/* (non-Javadoc)
	 * @see com.tinkerforge.IPConnection.ConnectedListener#connected(short)
	 */
	public void connected(short connectedReason) {
		if (ipcon == null){
			throw new IllegalArgumentException("TfAbstractListener::connected: missing IPConnection.");
		}
		
        if(connectedReason == IPConnection.CONNECT_REASON_AUTO_RECONNECT) {
        	log("Auto Reconnect for="+tfCallback.getTfStackName());

            while(true) {
                try {
                    ipcon.enumerate();
                    break;
                } catch(com.tinkerforge.NotConnectedException e) {
                }

                try {
                    Thread.sleep(1000);
                } catch(InterruptedException ei) {
                }
            }
        }
	}


	/* (non-Javadoc)
	 * @see com.tinkerforge.IPConnection.EnumerateListener#enumerate(java.lang.String, java.lang.String, char, short[], short[], int, short)
	 */
	@Override
	public synchronized void enumerate(
			String uid,
			String connectedUid,
			char position,
			short[] hardwareVersion,
			short[] firmwareVersion,
			int deviceIdentifier,
			short enumerationType) {

		if(enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED) {
			commonObserver.setFunctionCode(2);
			commonObserver.startObservation();
		} else {
			tfDeviceReConnected(new TfDeviceInfo(
					uid,
					connectedUid,
					position,
					hardwareVersion,
					firmwareVersion,
					deviceIdentifier,
					enumerationType,
					ipcon));
			commonObserver.setFunctionCode(1);
			commonObserver.startObservation();
		}
	}
	
	
	/**
	 * Returns the IPConnection
	 * @return IPConncection
	 */
	public IPConnection getIpcon(){
		return ipcon;
	}


	/**
	 * Set IPConnection
	 * @param ipcon - the ip connection
	 */
	public void setIPConnection(IPConnection ipcon){
		this.ipcon = ipcon;
	}


	/**
	 * Called if ReConnect is triggered
	 * To implement by inheriting class
	 * @param deviceInfo - container of device information
	 */
	public abstract void tfDeviceReConnected(TfDeviceInfo deviceInfo);


	/**
	 * @return boolean - true if connected; otherwise false
	 */
	public boolean isConnected() {
		return connected;
	}
	
	
	/**
	 * Triggered after a defined time period of the last device reconnect
	 */
	public void commonObserverTriggeredMethod(Integer functionCode){
		switch (functionCode) {
		case 1:
			connected = true;
			tfCallback.tfStackReConnected();
			break;

		case 2:
			connected = false;
			tfCallback.tfStackDisconnected();
			break;

		default:
			throw new IllegalArgumentException("not expected switch exit. functionCode="+functionCode);
		}
	}

	/**
	 * Write Log Msg
	 * @param msg - the msg to write
	 */
	private void log(String msg){
		if (LogControllerImpl.getInstance() == null){
			System.out.println(msg);
		} else {
			LogControllerImpl.getInstance().createTechnicalLogMessage(tfCallback.getTfStackName(),"TfAbstractStack::connected:", msg);
		}
	}
	
}