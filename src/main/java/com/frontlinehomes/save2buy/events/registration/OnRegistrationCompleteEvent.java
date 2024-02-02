package com.frontlinehomes.save2buy.events.registration;


import com.frontlinehomes.save2buy.data.users.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Setter
@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;

    private User user;
    private String template;

    public OnRegistrationCompleteEvent(User user, String appUrl, String template) {
        super(user);
        this.user = user;
        this.appUrl=appUrl;
        this.template= template;
    }

    // standard getters and setters
}