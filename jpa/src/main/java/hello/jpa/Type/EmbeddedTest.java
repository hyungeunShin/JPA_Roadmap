package hello.jpa.Type;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EmbeddedTest {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @Embedded
    private Period period;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "work_startDate"))
            , @AttributeOverride(name = "endDate", column = @Column(name = "work_endDate"))
    })
    private Period workPeriod;
}
