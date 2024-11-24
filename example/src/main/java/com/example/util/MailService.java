package com.example.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;

    private final StringRedisTemplate template;

    public void sendEmail(String to) throws MessagingException {
        if(Boolean.TRUE.equals(template.hasKey(to))) {
            template.delete(to);
        }

        String code = createCode();

        MimeMessage email = createEmail(to, code);
        mailSender.send(email);

        ValueOperations<String, String> valueOperations = template.opsForValue();
        valueOperations.set(to, code, 180, TimeUnit.SECONDS);
    }

    public boolean verifyCode(String username, String code) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        String check = valueOperations.get(username);

        if(check != null) {
            return check.equals(code);
        }
        return false;
    }

    private MimeMessage createEmail(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("인증번호 입니다.");
        message.setFrom(from);

        message.setText(createContext(code), "UTF-8", "html");
        return message;
    }

    private String createContext(String code) {
        String body = "";
        body += "<h1>인증 코드 메일입니다.</h1>";
        body += "<h1 style='color: crimson;'>" + code + "</h1>";
        return body;
    }

    private String createCode() {
        Random random = new Random();
        return IntStream.generate(() -> random.nextInt(10))
                        .limit(6)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining());
    }
}
