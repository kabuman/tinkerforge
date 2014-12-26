package de.kabuman.tinkerforge.services.connect;

/**
 * Callback Interface to implement by application <br> 
 *  <br>
 * Via this interface the application will be triggered <br> 
 * if a Tinkerforge Stack is connected, reconnect or disconnected <br> 
 *   <br>
 * => Stack calls Application
 */
public interface TfCloneCallbackApp {
	
	/**
	 *  Called from TfStackImpl in the case of a successfully (re)-connect
	 * @param cloneId - to identify the clone by id
	 */
	void tfCloneReConnected(int cloneId);
	

	/**
	 * Called from TfStackImpl in the case of a successfully disconnect 
	 * @param cloneId - to identify the clone by id
	 */
	void tfCloneDisconnected(int cloneId);

}
