package com.java.ploud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.debug_dev}")
    private String debug;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username); // 지메일 계정
        mailSender.setPassword(password);    // 앱 비밀번호

        mailSender.setJavaMailProperties(getProperties());

        return mailSender;
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");// 전송 프로토콜 지정
        props.put("mail.smtp.auth", "true");// 서버가 로그인을 요구하는지 설정
        props.put("mail.smtp.starttls.enable", "true");//암호화된 연결을 사용할지 여부
        props.put("mail.debug", debug);//메일 잔송 과정을 로그로 출력 운영 환경은 false
        props.put("mail.smtp.connectiontimeout", "3000");//SMTP 서버 타임아웃 시간, 밀리초
        return props;
    }
}
