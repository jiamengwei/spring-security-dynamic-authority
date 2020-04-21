package org.dynamic.demo.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;

public class EmailService implements ApplicationEventPublisherAware {

    private List<String> blackList;
    private ApplicationEventPublisher applicationEventPublisher;

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }


    public void sendEmail(String address, String content) {
        if (blackList.contains(address)) {
            applicationEventPublisher.publishEvent(new BlackListEvent(this, address, content));
            return;
        }
        // send email...
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}