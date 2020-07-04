package com.sda.utils;

import com.sda.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailUtil {

@Autowired
    private JavaMailSender javaMailSender;

    public void sendNotificationNewEvent(Event event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUser().getEmail(), "jankowalskidemo534@gmail.com");
        message.setSubject("you created new event "+event.getTitle());
        String text = "Hello!\n\n"+
                "\nCongratulations "+event.getUser().getFirstName()+" "+event.getUser().getLastName()+" you created new event.\n\n"+
                "Title: "+event.getTitle()+"\n\n"+
                "Start: "+ event.getStartDate()+
                "\nEnd" + event.getEndDate()+
                "\n\nCity: "+event.getCity()+
                "\n\nDescription:\n"+
                event.getDescription()+
                "\n\nPublisher: "+event.getUser().getFirstName()+" "+event.getUser().getLastName();
        message.setText(text);

        Thread thread = new Thread(){
            public void run(){
                javaMailSender.send(message);
            }
        };
        thread.start();
    }
}
