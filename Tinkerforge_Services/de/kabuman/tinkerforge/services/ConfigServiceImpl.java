package de.kabuman.tinkerforge.services;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickIMU;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickStepper;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.kabuman.common.services.FormatterService;
import de.kabuman.common.services.StringService;
import de.kabuman.common.services.TableFormatterService;
import de.kabuman.common.services.TableFormatterServiceImpl;

/**
 * ConfigServiceImpl - creates connect, instantiates and adds brick(lets) to connection 
 * 
 * Creates a connection by constructor
 * Maps the own id and brick(let) type to the physical used brick(let).
 * Creates the device object and add it to the given connection.
 * Provides several static methods to get information about a brick or bricklet.
 * 
 * All Parameter are defined in Interface ConfigService
 */
public class ConfigServiceImpl implements ConfigService {
	
	// Used for Singleton Pattern
	private static ConfigServiceImpl instance = null;
 
	private IPConnection ipcon = null;
	
	private Map<Device, DeviceUsage> deviceUsageMap = null;

	/**
	 * Private Constructor 
	 * Create Connection
	 * 
	 * @throws IOException
	 */
	private ConfigServiceImpl() throws IOException{
//		ipcon = new IPConnection(HOST, PORT);
		ipcon = new IPConnection();
		try {
			ipcon.connect(HOST, PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deviceUsageMap = new HashMap<Device, DeviceUsage>();
	}
	
	/**
	 * Singleton<br>
	 * 
	 * Returns an instance only. Does NOT instantiate a new one.
	 * @return instance
	 * @throws IOException
	 */
	public static synchronized ConfigServiceImpl getInstance() {
		return instance;
	}

	/**
	 * Singleton
	 * 
	 * @return new instance in every case
	 * @throws IOException
	 */
	public static synchronized ConfigServiceImpl getNewInstance() throws IOException{
		if (instance != null){
			int resetCounter = instance.reset();
			try {
				instance.ipcon.disconnect();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(resetCounter+" Tinkerforge Bricks und die Connection zur�ckgesetzt.");
		}
		instance = new ConfigServiceImpl();
		return getInstance();
	}

	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#reset()
	 */
	public int reset(){
		int count = 0;
		Collection<DeviceUsage> deviceUsageList = deviceUsageMap.values();
		
		// Reset non-master bricks first
		for (DeviceUsage deviceUsage : deviceUsageList) {
			if (deviceUsage != null){
				try {
					if (deviceUsage.getDevice() instanceof BrickMaster ){ 
						((BrickMaster) deviceUsage.getDevice()).reset();
						count++;
					}
					if (deviceUsage.getDevice() instanceof BrickDC){ 
						((BrickDC) deviceUsage.getDevice()).reset();
						count++;
					}
					if (deviceUsage.getDevice() instanceof BrickIMU){ 
						((BrickIMU) deviceUsage.getDevice()).reset();
						count++;
					}
					if (deviceUsage.getDevice() instanceof BrickServo){
						((BrickServo) deviceUsage.getDevice()).reset();
						count++;
					}
					if (deviceUsage.getDevice() instanceof BrickStepper){
						((BrickStepper) deviceUsage.getDevice()).reset();
						count++;
					}
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return count;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#getConnect()
	 */
	public IPConnection getConnect(){
		return ipcon;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#createAndConnect(int, java.lang.String, double)
	 */
	public Device createAndConnect(int id, String usedFor, double thresholdMinVoltage) throws TimeoutException{
		DeviceUsage deviceUsage = createResetAndConnectDevice(id);
		if (deviceUsage != null){
			deviceUsage.setUsedFor(usedFor);
			deviceUsage.setThresholdMinVoltage(thresholdMinVoltage);
			deviceUsageMap.put(deviceUsage.getDevice(), deviceUsage);
		}
		return deviceUsage.getDevice();
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#createAndConnect(int, java.lang.String)
	 */
	public Device createAndConnect(int id, String usedFor) throws TimeoutException{
		DeviceUsage deviceUsage = createResetAndConnectDevice(id);
		if (deviceUsage != null){
			deviceUsage.setUsedFor(usedFor);
			deviceUsageMap.put(deviceUsage.getDevice(), deviceUsage);
		}
		return deviceUsage.getDevice();
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#createAndConnect(int, com.tinkerforge.IPConnection)
	 */
	public Device createAndConnect(int id) throws TimeoutException{
		DeviceUsage deviceUsage = (createResetAndConnectDevice(id));
		if (deviceUsage != null){
			deviceUsageMap.put(deviceUsage.getDevice(), deviceUsage);
		}
		return deviceUsage.getDevice();
	}
	
	
	private DeviceUsage createResetAndConnectDevice(int id) throws TimeoutException{
		DeviceUsage deviceUsage = createAndConnectDevice(ipcon, id);
//		if (deviceUsage.getDevice() instanceof BrickMaster){
//			((BrickMaster)deviceUsage.getDevice()).reset();
//			System.out.println("ConfigService:: Brickmaster reset");
//			deviceUsage = createAndConnectDevice(id);
//		}
		return deviceUsage;
	}
	
	/**
	 * @param ipcon
	 * @param id
	 * @return
	 * @throws TimeoutException
	 */
	private DeviceUsage createAndConnectDevice(IPConnection ipcon, int id) throws TimeoutException{
		return ConnectDeviceServiceImpl.createAndConnectDevice(ipcon, id);
	}
	
	/**
	 * Returns the firmware version for the given device
	 * 
	 * @param device - the device
	 * @return String - the formated firmware version
	 */
	public static String getFirmwareVersion(Device device){
//		return device.getVersion().firmwareVersion[0]+"."+device.getVersion().firmwareVersion[1]+"."+device.getVersion().firmwareVersion[2];
		try {
			return device.getIdentity().firmwareVersion.toString();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * Returns the binding version for the given device
	 * 
	 * @param device - the device
	 * @return String - the formated binding version
	 */
	public static String getBindingVersion(Device device){
		return device.getAPIVersion()[0]+"."+device.getAPIVersion()[1]+"."+device.getAPIVersion()[2];
	}
	
	/**
	 * Returns the name and the hardware version of the given device
	 * 
	 * @param device - the device
	 * @return String
	 */
	public static String getName(Device device){
//		int lenName = device.getVersion().name.length() - 24; // Short-Array with 3 elements 
//		return device.getVersion().name.substring(0,lenName);
		try {
			return device.getIdentity().toString();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#setUsedFor(com.tinkerforge.Device, java.lang.String)
	 */
	public void setUsedFor(Device device, String usedFor) {
		if (deviceUsageMap.containsValue(device)){
			deviceUsageMap.get(device).setUsedFor(usedFor);
		} else {
			throw new IllegalArgumentException("configService:: setUsedFor: device unknown. usedFor="+usedFor);
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#getUsedFor(com.tinkerforge.Device)
	 */
	public String getUsedFor(Device device) {
		if (deviceUsageMap.containsKey(device)){
			return deviceUsageMap.get(device).getUsedFor();
		} else {
			throw new IllegalArgumentException("configService:: getUsedFor: device unknown");
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#setThresholdMinVoltage(com.tinkerforge.BrickMaster, double)
	 */
	public void setThresholdMinVoltage(BrickMaster brickMaster,
			double thresholdMinVoltage) {
		if (deviceUsageMap.containsValue(brickMaster)){
			deviceUsageMap.get(brickMaster).setThresholdMinVoltage(thresholdMinVoltage);
		} else {
			throw new IllegalArgumentException("configService:: setThresholdMinVoltage: brickMaster unknown. thresholdMinVoltage="+thresholdMinVoltage);
		}
	}

	/* (non-Java)doc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#getThresholdMinVoltage(com.tinkerforge.BrickMaster)
	 */
	public double getThresholdMinVoltage(BrickMaster brickMaster) {
		if (deviceUsageMap.containsKey(brickMaster)){
			return deviceUsageMap.get(brickMaster).getThresholdMinVoltage();
		} else {
			throw new IllegalArgumentException("configService:: getThresholdMinVoltage: brickMaster unknown");
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.tinkerforge.services.ConfigService#report()
	 */
	public void report() {
		TableFormatterService tfs = new TableFormatterServiceImpl(160);
		Collection<DeviceUsage> deviceUsageColl = deviceUsageMap.values();
		System.out.println("Name                                 id   uid          MV   Adr  Frequenz             Channel  MAdr db   Error Log");
		System.out.println(StringService.create(160,  "-"));
		for (DeviceUsage deviceUsage : deviceUsageColl) {
			if (deviceUsage != null){
				tfs.newRow(0, deviceUsage.getUsedFor());
				tfs.addValue(37, deviceUsage.getId().toString());
				if (deviceUsage.getUid() != null){
					tfs.addValue(42, deviceUsage.getUid());
				}
				if (deviceUsage.getThresholdMinVoltage() != null){
					tfs.addValue(55, FormatterService.getShortFormVoltage(deviceUsage.getThresholdMinVoltage()));
				}
				if (deviceUsage.getDevice() instanceof BrickMaster){
					BrickMaster brickMaster = (BrickMaster) deviceUsage.getDevice();
					try {
						if (brickMaster.isChibiPresent() == true){
							brickMaster.getChibiFrequency();
							tfs.addValue(60, String.valueOf(brickMaster.getChibiAddress()));
							tfs.addValue(65, getChibiFrequency(brickMaster.getChibiFrequency()));
							tfs.addValue(90, String.valueOf(brickMaster.getChibiChannel()));
							tfs.addValue(95, String.valueOf(brickMaster.getChibiMasterAddress()));
//							tfs.addValue(61, String.valueOf(brickMaster.getChibiSlaveAddress()));
							tfs.addValue(100, String.valueOf(brickMaster.getChibiSignalStrength()));
							tfs.addValue(105, String.valueOf(brickMaster.getChibiErrorLog().toString()));
						}
					} catch (TimeoutException e) {
						 System.out.println("ConfigServiceImpl:: report: TimeoutException occurred durch Chibi Extension report. e="+e);
//						e.printStackTrace();
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println(tfs.getRow());
			}
		}
	}
	
	private String getChibiFrequency(short frequencyCode){
		String frequency;
		switch (frequencyCode) {
		case 0:
			frequency = "OQPSK 868Mhz (Europe)";
			break;
		case 1:
			frequency = "OQPSK 915Mhz (US)";
			break;
		case 2:
			frequency = "OQPSK 780Mhz (China)";
			break;
		case 3:
			frequency = "BPSK40 915Mhz";
			break;

		default:
			frequency = "n/a";
			break;
		}
		return frequency;
	}
	
}
