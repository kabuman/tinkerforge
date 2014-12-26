package de.kabuman.tinkerforge.services.connect;

import java.io.IOException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.ConnectedListener;
import com.tinkerforge.IPConnection.EnumerateListener;
import com.tinkerforge.NotConnectedException;

import de.kabuman.common.services.InetService;

/**
 * Tinkerforge Connect Service <br>
 */
public class TfConnectService {
	
	// Constants
	private final int defaultTimeOut = 30000;

	// Parameter
	private IPConnection ipcon = null;
	private InetService inetService = null;
	private String host = null;
	private int port = 0;
	private Integer timeOut = null;


	/**
	 * Tinkerforge Connect Service <br>
	 *  <br>
	 *  - Creates a new IPConnection <br>
	 *  - adds several listener to this IPConnection <br>
	 *  - returns the IPConnection via Interface TfStack to TfStack Implementation <br>
	 *  - triggers ENUMERATE to connect the stack the first time  <br>
	 *   <br> 
	 * @param host
	 * @param port
	 * @param timeOut - in milliseoncs. If null defaultTimeOut will be set (currently 5000)
	 * @param tfListener
	 */
	public TfConnectService(
			String host
			, int port
			, Integer timeOut
			, TfStack tfListener){
		this.host = host;
		this.port = port;
		this.timeOut = (timeOut == null)? defaultTimeOut : timeOut;
		
		ipcon = new IPConnection();
    	inetService = new InetService();

		try {
			ipcon.setTimeout(this.timeOut);
			ipcon.connect(inetService.resolveURL(host), port);
			tfListener.setIPConnection(ipcon);
		} catch (AlreadyConnectedException | IOException e2) {
			System.out.println("TfConnectService:: Exception thrown for host="+host+" port="+port);
			e2.printStackTrace();
			ipcon = null;
			return;
		}
 
        ipcon.addEnumerateListener((EnumerateListener) tfListener);
        ipcon.addConnectedListener((ConnectedListener) tfListener);

        try {
        	ipcon.enumerate();
		} catch (NotConnectedException e) {
			System.out.println("TfConnectService:: Exception thrown for host="+host+" port="+port);
			e.printStackTrace();
			ipcon = null;
			return;
		}
	}

	public IPConnection getIpcon() {
		return ipcon;
	}

	public String getIP() {
		return inetService.getIP();
	}
	
	public String getURL(){
		return inetService.getURL();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
