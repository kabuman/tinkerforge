package de.kabuman.common.services;

import com.sun.org.apache.bcel.internal.generic.RETURN;

/**
 * Provides static String Methods
 */
public class StringService {
	
	/**
	 * Returns a fixed length string with the given default value<br>
	 * Please note: The length of the string is = length * length(defaultValue)
	 * 
	 * @param length - the length of the string (length >= 0)
	 * @param defaultValue - the default value (not null)
	 * @return String - the created and with the default value filled string
	 */
	public static String create(int length, String defaultValue){
		if (length < 0){
			throw new IllegalArgumentException("StringService.create:: 'length >= 0' violation detected. length="+length);
		}
		if (defaultValue == null){
			throw new IllegalArgumentException("StringService.create:: 'defaultValue not null' violation detected.");
		}

		StringBuffer sb = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			sb.append(defaultValue);
		}
		return sb.toString();

	}

	/**
	 * Returns a fixed length string filled with blanks
	 * 
	 * @param length - the length of the string (length >= 0)
	 * @return String - the created and with blanks filled string
	 */
	public static String create(int length){
		if (length < 0){
			throw new IllegalArgumentException("StringService.create:: 'length >= 0' violation detected. length="+length);
		}
		return create(length, " ");
	}

	/**
	 * Returns a new string filled with the old value and overwritten with a new value at the given position<br>
	 * Throws IllegalArgumentException if parameter conditions are violated
	 * 
	 * @param targetStrg - the target string (not null; length >= length(overwriteStrg))
	 * @param pos - the start position to overwrite (pos >= 0; pos <= length(targetStrg)
	 * @param overwriteStrg - the new value which will replace the old value at the given position (not null)
	 * @return String - the new String (can be larger than the targetStrg)
	 */
	public static String overwrite(String targetStrg, int pos, String overwriteStrg){
		if (targetStrg == null){
			throw new IllegalArgumentException("StringService.overwrite:: 'targetStrg not null' violation detected.");
		}
		if (overwriteStrg == null){
			throw new IllegalArgumentException("StringService.overwrite:: 'overwriteStrg not null' violation detected.");
		}
		if (pos < 0){
			throw new IllegalArgumentException("StringService.overwrite:: 'pos >= 0' violation detected. pos="+pos);
		}
		if (pos > targetStrg.length()){
			throw new IllegalArgumentException("StringService.overwrite:: 'pos <= length(targetStrg)' violation detected. pos="+pos+" length(targetStrg)="+targetStrg.length());
		}
		
		StringBuffer sb = new StringBuffer(targetStrg);
		sb.replace(pos, pos + overwriteStrg.length(), overwriteStrg);
		return sb.toString();
	}

	/**
	 * Returns a new String filled by targetString. The given insertStrg is inserted.<br>
	 * Throws IllegalArgumentException if parameter conditions are violated
	 * 
	 * @param targetStrg - the target string (not null; length >= length(overwriteStrg))
	 * @param pos - the start position to insert (pos >= 0; pos <= length(targetStrg)
	 * @param insertStrg - the new value which will inserted at the given position (not null)
	 * @return String - the new String (is larger than the targetStrg)
	 */
	public static String insert(String targetStrg, int pos, String insertStrg){
		if (targetStrg == null){
			throw new IllegalArgumentException("StringService.insert:: 'targetStrg not null' violation detected.");
		}
		if (insertStrg == null){
			throw new IllegalArgumentException("StringService.insert:: 'insertStrg not null' violation detected.");
		}
		if (pos < 0){
			throw new IllegalArgumentException("StringService.insert:: 'pos >= 0' violation detected. pos="+pos);
		}
		if (pos > targetStrg.length()){
			throw new IllegalArgumentException("StringService.insert:: 'pos <= length(targetStrg)' violation detected. pos="+pos+" length(targetStrg)="+targetStrg.length());
		}
		
		StringBuffer sb = new StringBuffer(targetStrg);
		sb.replace(pos, pos , insertStrg);
		return sb.toString();
	}
	
	/**
	 * Returns a new String filled left with leading blanks
	 * Throws IllegalArgumentException if parameter conditions are violated

	 * @param valueStrg - the vaule string (not null; value.length >= length(the next parameter)
	 * @param length - the length of the new String ( length >= value.length(the parameter before)
	 * @return String - the new created String
	 */
	public static String fillLeft(String value, int length){
		if (value == null){
			throw new IllegalArgumentException("StringService.fillLeft:: 'value not null' violation detected.");
		}
		if (length < value.length()){
			throw new IllegalArgumentException("StringService.fillLeft:: 'length >= value.length' violation detected. length="+length+" value.length="+value.length());
		}
		
		StringBuffer sb = new StringBuffer(length);

		int emptyDigits = length - value.length();
		if (emptyDigits > 0){
			sb.append(StringService.create(emptyDigits));
		}
		sb.append(value);
		return sb.toString();
		
	}
}
