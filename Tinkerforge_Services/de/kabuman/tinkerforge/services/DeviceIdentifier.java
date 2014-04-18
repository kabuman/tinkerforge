package de.kabuman.tinkerforge.services;

public interface DeviceIdentifier {
	// device serial number
	public static final String UID_DC1 = new String("6eRLuj"); 		// !Motor Driver
	public static final String UID_DC2 = new String("9Jk6h66rvCw"); // Motor Driver 				(03.05.2012)
	public static final String UID_DC3 = new String("94yAGvbKm9s");	// Motor Driver					(03.05.2012)
	public static final String UID_DC4 = new String("6QHSh3");		// !Motor Driver				(14.08.2012)

	public static final String UID_IR1 = new String("767");			// Infrarot Sensor (10-80cm)
	public static final String UID_IR2 = new String("763");			// Infrarot Sensor (10-80cm)
	public static final String UID_IR3 = new String("6GG");			// Infrarot Sensor (4-30cm) 	(03.05.2012)
	public static final String UID_IR4 = new String("6GW");			// Infrarot Sensor (7-80cm)		(03.05.2012) => noch zu kalibrieren!

	public static final String UID_RP1 = new String("7y9");			// Rotary Poti
	public static final String UID_RP2 = new String("aDE");			// Rotary Poti					(06.11.2012)
	public static final String UID_RP3 = new String("aB3");			// Rotary Poti					(06.11.2012)
	public static final String UID_RP4 = new String("aAf");			// Rotary Poti					(06.11.2012)
	public static final String UID_RP5 = new String("aBL");			// Rotary Poti					(06.2013)
	public static final String UID_RP6 = new String("cFb");			// Rotary Poti					(06.2013)
	public static final String UID_RP7 = new String("cF7");			// Rotary Poti					(06.2013)

	// Master Bricks
	public static final String UID_MB1 = new String("94ANbubC7Ga");	// Master Brick
	public static final String UID_MB2 = new String("a4LCMzahF8P");	// Master Brick
	public static final String UID_MB3 = new String("62eyyG");		// !Master Brick					(11.05.2012)
	public static final String UID_MB4 = new String("6qzyUz");		// Master Brick					(30.05.2012)
	public static final String UID_MB5 = new String("68XaEH");		// !Master Brick					(.06.2013)
	public static final String UID_MB6 = new String("6wvP5D");		// !Master Brick					(.06.2013)
	public static final String UID_MB7 = new String("62BqLy");		// !Master Brick 					(17.08.2013)
	public static final String UID_MB8 = new String("5Vmi4w");		// !Master Brick 					(28.08.2013)
	public static final String UID_MB9 = new String("6R5Dxo");		// !Master Brick 					(18.09.2013)
	
		
	public static final String UID_JS1 = new String("7s3");			// Joystick

	public static final String UID_VO1 = new String("6vd");			// Voltage						(03.05.2012)

	public static final String UID_IO41 = new String("79W");		// Input / Output - 4 channel	(03.05.2012)
	public static final String UID_IO42 = new String("eYa");		// Input / Output - 4 channel	(28.08.2013)
	public static final String UID_IO43 = new String("eYB");		// Input / Output - 4 channel	(28.08.2013)
	public static final String UID_IO44 = new String("eYr");		// Input / Output - 4 channel	(18.09.2013)
	public static final String UID_IO45 = new String("eXY");		// Input / Output - 4 channel	(18.09.2013)
	
	

	public static final String UID_IO161 = new String("8P5");		// Input / Output - 16 channel	(30.05.2012)
	public static final String UID_IO162 = new String("gkH");		// Input / Output - 16 channel	(18.09.2013)
	

	public static final String UID_CU121 = new String("7r7");		// Current 12					(11.05.2012)
	public static final String UID_CU122 = new String("7rj");		// Current 12					(11.05.2012)

	public static final String UID_DR1 = new String("6C7");			// Dual Relay					(11.05.2012)
	public static final String UID_DR2 = new String("6BW");			// Dual Relay					(11.05.2012)
	public static final String UID_DR3 = new String("6C3");			// Dual Relay					(11.05.2012)

	public static final String UID_QR1 = new String("gR8");			// Industrial Quad Relay		(17.08.2013)

	public static final String UID_LCD201 = new String("81N");		// LCD 20 x 4					(11.05.2012)

	public static final String UID_SV1 = new String("6JnHkq");	// !Servo Brick					(30.05.2012)

	public static final String UID_AL1 = new String("8SE");			// Ambient Light				(30.05.2012)

	public static final String UID_IMU1 = new String("9oTxLAX9p3j");// IMU							(03.08.2012)

	public static final String UID_AI1 = new String("bJE");			// Analog In 0-45V				(06..2013)
	public static final String UID_AI2 = new String("bKU");			// Analog In 0-45V				(06..2013)
	public static final String UID_AI3 = new String("bJh");			// Analog In 0-45V				(06..2013)
	public static final String UID_AI4 = new String("bKS");			// Analog In 0-45V				(06..2013)

	public static final String UID_AO1 = new String("br3");			// Analog Out 0-5V				(28.08.2013)
	public static final String UID_AO2 = new String("bqs");			// Analog Out 0-5V				(28.08.2013)
	
	public static final String UID_DI1 = new String("dtQ");			// Industrial Digital In 0-36V	(17.08.2013)

	public static final String UID_BM1 = new String("bM5");			// Barometer					(06..2013)
	public static final String UID_BM2 = new String("da9");			// Barometer					(06..2013)

	public static final String UID_GP1 = new String("cPs");			// !GPS Receiver					(06..2013)

	public static final String UID_TP1 = new String("dSA");			// Temperature					(01.11.2013)
	public static final String UID_TP2 = new String("dZE");			// Temperature					(01.11.2013)
	public static final String UID_TP3 = new String("dDj");			// Temperature					(01.11.2013)
	public static final String UID_HM1 = new String("hVz");			// Humidity						(01.11.2013)
	public static final String UID_HM2 = new String("hUR");			// Humidity						(01.11.2013)
	public static final String UID_HM3 = new String("hXP");			// Humidity						(01.11.2013)
	
	public static final String UID_PS1 = new String("iMc");			// Piezo Speaker 				(08.01.2014)
	public static final String UID_MB10 = new String("6xCEaf");		// Master Brick 				(08.01.2014)
	public static final String UID_MD1 = new String("jXo");			// Motion Detection				(08.01.2014)
	public static final String UID_RS1 = new String("jP3");			// Remote Switch				(08.01.2014)

	public static final String UID_MD2 = new String("krR");			// Motion Detection				(24.02.2014)
	public static final String UID_MD3 = new String("krT");			// Motion Detection				(24.02.2014)
	public static final String UID_DB1 = new String("itz");			// Dual Button					(24.02.2014)
	public static final String UID_DB2 = new String("j3n");			// Dual Button					(24.02.2014)
	public static final String UID_HE1 = new String("kpg");			// Hall Effect					(24.02.2014)
	public static final String UID_HE2 = new String("kmz");			// Hall Effect					(24.02.2014)
	

	// SD1: Power Supply Step-Down (03.05.2012)
	// SD2: Power Supply Step-Down (11.05.2012)
	// SD3: Power Supply Step-Down (30.05.2012)
	
	// CE1: Chibi Master Extension (11.05.2012)
	// CE2: Chibi Master Extension (11.05.2012)
	// CE3: Chibi Master Extension (11.05.2012)
	
	// WE1; Wifi Extension 			(.06.2013)
	// WE2:	Wifi Extension			(.06.2013)		buzinet:192.168.178.43
	// WE3:	Wifi Extension			(14.08.2013)
	// WE4:	Wifi Extension			(28.08.2013)
	

	
	
	// device identifier
	public static final int DC1 = 1;
	public static final int IR1 = 2;
	public static final int IR2 = 3;
	public static final int RP1 = 4;
	public static final int MB1 = 5;
	public static final int MB2 = 6;
	public static final int JS1 = 7;
	public static final int DC2 = 8;
	public static final int DC3 = 9;
	public static final int IR3 = 10;
	public static final int IR4 = 11;
	public static final int VO1 = 12;
	public static final int MB3 = 13;
	public static final int CU121 = 14;
	public static final int CU122 = 15;
	public static final int DR1 = 16;
	public static final int DR2 = 17;
	public static final int DR3 = 18;
	public static final int LCD201 = 19;
	public static final int IO41 = 20;
	public static final int IO161 = 21;
	public static final int SV1 = 22;
	public static final int MB4 = 23;
	public static final int AL1 = 24;
	public static final int IMU1 = 25;
	public static final int RP2 = 26;	// Strobe
	public static final int RP3 = 27;	// Strobe
	public static final int RP4 = 28;
	public static final int MB5 = 29;
	public static final int MB6 = 30;
	public static final int RP5 = 31;
	public static final int RP6 = 32;
	public static final int AI1 = 33;
	public static final int AI2 = 34;
	public static final int AI3 = 35;
	public static final int AI4 = 36;
	public static final int BM1 = 37;
	public static final int BM2 = 38;
	public static final int GP1 = 39;
	public static final int DC4 = 40;
	public static final int MB7 = 41;
	public static final int QR1 = 42;
	public static final int DI1 = 43;
	public static final int MB8 = 44;
	public static final int IO42 = 45;
	public static final int IO43 = 46;
	public static final int AO1 = 47;
	public static final int AO2 = 48;
	public static final int MB9 = 49;
	public static final int IO162 = 50;
	public static final int IO44 = 51;
	public static final int IO45 = 52;
	public static final int TP1 = 53;
	public static final int TP2 = 54;
	public static final int TP3 = 55;
	public static final int HM1 = 56;
	public static final int HM2 = 57;
	public static final int HM3 = 58;
	public static final int RP7 = 59;
	public static final int MB10 = 60;
	public static final int PS1 = 61;
	public static final int MD1 = 62;
	public static final int RS1 = 63;
	public static final int MD2 = 64;
	public static final int MD3 = 65;

	

}
