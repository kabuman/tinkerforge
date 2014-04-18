package de.kabuman.tests;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestEmail {

	/**
	 * @param args
	 * @throws NoSuchProviderException 
	 */
	public static void main(String[] args){
        String server="smtp.1und1.de";
        int port=25;
        final String user="kabumobil@online.de";
        final String password="";
        
        Properties prop = new Properties();
        prop.put("mail.transport.protocol","smtp");
        prop.put("mail.smtp.auth","true");
        prop.put("mail.smtp.host",server);
        prop.put("mail.smtp.port",port);
        prop.put("mail.user",user);
        prop.put("mail.password",password);
        
        Authenticator auth=null;
        auth=new Authenticator()
        {
        public PasswordAuthentication getPasswordAuthentication()
        {
        return new PasswordAuthentication(user,password);
        }
        };
        javax.mail.Session ses1=Session.getInstance(prop,auth);
        
        
        InternetAddress from;
		try {
			from = new InternetAddress("kabumobil@online.de");
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		MimeMessage oMimeMessage = new MimeMessage(ses1);
        try {
			oMimeMessage.setFrom(from);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        InternetAddress rcpt;
		try {
			rcpt = new InternetAddress("kabumobil@online.de");
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
        try {
			oMimeMessage.addRecipient(Message.RecipientType.TO,rcpt);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			oMimeMessage.setSubject("TEST");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			oMimeMessage.setText("sa");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Transport tr;
		try {
			tr = ses1.getTransport("smtp");
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
        try {
			tr.connect("smtp.1und1.de", "kabumobil@online.de", "KVrF92V8RBiGXs0f4ghfz2be2");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			Transport.send(oMimeMessage);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
