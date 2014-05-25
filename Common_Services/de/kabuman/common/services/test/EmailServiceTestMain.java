package de.kabuman.common.services.test;

import de.kabuman.common.services.EmailService;
import de.kabuman.common.services.EmailServiceImpl;

public class EmailServiceTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        String host="smtp.1und1.de";
        int port=25;
        String user="kabumobil@online.de";
        String password="KVrF92V8RBiGXs0f4ghfz2be2";
        String sentFrom = "Alarmanlage@online.de";
        
		EmailService emailService = new EmailServiceImpl(
				host,
				port,
				user,
				password,
				sentFrom);
		
        String[] sendTo = {user};
        String subject = "EmailService Test furtr";
        String text = "this a test for 2 receiver xxxXXX";

		emailService.sendMail(sendTo, subject, text);

	}

}
