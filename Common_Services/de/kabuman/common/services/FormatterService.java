package de.kabuman.common.services;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for "FormatterService"
 * 
 * Provides static methods to format often used numeric data types 
 * in connection with tinkerforge objects like BrickDC etc.
 */
public class FormatterService{

	// Definion of display formats
    static DecimalFormat dfLongVoltage = new DecimalFormat( "#0.000" );
    static DecimalFormat dfShortVoltage = new DecimalFormat( "#0.0" );

    static DecimalFormat dfLongCurrent = new DecimalFormat( "#0.000" );
    static DecimalFormat dfShortCurrent = new DecimalFormat( "#0.0" );
    
    static DecimalFormat dfVelocity = new DecimalFormat( "####0" );

    static DecimalFormat dfShort = new DecimalFormat( "#0" );

    public static DecimalFormat dfTemperature = new DecimalFormat( "#########0.0" );
    static DecimalFormat dfHumidity = new DecimalFormat( "00.0" );

    static SimpleDateFormat dfDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
    static SimpleDateFormat dfDateDDMMYYYY = new SimpleDateFormat("dd.MM.yyyy");
    static SimpleDateFormat dfDateHHmmssSSS = new SimpleDateFormat("kk:mm:ss.SSS");
    static SimpleDateFormat dfDateHHmmss = new SimpleDateFormat("kk:mm:ss");
    static SimpleDateFormat dfDateHHMM = new SimpleDateFormat("kk:mm");
    
    public static String getEinAus(Boolean boolValue){
    	if (boolValue == null){
    		return "-";
    	}
    	if (boolValue){
    		return "Ein";
    	} else {
    		return "Aus";
    	}
    }
    
    public static String getJaNein(Boolean boolValue){
    	if (boolValue == null){
    		return "-";
    	}
    	if (boolValue){
    		return "Ja";
    	} else {
    		return "Nein";
    	}
    }
    
	public static String getLongFormVoltage(double voltage) {
		return dfLongVoltage.format(voltage);
	}

	public static String getLongFormCurrent(double current) {
		return dfLongCurrent.format(current);
	}

	public static String getShortFormVoltage(double voltage) {
		return dfShortVoltage.format(voltage);
	}

	public static String getTemperature(double temperature) {
		return dfTemperature.format(temperature);
	}

	public static String getHumidity(double humidity) {
		return dfHumidity.format(humidity);
	}

	public static String getShortFormCurrent(double current) {
		return dfShortCurrent.format(current);
	}

	public static String getFormVelocity(short velocity) {
		return dfVelocity.format(velocity);
	}
	public static String getFormShort(short shortValue) {
		return dfShort.format(shortValue);
	}
	public static String getDateCurrent(){
		return dfDate.format(new Date());
	}
	public static String getDate(Date date){
		return dfDate.format(date);
	}
	public static String getDateDDMMYYYY(Date date){
		return dfDateDDMMYYYY.format(date);
	}
	public static String getDateHHMMSSS(Date date){
		return dfDateHHmmssSSS.format(date);
	}
	public static String getDateHHMMSS(Date date){
		return dfDateHHmmss.format(date);
	}
	public static String getDateHHMM(Date date){
		return dfDateHHMM.format(date);
	}
	public static String getIP(short[] ipParts){
		if (ipParts.length==4){
			StringBuffer sb = new StringBuffer(4);
			for (int i = 3; i > 0; i--) {
				sb.append(ipParts[i]+".");
			}
			sb.append(ipParts[0]);
			return sb.toString();
		} else {
			return null;
		}
	}
	
	
}
