package com.example.datajpa.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import com.example.datajpa.entity.Member;
import com.example.datajpa.entity.Team;

public class MemberSpec {
    public static Specification<Member> teamName(final String teamName) {
        return (root, query, builder) -> {
            if(!StringUtils.hasText(teamName)) {
                return null;
            }

            Join<Member, Team> t = root.join("team", JoinType.INNER);
            return builder.equal(t.get("name"), teamName);
        };
    }

    public static Specification<Member> username(final String username) {
        return (root, query, builder) -> builder.equal(root.get("username"), username);
    }
}
