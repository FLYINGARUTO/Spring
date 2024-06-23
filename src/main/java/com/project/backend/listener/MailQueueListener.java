package com.project.backend.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {
    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;


    @RabbitHandler
    public void handleMail(Map<String,Object> data){
        String type= (String) data.get("type");
        Integer code=(Integer) data.get("code");
        String email =(String) data.get("email");
        SimpleMailMessage message=switch (type){
            case "register"->createMessage("欢迎注册，点击查看验证码",
                    "验证码为"+code+"，有效期为3分钟，为了您的隐私安全，请勿将验证码告诉他人。",
                    email);
            case "reset"->createMessage("重置密码申请",
                    "验证码为"+code+"，有效期为3分钟，为了您的隐私安全，请勿将验证码告诉他人。",
                    email);
            default -> null;
        };
        if(message==null)return;
        sender.send(message);
    }


    private SimpleMailMessage createMessage(String title,String content,String email){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(email);
        message.setText(content);
        message.setSubject(title);
        return message;
    }
}
