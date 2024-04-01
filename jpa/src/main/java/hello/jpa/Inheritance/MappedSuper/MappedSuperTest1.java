package hello.jpa.Inheritance.MappedSuper;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MappedSuperTest1 extends TimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
