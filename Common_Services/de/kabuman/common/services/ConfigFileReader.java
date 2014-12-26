package de.kabuman.common.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implements a buffered file reader for a comfortable read in of config file records
 *
 */
public class ConfigFileReader {

	private FileReader fileReader;
	private BufferedReader bufferedReader;
	
	private String line;
	private String[] vars;
	private String sep;
	
	public DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
	public DateFormat simpleTimeFormat = new SimpleDateFormat("kk:mm:ss");
	public DateFormat simpleDateTimeFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");

	/**
	 * Constructor
	 */
	public ConfigFileReader() {
	}

	/**
	 * Opens the given filePathName
	 * 
	 * @param filePathName - path + fileName
	 * @param sep - separator to separate the variables for reading config file records
	 * @return true: if file could open / false: if not (FileNotFoundException)
	 */
	public boolean openFile(String filePathName, String sep){
		this.sep = sep;
		try {
			fileReader = new FileReader(filePathName);
		} catch (FileNotFoundException e) {
			return false;
		}
		bufferedReader = new BufferedReader(fileReader);
		return true;
	}
	
	/**
	 * Read a line. <br>
	 * Will close the file if it could not successfully read (IOException) <br>
	 * Returns null in this case. <br>
	 *  <br>
	 * @return String - the trimmed line which has been read successfully
	 */
	public String readLine(){
		line = null;
		vars = null;
		try {
			line = bufferedReader.readLine();
			while (line != null && (isEmptyLine(line)||isCommentLine(line))) {
				line = bufferedReader.readLine();
			}
			if (line != null){
				vars = line.split(sep);
			}
		} catch (IOException e) {
			closeFile();
			return null;
		}
		
		if (line==null){
			return null;
		} else {
			return line.trim();
		}
	}
	
	/**
	 * Set a line. <br>
	 *  <br>
	 * @return String - the trimmed line 
	 */
	
	
	/**
	 * Set a line and the var separator
	 * Can be used if the line does not come from a config file
	 * 
	 * @param line - the line to disamble
	 * @param sep - separator to separate the variables for the given line
	 * @return line - the trimmed line
	 */
	public String setLine(String line, String sep){
		this.sep = sep;
		vars = null;

		if (line != null){
			vars = line.split(sep);
		}

		if (line==null){
			return null;
		} else {
			return line.trim();
		}
	}
	
	private boolean isEmptyLine(String line){
		if (line.trim().length() == 0){
			return true;
		} else {
			return false;
		}
	}

	private boolean isCommentLine(String line){
		// Comment line: empty line
		if (line.length() == 0){
			return true;
		}
		
		// Comment line: leading space
		if (line.length()>0 && line.substring(0, 1) == " "){
			return true;
		}
		
		// Comment line: not valid recordType or recordType=0
		try {
			int recordType = Integer.parseInt(line.split(sep)[0]);  	
			if (recordType == 0){
				// recordType=0 is comment line
				return true;
			} else {
				// recordType!=0 is NOT a comment line
				return false;
			}
		} catch (NumberFormatException e) {
			// no valid number: must be a comment line
			return true;
		}
	}
	
	/**
	 * Returns the slitted and separated variables  <br>
	 *  <br>
	 * @return String[] - the array with the separated variables
	 */
	public String[] getVars(){
		return vars;
	}
	
	/**
	 * Returns the content of the given array position as Integer  <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return Integer - the casted Integer
	 */
	public Integer getInteger(int pos){
		return Integer.parseInt(vars[pos]);
	}
	
	/**
	 * Returns the content of the given array position as Long  <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return Long - the casted Long
	 */
	public Long getLong(int pos){
		return Long.parseLong(vars[pos]);
	}
	
	/**
	 * Returns the content of the given array position as Long  <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return Long - the casted Long
	 */
	public Short getShort(int pos){
		return Short.parseShort(vars[pos]);
	}
	
	/**
	 * Returns the content of the given array position as int  <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return int - the casted int
	 */
	public int getInt(int pos){
		return Integer.parseInt(vars[pos]);
	}
	
	/**
	 * Returns the record type (1. part separated by SEP)  <br>
	 * If the line is a comment line then a Integer.ZERO will be returned  <br>
	 *  <br>
	 * @return Integer - the record type (is zero in the case of a comment line)
	 */
	public Integer getRecordType(){
		if (isCommentLine(line)){
			return new Integer(0);
		}
		return getInteger(0);
	}
	
	/**
	 * Returns the content of the given array position as string  <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return String - the string
	 */
	public String getString(int pos){
		return vars[pos];
	}
	
	/**
	 * Returns the content of the given array position as Boolean  <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return Boolean - the Boolean (true: 1; false <> 1);
	 */
	public Boolean getBoolean(int pos){
		return (Integer.parseInt(vars[pos]) == 1);
	}
	
	/**
	 * Returns the content of the given array position as Date  <br>
	 * The format will b mm.dd.yyyy <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return Date - "mm.dd.yyyy 00:01:00"
	 */
	public Date getDate(int pos){
		try {
			return simpleDateFormat.parse(vars[pos]);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Returns the content of the given array position as Date Time  <br>
	 * From the given Date object the time "hh:mm:ss" ONLY will be taken over <br>
	 * The format will be "dd.mm.yyyy hh.mm.ss" <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return Date - "mm.dd.yyyy hh:mm:ss"
	 */
	@SuppressWarnings("deprecation")
	public Date getDate(int pos, Date givenTime){
		try {
			Date result = simpleDateFormat.parse(vars[pos]);
			result.setHours(givenTime.getHours());
			result.setMinutes(givenTime.getMinutes());
			result.setSeconds(givenTime.getSeconds());
			return result;
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Returns the content of the given array position as Time  <br>
	 * The format will be hh.mm.ss <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return Date - "01.01.1970 hh.mm.ss"
	 */
	public Date getTime(int pos){
		try {
			return simpleTimeFormat.parse(vars[pos]);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Returns the content of the given array position as Date Time  <br>
	 * From the given Date object the date "dd.mm.yyyy" ONLY will be taken over <br>
	 * The format will be "dd.mm.yyyy hh.mm.ss" <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return Date - "dd.mm.yyyy hh.mm.ss"
	 */
	@SuppressWarnings("deprecation")
	public Date getTime(int pos, Date givenDate){
		try {
			Date result = new Date(givenDate.getTime());
			Date time = simpleTimeFormat.parse(vars[pos]);
			result.setHours(time.getHours());
			result.setMinutes(time.getMinutes());
			result.setSeconds(time.getSeconds());
			return result;
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Returns the content of the given array position as Date Time  <br>
	 * The format will be "dd.mm.yyyy hh.mm.ss" <br>
	 *  <br>
	 * @param pos - the array position (index)
	 * @return Date - "dd.mm.yyyy hh.mm.ss"
	 */
	public Date getDateTime(int pos){
		try {
			return simpleDateTimeFormat.parse(vars[pos]);
		} catch (IndexOutOfBoundsException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Close the file 
	 */
	public void closeFile(){
		try {
			bufferedReader.close();
		} catch (IOException e1) {
		}
	}

}
