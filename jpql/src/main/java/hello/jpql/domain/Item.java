package hello.jpql.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
@ToString
public class Item {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private int price;
}
