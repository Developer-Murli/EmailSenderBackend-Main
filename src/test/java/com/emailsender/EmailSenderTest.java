package com.emailsender;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.emailsender.helper.Messages;
import com.emailsender.service.EmailService;

@SpringBootTest
public class EmailSenderTest {

  @Autowired
  private EmailService emailService;

  @Test
  public void emailSendTest() {
    System.out.println("Message Sending Test");
    emailService.sendEmail("murlivishwakarma4@gmail.com", "Namra Nivedan",
        "Demo mail from spring boot");

  }

  @Test
  public void emailSenderToMultipleUsers() {
    String[] to = {
        "murlivishwakarma4@gmail.com",
        "abhishekkushwaha5331@gmail.com"
    };
    emailService.sendEmail(to, "Namaskar", "This is demo mail from spring boot to multiple users");
  }

  @Test
  public void emailSenderInHTMLFormat() {
    String htmlContent = "<h1 style='color:red; Text-align:center'>This is Murli Vishwakaram</h1>" +
        "<p>This is All About me , i am Very Happy</p>";
    emailService.sendEmailWithHTML("murlivishwakarma4@gmail.com", "This is Demo by Murli", htmlContent);
  }

  @Test
  public void emailSenderWithAttachement() {
    emailService.sendEmailWithFile("murlivishwakarma4@gmail.com", "Java Developer role",
        "I am drastically Looking the the job role as a Juniar Java Programmer",
        new File("C:\\Users\\asus\\Desktop\\Murli Vish CV.pdf"));
  }

  @Test
  public void emailSenderWithInputStream() {
    try {
      File file = new File("C:\\Users\\asus\\Desktop\\MyPic.jpg");
      InputStream is = new FileInputStream(file);
      emailService.sendEmailWithInputFile("murlivishwakarma4@gmail.com", "God Father",
          "Only your parents are Everything", is);
    } catch (Exception exception) {

    }
  }

  @Test
  public void gettingInbox() {
    try {
      List<Messages> inboxMessages = emailService.getInboxMessages();
      inboxMessages.forEach(item -> {

        System.out.println(item.getSubject());
        System.out.println(item.getContent());
        System.out.println(item.getFiles());
        System.out.println("---------------------------");
      });

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Having some Error while Reading All Inbox Data");
    }
  }

}
