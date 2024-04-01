package hello.jpa.RelationMapping.OneToMany;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Team2 {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany
    @JoinColumn(name = "team_id") //주석처리하면 테이블이 하나 더 생김
    private List<Member3> members = new ArrayList<>();
}

