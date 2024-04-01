package hello.jpa.Type;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Embeddable
@Getter
@Setter
public class Period {
    private Date startDate;

    private Date endDate;

    public boolean isWork(Date start, Date end) {
        //로직
        return true;
    }
}
