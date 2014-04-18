package de.kabuman.common.services;

public interface EmailService {
	
	public void sendMail(
			String[] sendTo,
			String subject,
			String text);


}
