/**
 * 
 */
package com.eulerhermes.dev;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Karsten Buchmann
 *
 */
public class ReaderMain {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) {
		
		Set<String> record = new HashSet<String>(30000);
		
		try {
			BufferedReader in = new BufferedReader(new FileReader("myCSV.csv"));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				record.add(zeile);
				System.out.println("Gelesene Zeile: " + zeile);
			}
			
			FileWriter fileWriter = new FileWriter("myCSVout.csv");
			for (Object object : record) {
				fileWriter.append((String) object);
			}
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
