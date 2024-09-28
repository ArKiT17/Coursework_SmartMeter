package com.smartmeter;

import android.content.Context;
import android.util.Log;

import java.util.Objects;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender {
    private Context context;
    private static String from = "smart.meter.pro@gmail.com";
    private static String password = MailPassword.password;
    private static String host = "smtp.gmail.com";
    private Session session;

    public MailSender(Context context) {
        this.context = context;
    }

    private void connect() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        session = javax.mail.Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
    }

    public void send(String fileName, String mailReceiver) {
        connect();
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailReceiver));
            message.setSubject(fileName.substring(0, fileName.length() - 4));

            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("");
            multipart.addBodyPart(messageBodyPart);
            String filePath = context.getApplicationContext().getFilesDir() + "/" + fileName;
            DataSource source = new FileDataSource(filePath);
            messageBodyPart.setText("");
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(message);
                        Thread.sleep(10000);
                        context.deleteFile(fileName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
        }
    }
}
