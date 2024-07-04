package com.nailsbyliz.reservation.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender {

    public static void sendEmail(String receiverEmail, String title, String emailBody, String bodyEnd)
            throws Exception {

        String from = System.getenv("MAILGUN_SMTP_LOGIN");
        String pass = System.getenv("MAILGUN_SMTP_PASSWORD");
        String host = System.getenv("MAILGUN_SMTP_SERVER");
        String port = System.getenv("MAILGUN_SMTP_PORT");
        String senderAddress = System.getenv("SENDER_EMAIL_ADDRESS");

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

            // Create MimeBodyPart object for plain text
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(emailBody, "utf-8");

            // Create MimeBodyPart object for HTML content
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(bodyEnd, "text/html; charset=utf-8");

            // Create a Multipart object and add both body parts to it
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);

            // Set the multipart content to the message
            message.setContent(multipart);

            // Send the message
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error while sending.");
            throw e; // Re-throw the exception to handle it properly in the calling method
        }
    }
}
