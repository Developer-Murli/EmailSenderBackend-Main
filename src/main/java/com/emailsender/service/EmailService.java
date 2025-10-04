package com.emailsender.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.emailsender.helper.Messages;

public interface EmailService {

  // send email to single persion
  void sendEmail(String to, String subject, String message);

  // send email to multiple persion
  void sendEmail(String[] to, String subject, String message);

  // Send email with HTML content
  void sendEmailWithHTML(String to, String subject, String message);

  // send email with file
  void sendEmailWithFile(String to, String subject, String message, File file);

  // send email with InputStream
  void sendEmailWithInputFile(String to, String subject, String message, InputStream is);

  // imap protocol Method
  List<Messages> getInboxMessages();

}
