package de.kabuman.tests;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestINet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try 
		{
			InetAddress inet = InetAddress.getByName( "www.fritz.box" );
			
			System.out.println( inet.getHostAddress() );	// Gibt die IP-Adresse zurück
			System.out.println( inet.getHostName() );	// Gibt den Hostname / DNS Namen zurück
			
			System.out.println( inet.toString() );
		}
		catch( UnknownHostException e )
		{
			e.printStackTrace();
		}

	}

}
