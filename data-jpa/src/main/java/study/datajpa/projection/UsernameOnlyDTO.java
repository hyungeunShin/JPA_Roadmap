package study.datajpa.projection;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UsernameOnlyDTO {
    private final String username;

    //생성자의 파라미터 이름으로 매칭
    public UsernameOnlyDTO(String username) {
        this.username = username;
    }
}
