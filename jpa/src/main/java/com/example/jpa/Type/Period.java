package com.example.jpa.Type;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Embeddable
public class Period {
    private Date startDate;

    private Date endDate;

    public boolean isWork(Date start, Date end) {
        //로직
        return true;
    }
}
