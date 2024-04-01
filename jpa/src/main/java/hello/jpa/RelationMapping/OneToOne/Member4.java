package hello.jpa.RelationMapping.OneToOne;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member4 {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "locker_id")
    private Locker locker;
}
