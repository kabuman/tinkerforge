package de.kabuman.tinkerforge.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickIMU;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.BrickletAnalogIn;
import com.tinkerforge.BrickletAnalogOut;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletCurrent12;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.BrickletJoystick;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMotionDetector;
import com.tinkerforge.BrickletPiezoBuzzer;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.BrickletVoltage;
import com.tinkerforge.IPConnection;
import com.tinkerforge.TimeoutException;

public class ConnectDeviceServiceImpl implements DeviceIdentifier{
	/**
	 * @param ipcon
	 * @param id
	 * @return
	 * @throws TimeoutException
	 */
	public static DeviceUsage createAndConnectDevice(IPConnection ipcon, int id) throws TimeoutException{
		DeviceUsage deviceUsage = null;
		
		switch (id) {
		case MB1:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB1,ipcon), ipcon, MB1, UID_MB1);
			break;
		case MB2:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB2,ipcon), ipcon, MB2, UID_MB2);
			break;
		case MB3:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB3,ipcon), ipcon, MB3, UID_MB3);
			break;
		case MB4:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB4,ipcon), ipcon, MB4, UID_MB4);
			break;
		case MB5:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB5,ipcon), ipcon, MB5, UID_MB5);
			break;
		case MB6:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB6,ipcon), ipcon, MB6, UID_MB6);
			break;
		case MB7:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB7,ipcon), ipcon, MB7, UID_MB7);
			break;
		case MB8:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB8,ipcon), ipcon, MB8, UID_MB8);
			break;
		case MB9:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB9,ipcon), ipcon, MB9, UID_MB9);
			break;
		case MB10:
			deviceUsage = new DeviceUsage(new BrickMaster(UID_MB10,ipcon), ipcon, MB10, UID_MB10);
			break;
		case LCD201:
			deviceUsage = new DeviceUsage(new BrickletLCD20x4(UID_LCD201,ipcon), ipcon, LCD201, UID_LCD201);
			break;
		case AL1:
			deviceUsage = new DeviceUsage(new BrickletAmbientLight(UID_AL1,ipcon), ipcon, AL1, UID_AL1);
			break;
		case DC1:
			deviceUsage = new DeviceUsage(new BrickDC(UID_DC1,ipcon), ipcon, DC1, UID_DC1);
			break;
		case DC2:
			deviceUsage = new DeviceUsage(new BrickDC(UID_DC2,ipcon), ipcon, DC2, UID_DC2);
			break;
		case DC3:
			deviceUsage = new DeviceUsage(new BrickDC(UID_DC3,ipcon), ipcon, DC3, UID_DC3);
			break;
		case DC4:
			deviceUsage = new DeviceUsage(new BrickDC(UID_DC4,ipcon), ipcon, DC4, UID_DC4);
			break;
		case IR1:
			deviceUsage = new DeviceUsage(new BrickletDistanceIR(UID_IR1,ipcon), ipcon, IR1, UID_IR1);
			break;
		case IR2:
			deviceUsage = new DeviceUsage(new BrickletDistanceIR(UID_IR2,ipcon), ipcon, IR2, UID_IR2);
			break;
		case IR3:
			deviceUsage = new DeviceUsage(new BrickletDistanceIR(UID_IR3,ipcon), ipcon, IR3, UID_IR3);
			break;
		case IR4:
			deviceUsage = new DeviceUsage(new BrickletDistanceIR(UID_IR4,ipcon), ipcon, IR4, UID_IR4);
			break;
		case RP1:
			deviceUsage = new DeviceUsage(new BrickletRotaryPoti(UID_RP1,ipcon), ipcon, RP1, UID_RP1);
			break;
		case RP7:
			deviceUsage = new DeviceUsage(new BrickletRotaryPoti(UID_RP7,ipcon), ipcon, RP7, UID_RP7);
			break;
		case SV1:
			deviceUsage = new DeviceUsage(new BrickServo(UID_SV1,ipcon), ipcon, SV1, UID_SV1);
			break;
		case JS1:
			deviceUsage = new DeviceUsage(new BrickletJoystick(UID_JS1,ipcon), ipcon, JS1, UID_JS1);
			break;
		case VO1:
			deviceUsage = new DeviceUsage(new BrickletVoltage(UID_VO1,ipcon), ipcon, VO1, UID_VO1);
			break;
		case IO41:
			deviceUsage = new DeviceUsage(new BrickletIO4(UID_IO41,ipcon), ipcon, IO41, UID_IO41);
			break;
		case IO42:
			deviceUsage = new DeviceUsage(new BrickletIO4(UID_IO42,ipcon), ipcon, IO42, UID_IO42);
			break;
		case IO43:
			deviceUsage = new DeviceUsage(new BrickletIO4(UID_IO43,ipcon), ipcon, IO43, UID_IO43);
			break;
		case IO44:
			deviceUsage = new DeviceUsage(new BrickletIO4(UID_IO44,ipcon), ipcon, IO44, UID_IO44);
			break;
		case IO45:
			deviceUsage = new DeviceUsage(new BrickletIO4(UID_IO45,ipcon), ipcon, IO45, UID_IO45);
			break;
		case IO161:
			deviceUsage = new DeviceUsage(new BrickletIO16(UID_IO161,ipcon), ipcon, IO161, UID_IO161);
			break;
		case IO162:
			deviceUsage = new DeviceUsage(new BrickletIO16(UID_IO162,ipcon), ipcon, IO162, UID_IO162);
			break;
		case CU121:
			deviceUsage = new DeviceUsage(new BrickletCurrent12(UID_CU121,ipcon), ipcon, CU121, UID_CU121);
			break;
		case CU122:
			deviceUsage = new DeviceUsage(new BrickletCurrent12(UID_CU122,ipcon), ipcon, CU122, UID_CU122);
			break;
		case DR1:
			deviceUsage = new DeviceUsage(new BrickletDualRelay(UID_DR1,ipcon), ipcon, DR1, UID_DR1);
			break;
		case DR2:
			deviceUsage = new DeviceUsage(new BrickletDualRelay(UID_DR2,ipcon), ipcon, DR2, UID_DR2);
			break;
		case DR3:
			deviceUsage = new DeviceUsage(new BrickletDualRelay(UID_DR3,ipcon), ipcon, DR3, UID_DR3);
			break;
		case IMU1:
			deviceUsage = new DeviceUsage(new BrickIMU(UID_IMU1,ipcon), ipcon, IMU1, UID_IMU1);
			break;
		case RP2:
			deviceUsage = new DeviceUsage(new BrickletRotaryPoti(UID_RP2,ipcon), ipcon, RP2, UID_RP2);
			break;
		case RP3:
			deviceUsage = new DeviceUsage(new BrickletRotaryPoti(UID_RP3,ipcon), ipcon, RP3, UID_RP3);
			break;
		case RP4:
			deviceUsage = new DeviceUsage(new BrickletRotaryPoti(UID_RP4,ipcon), ipcon, RP4, UID_RP4);
			break;
		case RP5:
			deviceUsage = new DeviceUsage(new BrickletRotaryPoti(UID_RP5,ipcon), ipcon, RP5, UID_RP5);
			break;
		case RP6:
			deviceUsage = new DeviceUsage(new BrickletRotaryPoti(UID_RP6,ipcon), ipcon, RP6, UID_RP6);
			break;
		case AI1:
			deviceUsage = new DeviceUsage(new BrickletAnalogIn(UID_AI1,ipcon), ipcon, AI1, UID_AI1);
			break;
		case AI2:
			deviceUsage = new DeviceUsage(new BrickletAnalogIn(UID_AI2,ipcon), ipcon, AI2, UID_AI2);
			break;
		case AI3:
			deviceUsage = new DeviceUsage(new BrickletAnalogIn(UID_AI3,ipcon), ipcon, AI3, UID_AI3);
			break;
		case AI4:
			deviceUsage = new DeviceUsage(new BrickletAnalogIn(UID_AI4,ipcon), ipcon, AI4, UID_AI4);
			break;
		case AO1:
			deviceUsage = new DeviceUsage(new BrickletAnalogOut(UID_AO1,ipcon), ipcon, AO1, UID_AO1);
			break;
		case AO2:
			deviceUsage = new DeviceUsage(new BrickletAnalogOut(UID_AO2,ipcon), ipcon, AO2, UID_AO2);
			break;
		case BM1:
			deviceUsage = new DeviceUsage(new BrickletBarometer(UID_BM1,ipcon), ipcon, BM1, UID_BM1);
			break;
		case BM2:
			deviceUsage = new DeviceUsage(new BrickletBarometer(UID_BM2,ipcon), ipcon, BM2, UID_BM2);
			break;
		case TP1:
			deviceUsage = new DeviceUsage(new BrickletTemperature(UID_TP1,ipcon), ipcon, TP1, UID_TP1);
			break;
		case TP2:
			deviceUsage = new DeviceUsage(new BrickletTemperature(UID_TP2,ipcon), ipcon, TP2, UID_TP2);
			break;
		case TP3:
			deviceUsage = new DeviceUsage(new BrickletTemperature(UID_TP3,ipcon), ipcon, TP3, UID_TP3);
			break;
		case HM1:
			deviceUsage = new DeviceUsage(new BrickletHumidity(UID_HM1,ipcon), ipcon, HM1, UID_HM1);
			break;
		case HM2:
			deviceUsage = new DeviceUsage(new BrickletHumidity(UID_HM2,ipcon), ipcon, HM2, UID_HM2);
			break;
		case HM3:
			deviceUsage = new DeviceUsage(new BrickletHumidity(UID_HM3,ipcon), ipcon, HM3, UID_HM3);
			break;
		case PS1:
			deviceUsage = new DeviceUsage(new BrickletPiezoBuzzer(UID_PS1,ipcon), ipcon, PS1, UID_PS1);
			break;
		case RS1:
			deviceUsage = new DeviceUsage(new BrickletRemoteSwitch(UID_RS1,ipcon), ipcon, RS1, UID_RS1);
			break;
		case MD1:
			deviceUsage = new DeviceUsage(new BrickletMotionDetector(UID_MD1,ipcon), ipcon, MD1, UID_MD1);
			break;
		case MD2:
			deviceUsage = new DeviceUsage(new BrickletMotionDetector(UID_MD2,ipcon), ipcon, MD2, UID_MD2);
			break;
		case MD3:
			deviceUsage = new DeviceUsage(new BrickletMotionDetector(UID_MD3,ipcon), ipcon, MD3, UID_MD3);
			break;
		default:
			System.out.println("createAndConnectBM:: Unbekannte Geräte-ID="+id);
		return null;
		}
		return deviceUsage;
	}

}
