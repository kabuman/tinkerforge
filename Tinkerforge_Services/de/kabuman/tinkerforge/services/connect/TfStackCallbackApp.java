package de.kabuman.tinkerforge.services.connect;

/**
 * Callback Interface to implement by application <br> 
 *  <br>
 * Via this interface the application will be triggered <br> 
 * if a Tinkerforge Stack is connected, reconnect or disconnected <br> 
 *   <br>
 * => Stack calls Application
 */
public interface TfStackCallbackApp {
	
	/**
	 *  Called from TfStackImpl in the case of a successfully (re)-connect
	 */
	void tfStackReConnected();
	
	
	/**
	 * Called from TfStackImpl in the case of a successfully disconnect 
	 */
	void tfStackDisconnected();
	
	/**
	 * Returns the Tf Stack Name
	 * 
	 * @return tfStackName
	 */
	String getTfStackName();

}
