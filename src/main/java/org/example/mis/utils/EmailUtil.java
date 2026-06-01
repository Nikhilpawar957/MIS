package org.example.mis.utils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailUtil {

	private final JavaMailSender mailSender;
	
	private final TemplateEngine templateEngine;

	public void sendHtmlMail(
            String to,
            String subject,
            String templateName,
            Map<String, Object> variables
    ) {

        try {

            Context context = new Context();

            if (variables != null) {
                variables.forEach(context::setVariable);
            }

            String html =
                    templateEngine.process(
                            templateName,
                            context
                    );

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to send email",
                    e
            );
        }
    }
}
