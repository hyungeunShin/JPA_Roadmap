package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import study.querydsl.entity.Member;

@Data
public class MemberTeamDTO {
    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    @QueryProjection
    public MemberTeamDTO(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public MemberTeamDTO(Member member) {
        this.memberId = member.getId();
        this.username = member.getUsername();
        this.age = member.getAge();
    }
}
