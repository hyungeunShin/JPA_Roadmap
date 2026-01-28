package com.example.datajpa.dto;

import lombok.Data;
import com.example.datajpa.entity.Member;

@Data
public class MemberDTO {
    private Long id;
    private String username;
    private String teamName;

    public MemberDTO(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDTO(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        if(member.getTeam() != null) {
            this.teamName = member.getTeam().getName();
        }
    }
}
