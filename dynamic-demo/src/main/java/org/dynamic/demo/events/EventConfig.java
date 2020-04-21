package org.dynamic.demo.events;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class EventConfig {

    @Bean
    public EmailService emailService(){
        EmailService service = new EmailService();
        List<String> blackList = new ArrayList<>();
        blackList.add("beijing");
        blackList.add("shanghai");
        service.setBlackList(blackList);

        return service;
    }

    @Bean
    public BlackListNotifier blackListNotifier(){
        BlackListNotifier notifier = new BlackListNotifier();
        notifier.setNotificationAddress("beijing");
        return notifier;
    }
}
