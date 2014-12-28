import java.io.IOException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;


public class ReconnectMain {
    private static final String host = "localhost";
    private static final int port = 4223;
    private static IPConnection ipcon = null;
    private static ReconnectListener reconnectListener;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
        ipcon = new IPConnection();
        try {
			ipcon.connect(host, port);
		} catch (AlreadyConnectedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        reconnectListener = new ReconnectListener(ipcon);
        ipcon.addEnumerateListener(reconnectListener);
        ipcon.addConnectedListener(reconnectListener);

        try {
			ipcon.enumerate();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Keep the listener alive 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}


	}

}
