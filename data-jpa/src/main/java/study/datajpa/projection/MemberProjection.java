package study.datajpa.projection;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberProjection {
    private final Long id;
    private final String username;
    private final int age;

    public MemberProjection(Long id, String username, int age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }
}
