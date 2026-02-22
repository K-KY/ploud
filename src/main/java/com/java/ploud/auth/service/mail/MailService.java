package com.java.ploud.auth.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${spring.mail.host}")
    private String host;

    private final JavaMailSender mailSender;

    @Value("${front.path}")
    private String frontPath;
    private String apiPath = "http://localhost:8080/";

    public void sendMail(String to, String subject, String linkUrl) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();


        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(host);
        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                "<div style='background-color: white; padding: 20px; border-radius: 8px; max-width: 600px; margin: auto; " +
                "box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>" +
                "<h1 style='color: #007BFF; margin-top: 0;'>이메일 제목</h1>" +
                "<p style='font-size: 16px; color: #333333;'>내용 본문입니다.</p>" +
                "<a href='" + apiPath + "signup?token=" + linkUrl + "'" +
                " style='display: inline-block; padding: 10px 20px; color: white; background-color: #28a745; " +
                "text-decoration: none; border-radius: 5px; font-weight: bold;'>회원가입 완료하러 가기</a>" +
                "</div>" +
                "</body>" +
                "</html>";
        helper.setText(htmlContent, true); // true: HTML 사용 설정

        mailSender.send(message);
    }
}
