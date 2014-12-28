import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class ReconnectListener implements IPConnection.EnumerateListener, IPConnection.ConnectedListener {
	private IPConnection ipcon = null;
	
	BrickletLCD20x4 brickletLCD;


	public ReconnectListener										(IPConnection ipcon) {
		this.ipcon = ipcon;
	}


	public void connected(short connectedReason) {
        if(connectedReason == IPConnection.CONNECT_REASON_AUTO_RECONNECT) {
            System.out.println("Auto Reconnect");

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


	@Override
	public void enumerate(
			String uid,
			String connectedUid,
			char position,
			short[] hardwareVersion,
			short[] firmwareVersion,
			int deviceIdentifier,
			short enumerationType) {
		System.out.println("UID:               " + uid);
		System.out.println("Enumeration Type:  " + enumerationType);

		if(enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED) {
			System.out.println("");
			return;
		}

		System.out.println("Connected UID:     " + connectedUid);
		System.out.println("Position:          " + position);
		System.out.println("Hardware Version:  " + hardwareVersion[0] + "." +
		                                           hardwareVersion[1] + "." +
		                                           hardwareVersion[2]);
		System.out.println("Firmware Version:  " + firmwareVersion[0] + "." +
		                                           firmwareVersion[1] + "." +
		                                           firmwareVersion[2]);
		System.out.println("Device Identifier: " + deviceIdentifier);
		System.out.println("");
		
		if(deviceIdentifier == BrickletLCD20x4.DEVICE_IDENTIFIER) {
		    brickletLCD = new BrickletLCD20x4(uid, ipcon);
		    try {
			    brickletLCD.clearDisplay();
				brickletLCD.backlightOn();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}
}