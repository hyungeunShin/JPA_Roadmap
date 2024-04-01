package hello.jpa.basic;

public class JpaPersistence {
    /*
    Entity 의 상태
        • 비영속 : 영속성 컨텍스트와 전혀 관련이 없는 상태

        • 영속 : 영속성 컨텍스트가 관리하고 있는 상태
            - em.persist(entity) 를 통해 영속성 컨테스트에 저장하거나 em.find(entity.class, @Id) 를 통해 DB 에서 엔티티를 조회

        • 준영속 : 영속성 컨텍스트의 관리를 더 이상 받지 않는 상태(영속성 컨텍스트가 제공하는 기능을 사용하지 못한다.)
            - em.detach(entity) : 특정 엔티티만 준영속 상태로 전환
            - em.clear() : 영속성 컨텍스트를 완전히 초기화
            - em.close() : 영속성 컨텍스트를 종료

        • 삭제 : 삭제된 상태
            - em.remove(entity)

    영속성 컨텍스트가 제공하는 기능
        - 1차 캐시
        - 동일성 보장
        - 트랜잭션을 지원하는 쓰기 지연
        - 변경 감지
        - 지연 로딩
    */
}
