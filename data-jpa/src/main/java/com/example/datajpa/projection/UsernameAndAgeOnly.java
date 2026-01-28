package com.example.datajpa.projection;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameAndAgeOnly {
    //다음과 같이 스프링의 SpEL 문법도 지원
    //단! 이렇게 SpEL 문법을 사용하면 DB 에서 엔티티 필드를 다 조회해온 다음에 계산한다. 따라서 JPQL SELECT 절 최적화가 안된다.
    @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
    String getUsernameAndAge();
}
