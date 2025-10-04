package com.emailsender.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.emailsender.Entity.EmailRequest;
import com.emailsender.helper.CustomResponse;
import com.emailsender.service.EmailService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/email/v1")
public class EmailController {

  @Autowired
  private EmailService emailService;

  @PostMapping("/send")
  public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest) {
    try {
      emailService.sendEmailWithHTML(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessage());
      return ResponseEntity
          .ok(CustomResponse.builder()
              .message("Email Sent Successfully")
              .httpStatus(HttpStatus.OK)
              .success(true)
              .build());
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(CustomResponse.builder()
              .message("Error while sending mail")
              .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
              .success(false)
              .build());
    }

  }

  @PostMapping("/send-html")
  public ResponseEntity<?> sendEmailWithHtml(@RequestBody EmailRequest emailRequest) {
    try {
      emailService.sendEmailWithHTML(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessage());
      return ResponseEntity
          .ok(CustomResponse.builder()
              .message("Email Sent Successfully")
              .httpStatus(HttpStatus.OK)
              .success(true)
              .build());
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(CustomResponse.builder()
              .message("Error while sending mail")
              .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
              .success(false)
              .build());
    }

  }

  @PostMapping("/send-file")
  public ResponseEntity<CustomResponse> sendEmailWithFile(@RequestPart("emailRequest") EmailRequest emailRequest,
      @RequestPart("file") MultipartFile file) throws IOException {

    emailService.sendEmailWithInputFile(emailRequest.getTo(), emailRequest.getSubject(),
        emailRequest.getMessage(),
        file.getInputStream());
    return ResponseEntity.ok(CustomResponse.builder().message("Email sent Successfully with attachment")
        .httpStatus(HttpStatus.OK)
        .success(true)
        .build());

  }

}
