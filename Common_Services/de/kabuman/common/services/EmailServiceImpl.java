package de.kabuman.common.services;

import java.util.Properties;

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

import com.sun.mail.util.MailSSLSocketFactory;


public class EmailServiceImpl implements EmailService {

    private String host;
    private Integer port;
    private String user;
    private String password;
    private String sendFrom;
    
    private InternetAddress from;
    
    private Properties properties = new Properties();
    
    private javax.mail.Session session;

	public EmailServiceImpl(
			String host,
			Integer port,
			final String user,
			final String password,
			String sendFrom
			) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.sendFrom = sendFrom;
		
        properties.put("mail.transport.protocol","smtp");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port",port);
        properties.put("mail.user",user);
        properties.put("mail.password",password);
        properties.put("mail.smtp.starttls.enable", "true");

        // Authorisation
        Authenticator auth = null;
        auth = new Authenticator()
        {
        	public PasswordAuthentication getPasswordAuthentication()
        	{
        		return new PasswordAuthentication(user,password);
        	}
        };
        
        
        // Session
        session = Session.getInstance(properties,auth);

        
        // send from email
		try {
			from = new InternetAddress(sendFrom);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	
	public void sendMail(
			String[] sendTo,
			String subject,
			String text){
		
        try {
        	Transport tr = session.getTransport("smtp");
        	tr.connect(host, user, password);
        } catch (MessagingException e) {
        	e.printStackTrace();
        	return;
        }

		MimeMessage oMimeMessage = new MimeMessage(session);
		try {
			for (String string : sendTo) {
				try {
					InternetAddress rcpt = new InternetAddress(string);
					oMimeMessage.addRecipient(Message.RecipientType.TO,rcpt);
				} catch (AddressException e) {
					e.printStackTrace();
					return;
				}
			}
			oMimeMessage.setFrom(from);
			oMimeMessage.setSubject(subject);
			oMimeMessage.setText(text);
			Transport.send(oMimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	

}

