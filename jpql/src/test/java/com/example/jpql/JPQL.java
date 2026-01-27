package com.example.jpql;

public class JPQL {
    /*
    JPQL
        • JPA 를 사용하면 엔티티 객체를 중심으로 개발
        • 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
        • 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
        • 애플리케이션이 필요한 데이터만 DB 에서 불러오려면 결국 검색 조건이 포함된 SQL 이 필요
        • JPA 는 SQL 을 추상화한 JPQL 이라는 객체 지향 쿼리 언어 제공(특정 데이터베이스 SQL 의존X)
        • SQL 과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
        • JPQL 은 엔티티 객체를 대상으로 쿼리
        • SQL 은 데이터베이스 테이블을 대상으로 쿼리

    JPQL 문법
        • 예) select m from Member as m where m.age > 18
        • 엔티티와 속성은 대소문자 구분O (Member, age)
        • JPQL 키워드는 대소문자 구분X (SELECT, FROM, where)
        • 엔티티 이름 사용, 테이블 이름이 아님(Member)
        • 별칭은 필수(m) (as는 생략가능)

    TypeQuery, Query
        • TypeQuery: 반환 타입이 명확할 때 사용
        • Query: 반환 타입이 명확하지 않을 때 사용

    결과 조회 API
        • query.getResultList(): 결과가 하나 이상일 때, 리스트 반환
            • 결과가 없으면 빈 리스트 반환
        • query.getSingleResult(): 결과가 정확히 하나, 단일 객체 반환
            • 결과가 없으면: javax.persistence.NoResultException
            • 둘 이상이면: javax.persistence.NonUniqueResultException
    */
}
