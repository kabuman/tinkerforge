package de.kabuman.common.services.test;

import de.kabuman.common.services.TableFormatterService;
import de.kabuman.common.services.TableFormatterServiceImpl;

public class TableFormatterServiceTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Lineal
		System.out.println("12345678");
		
		// Instantiate and create 1. row
		TableFormatterService tfs = new TableFormatterServiceImpl(8);
		
		// 1. Row filling...
		tfs.addValue(0, "ABC");
		tfs.addValue(4, "DEF");
		tfs.addValue(7, "G");
		try {
			tfs.addValue(9, "H");		// not allowed: out of row length
			System.out.println(tfs.getRow());
		} catch (Exception e) {
			System.out.println("Exception occurred. e="+e.getMessage());
		}
		
		// 2. Row creating and filling...
		tfs.newRow(0, "AB CDEFG");
		System.out.println(tfs.getRow());
		
		// 3. Row creating and filling...
		tfs.newRow(0, "AB CDEFG");
		try {
			tfs.addValue(8, "H");		// not allowed: out of row length
			System.out.println(tfs.getRow());
		} catch (Exception e) {
			System.out.println("Exception occurred. e="+e.getMessage());
		}
	}

}
