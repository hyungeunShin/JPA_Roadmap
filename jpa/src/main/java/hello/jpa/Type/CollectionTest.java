package hello.jpa.Type;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class CollectionTest {
    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection
    @CollectionTable(name = "favorite_food"
            , joinColumns = @JoinColumn(name = "id")
    )
    @Column(name = "food_name")
    private Set<String> favoriteFoods = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "address"
            , joinColumns = @JoinColumn(name = "id")
    )
    private List<Address> addressHistory = new ArrayList<>();
}
