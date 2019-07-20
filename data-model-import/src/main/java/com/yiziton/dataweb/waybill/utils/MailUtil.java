package com.yiziton.dataweb.waybill.utils;

import com.sun.mail.util.MailSSLSocketFactory;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import sun.awt.AWTAccessor;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Component("mailUtil")
public class MailUtil {

    @Autowired
    public JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    public  String Sender;

    public static final String EXCEL_FORMAT = "application/vnd.ms-excel";

    public static MimeBodyPart byte2Multpart(byte[] bytes, String fileName, String format) throws MessagingException{
        DataSource dataSource = new ByteArrayDataSource(bytes,format);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(new DataHandler(dataSource));
        mimeBodyPart.setFileName(fileName);
        return mimeBodyPart;
    }


    /**
     * 邮件发送
     * @param subject
     * @param sendBody
     * @param tos
     * @throws Exception
     */
    public void sendEmail(String subject, Object sendBody, List<MimeBodyPart> mimeBodyParts, String... tos) throws Exception {
        MimeMessage message = null;
        message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(Sender);
        helper.setTo(tos);

        helper.setSubject(subject);
        helper.setText(sendBody.toString());
        //注意项目路径问题，自动补用项目路径
        for(MimeBodyPart mimeBodyPart : mimeBodyParts){
            //加入邮件
            helper.addAttachment(mimeBodyPart.getFileName(), mimeBodyPart.getDataHandler().getDataSource());
        }
        javaMailSender.send(message);
    }
}
