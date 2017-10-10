package com.hc9.common.util;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.hc9.commons.log.LOG;

/**
 * 邮件发送
 * @author frank
 *
 */
public class EmailUtils{
	public static final String contentType = "text/html;charset=UTF-8";
    public EmailUtils()
    {
    }
    /**
     * 
     * @param session 
     * @param date 发送日期
     * @param address_from 发出地址
     * @param subject 
     * @param context 邮件内容
     * @param type 
     * @param bodyParts 邮件内容
     * @param address_tos 目标邮箱
     */
    public static void sendEmail(Session session, Date date, String address_from, String subject, String context, String type, BodyPart bodyParts[], Address address_tos[])
    {
        MimeMessage message = new MimeMessage(session);
        try
        {
            message.setSubject(subject);
            message.setSentDate(date);
            message.setFrom(new InternetAddress(address_from));
            message.addRecipients(javax.mail.Message.RecipientType.TO, address_tos);
            if(bodyParts == null || bodyParts.length == 0)
            {
                message.setContent(context, type);
            } else
            {
                Multipart multipart = new MimeMultipart();
                BodyPart content = new MimeBodyPart();
                content.setContent(context, type);
                multipart.addBodyPart(content);
                BodyPart abodypart[];
                int j = (abodypart = bodyParts).length;
                for(int i = 0; i < j; i++)
                {
                    BodyPart bodyPart = abodypart[i];
                    multipart.addBodyPart(bodyPart);
                }

                message.setContent(multipart);
            }
            Transport.send(message);
        }
        catch(AuthenticationFailedException e)
        {
        	LOG.error("邮件帐号密码验证失败"+e.getMessage());
        }
        catch(MessagingException e)
        {
        	
        	LOG.error("邮件内容出错"+e.getMessage());
        }
        catch(Exception e)
        {
        	LOG.error(e.getMessage());
        }
    }
    
    public static void sendEmail(Session session, Date date, String address_from, String subject, String context, String type, Collection names, Collection filePathNames, String address[]){
        BodyPart bodyParts[] = null;
        if(names != null && filePathNames != null && names.size() > 0 && filePathNames.size() > 0)
        {
            bodyParts = new MimeBodyPart[names.size()];
            int i = 0;
            Iterator iterator_names = names.iterator();
            Iterator iterator_filePathNames = filePathNames.iterator();
            try
            {
                while(iterator_names.hasNext() && iterator_filePathNames.hasNext()) 
                {
                    bodyParts[i] = new MimeBodyPart();
                    bodyParts[i].setFileName((String)iterator_names.next());
                    bodyParts[i].setDataHandler(new DataHandler(new FileDataSource(new File((String)iterator_filePathNames.next()))));
                }
            }
            catch(Exception e)
            {
                LOG.error("文件路径错误，指定文件未找到！", e);
            }
        }
        Address address_tos[] = new Address[address.length];
        try
        {
            for(int i = 0; i < address.length; i++)
                address_tos[i] = new InternetAddress(address[i]);

        }
        catch(AddressException e)
        {
            LOG.error("邮箱地址不存在！", e);
        }
        sendEmail(session, date, address_from, subject, context, type, bodyParts, address_tos);
    }

}