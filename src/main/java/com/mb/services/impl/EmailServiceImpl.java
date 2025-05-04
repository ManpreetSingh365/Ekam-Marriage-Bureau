package com.mb.services.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.mb.services.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
	public boolean sendEmail(String subject, String message, String to) {

		// Rest of the Code..
		boolean isEmailSend = false;

		String from = "temporarydummymail@gmail.com";

		// Variable for G-mail
		String host = "smtp.gmail.com";

		// Getting the System Properties
		Properties properties = System.getProperties();

		// Setting Important Information to Properties Object--->
		// Host Set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step 1: To getting the Session Object...
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("temporarydummymail@gmail.com", "lvhmkjgpddbiqnke");
			}
		});

		session.setDebug(true);

		// Step 2: Compose the Message [Text, Multi_Media]
		MimeMessage m = new MimeMessage(session);
		try {

			// From Email
			m.setFrom(from);

			// Adding Recipient to Message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Adding Subject to Message
			m.setSubject(subject);

			// Adding Text to Message
			// m.setText(message);
			m.setContent(message, "text/html");

			// Send
			// Step 3: Send the Message using Transport Class
			Transport.send(m);

			isEmailSend = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isEmailSend;
	}
}
