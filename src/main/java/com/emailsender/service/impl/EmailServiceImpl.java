package com.emailsender.service.impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.emailsender.helper.Messages;
import com.emailsender.service.EmailService;

import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

  @Value("${spring.mail.from}")
  private String userFrom;

  @Autowired
  private JavaMailSender mailSender;

  private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

  @Override
  public void sendEmail(String to, String subject, String message) {
    try {

      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setTo(to);
      mailMessage.setSubject(subject);
      mailMessage.setText(message);
      mailMessage.setFrom(userFrom);
      mailSender.send(mailMessage);

      logger.info("Message sent successfully to " + to);

    } catch (Exception e) {
      e.printStackTrace();

    }

  }

  @Override
  public void sendEmail(String[] to, String subject, String message) {
    try {
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setTo(to);
      mailMessage.setSubject(subject);
      mailMessage.setText(message);
      mailMessage.setFrom(userFrom);
      mailSender.send(mailMessage);
      logger.info("Mail Successfully sent to multiple users" + to.length);

    } catch (Exception e) {
      e.printStackTrace();

    }

  }

  @Override
  public void sendEmailWithHTML(String to, String subject, String htmlContant) {

    MimeMessage simpleMailMessage = mailSender.createMimeMessage();
    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(simpleMailMessage, true, "UTF-8");
      mimeMessageHelper.setTo(to);
      mimeMessageHelper.setSubject(subject);
      mimeMessageHelper.setText(htmlContant, true);
      mimeMessageHelper.setFrom(userFrom);
      mailSender.send(simpleMailMessage);
      logger.info("Mail Send Succefully " + htmlContant);

    } catch (Exception e) {

    }
  }

  @Override
  public void sendEmailWithFile(String to, String subject, String message, File file) {
    try {
      MimeMessage simplMimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(simplMimeMessage, true);
      mimeMessageHelper.setTo(to);
      mimeMessageHelper.setSubject(subject);
      mimeMessageHelper.setText(message);
      mimeMessageHelper.setFrom(userFrom);
      mimeMessageHelper.addAttachment(file.getName(), file);
      mailSender.send(simplMimeMessage);
      logger.info("Mail Send Successfully with attachment to " + to);

    } catch (Exception e) {

    }

  }

  @Override
  public void sendEmailWithInputFile(String to, String subject, String message, InputStream is) {
    try {
      MimeMessage simplMimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(simplMimeMessage, true);
      mimeMessageHelper.setTo(to);
      mimeMessageHelper.setSubject(subject);
      mimeMessageHelper.setText(message, true);
      mimeMessageHelper.setFrom(userFrom);
      File file = new File("test2.png");
      Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

      // FileSystemResource file = new FileSystemResource(file);
      mimeMessageHelper.addAttachment(file.getName(), file);
      mailSender.send(simplMimeMessage);
      logger.info("Mail Send Successfully with attachment to " + to);

    } catch (Exception e) {
      e.printStackTrace();

    }

  }

  @Value("${mail.store.protocol}")
  String protocol;

  @Value("${mail.imaps.host}")
  String host;

  @Value("${mail.imaps.port}")
  String port;

  @Value("${spring.mail.username}")
  String username;

  @Value("${spring.mail.password}")
  String password;

  @Override
  public List<Messages> getInboxMessages() {
    Properties configurations = new Properties();
    configurations.setProperty("mail.store.protocol", protocol);
    configurations.setProperty("mail.imaps.host", host);
    configurations.setProperty("mail.imaps.port", port);

    Session session = Session.getDefaultInstance(configurations);
    List<Messages> list = new ArrayList<>();

    try {
      Store store = session.getStore();
      store.connect(username, password);
      Folder inbox = store.getFolder("INBOX");
      inbox.open(Folder.READ_ONLY);
      jakarta.mail.Message[] messages = inbox.getMessages();

      for (jakarta.mail.Message message : messages) {

        System.out.println("Subject " + message.getSubject());
        String content = getContentFromEmailMessage(message);
        List<String> files = getFileFromEmailMessage(message);

        list.add(Messages.builder().subject(message.getSubject()).content(content).files(files).build());

      }
      return list;

    } catch (Exception e) {
      e.printStackTrace();

    }
    return list;

  }

  private List<String> getFileFromEmailMessage(jakarta.mail.Message message) throws Exception {

    List<String> files = new ArrayList<>();
    if (message.isMimeType("multipart/*")) {
      Multipart content = (Multipart) message.getContent();
      for (int i = 0; i < content.getCount(); i++) {
        BodyPart bodyPart = content.getBodyPart(i);
        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {

          InputStream inputStream = bodyPart.getInputStream();
          File file = new File("src/main/resources/email/" + bodyPart.getFileName());

          Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
          files.add(file.getAbsolutePath());

        }

      }

    }

    return null;
  }

  private String getContentFromEmailMessage(jakarta.mail.Message message) throws Exception {

    if (message.isMimeType("text/plain") || message.isMimeType("text/html")) {

      return (String) message.getContent();

    } else if (message.isMimeType("multipart/*")) {

      Multipart multipart = (Multipart) message.getContent();
      for (int i = 0; i < multipart.getCount(); i++) {
        BodyPart bodyPart = multipart.getBodyPart(i);
        if (bodyPart.isMimeType("text/plain")) {
          return (String) bodyPart.getContent();
        }
      }

    }
    return null;
  }

}
