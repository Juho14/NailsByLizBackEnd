package com.nailsbyliz.reservation.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public static void sendEmail(String receiverEmail, String title, String emailBody) throws Exception {

        String from = System.getenv("MAILGUN_SMTP_LOGIN");
        String pass = System.getenv("MAILGUN_SMTP_PASSWORD");
        String host = System.getenv("MAILGUN_SMTP_SERVER");
        String port = System.getenv("MAILGUN_SMTP_PORT");
        String senderAddress = System.getenv("SENDER_EMAIL_ADDRESS");

        System.out.println("MAILGUN_SMTP_LOGIN: " + System.getenv("MAILGUN_SMTP_LOGIN"));
        System.out.println("MAILGUN_SMTP_PASSWORD: " + System.getenv("MAILGUN_SMTP_PASSWORD"));
        System.out.println("MAILGUN_SMTP_SERVER: " + System.getenv("MAILGUN_SMTP_SERVER"));
        System.out.println("MAILGUN_SMTP_PORT: " + System.getenv("MAILGUN_SMTP_PORT"));
        System.out.println("SENDER_EMAIL_ADDRESS: " + System.getenv("SENDER_EMAIL_ADDRESS"));
        System.out.println("RECIEVER EMAIL: " + receiverEmail);
        System.out.println(emailBody);

        Properties properties = System.getProperties();
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Get the default session object
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(senderAddress));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            message.setSubject(title);

            message.setText(emailBody);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error while sending.");

        }
    }
}