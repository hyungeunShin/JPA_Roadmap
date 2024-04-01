package hello.jpa.Inheritance.MappedSuper;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class TimeEntity {
    @Column(name = "insert_member")
    private String registerMember;

    private LocalDateTime registerDate;

    private LocalDateTime modifiedDate;
}
