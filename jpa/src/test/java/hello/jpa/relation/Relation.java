package hello.jpa.relation;

public class Relation {
    /*
    객체의 양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단뱡향 관계 2개다.
    객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.
        • 회원 -> 팀 연관관계 1개(단방향) : Team team
        • 팀 -> 회원 연관관계 1개(단방향) : List<Member> members

    하나의 회원이 팀을 변경하고 싶으면 Member 의 Team 을 변경해야 하는가? Team 의 List<Member> members 를 변경해야 하는가?
    누구를 주인으로 잡아야 하는가?
        ★★★ 외래 키가 있는 있는 곳을 주인으로 정해라 ★★★
        여기서는 Member.team 이 연관관계의 주인

    연관관계의 주인(Owner) - 양방향 매핑 규칙
        • 객체의 두 관계 중 하나를 연관관계의 주인으로 지정
        • 연관관계의 주인만이 외래 키를 관리(등록, 수정)
        • 주인이 아닌쪽은 읽기만 가능
        • 주인은 mappedBy 속성 사용X
        • 주인이 아니면 mappedBy 속성으로 주인 지정

    양방향 연관관계 주의
        • 순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하자
        • 연관관계 편의 메소드를 생성하자
        • 양방향 매핑시에 무한 루프를 조심하자
            - 예: toString(), lombok, JSON 생성 라이브러리

    양방향 매핑 정리
        • 단방향 매핑만으로도 이미 연관관계 매핑은 완료
        • 양방향 매핑은 반대 방향으로 조회(객체 그래프 탐색) 기능이 추가된 것 뿐
        • JPQL 에서 역방향으로 탐색할 일이 많음
        • 단방향 매핑을 잘 하고 양방향은 필요할 때 추가해도 됨(테이블에 영향을 주지 않음) ==> 순수한 객체 관계를 고려하면 항상 양쪽 다 값을 입력해야 한다.
        • 비즈니스 로직을 기준으로 연관관계의 주인을 선택하면 안됨 ==> 연관관계의 주인은 외래 키의 위치를 기준으로 정해야함
    */
}
