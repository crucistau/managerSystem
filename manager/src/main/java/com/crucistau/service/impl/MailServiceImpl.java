package com.crucistau.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.crucistau.service.IMailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MailServiceImpl implements IMailService {
    @Resource
    private JavaMailSenderImpl mailSender;

    public void sendMail(String mail, String code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("重置密码验证码");
        message.setText("您的验证码为："+ code);
        message.setTo(mail);
        message.setFrom("1350932647@qq.com");
        mailSender.send(message);
    }
}
