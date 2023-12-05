package com.frontlinehomes.save2buy.data.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDetails {
    protected String template;
    protected String  subject;
    protected String to;
    protected String from;
}
