package sumdu.edu.ua.spring.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ua.edu.catalog.core.model.Book;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;
    private final EmailTemplateProcessor templateProcessor;
    private final String sender;
    private final String recipient;
    private final boolean enabled;
    private final String logoUrl;

    public MailService(JavaMailSender mailSender, EmailTemplateProcessor templateProcessor,
            @Value("${spring.mail.username:}") String sender,
            @Value("${bookapp.mail.to:}") String recipient,
            @Value("${bookapp.mail.enabled:false}") boolean enabled,
            @Value("${bookapp.mail.logo-url:}") String logoUrl) {
        this.mailSender = mailSender;
        this.templateProcessor = templateProcessor;
        this.sender = sender;
        this.recipient = recipient;
        this.enabled = enabled;
        this.logoUrl = logoUrl;
    }

    public void sendNewBookEmail(Book book) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", book.title());
        model.put("author", book.author());
        model.put("year", book.year());
        model.put("added", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        model.put("logoUrl", logoUrl);
        String html = templateProcessor.process("new_book.ftl", model);
        if (!enabled) {
            log.info("SMTP вимкнено: HTML-лист для книги '{}' сформовано, але не відправлено", book.title());
            return;
        }
        if (sender.isBlank() || recipient.isBlank()) {
            throw new IllegalStateException("Вкажіть GMAIL_USERNAME та BOOKAPP_MAIL_TO");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setSubject("Нова книга в каталозі: " + book.title());
            helper.setText(html, true);
            mailSender.send(message);
            log.info("HTML-лист про книгу '{}' успішно надіслано на {}", book.title(), recipient);
        } catch (Exception e) {
            log.error("Не вдалося надіслати лист про книгу '{}'", book.title(), e);
            throw new IllegalStateException("Email sending failed", e);
        }
    }
}
