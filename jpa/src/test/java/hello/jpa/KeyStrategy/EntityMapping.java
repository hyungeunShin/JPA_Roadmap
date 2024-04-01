package hello.jpa.KeyStrategy;

public class EntityMapping {
    /*
    @Entity
        • @Entity 가 붙은 클래스는 JPA 가 관리, 엔티티라 한다.
        • JPA 를 사용해서 테이블과 매핑할 클래스는 @Entity 필수
        • 주의
            • 기본 생성자 필수(파라미터가 없는 public 또는 protected 생성자)
            • final 클래스, enum, interface, inner 클래스 사용X
            • 저장할 필드에 final 사용 X

    DDL 생성 기능
        src/main/resources/META-INF/persistence.xml 참고

        • 제약조건 추가: 회원 이름은 필수, 10자 초과X
            - @Column(nullable = false, length = 10)
        • 유니크 제약조건 추가
            - @Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"})})
        • DDL 생성 기능은 DDL 을 자동 생성할 때만 사용되고 JPA 의 실행 로직에는 영향을 주지 않는다.

    기본 키 매핑
        • 직접 할당: @Id만 사용
        • 자동생성 : @Id + @GeneratedValue
            - AUTO
                • 방언에 따라 자동 지정
                • 기본값

            - IDENTITY
                • 기본 키 생성을 데이터베이스에 위임
                • JPA 는 보통 트랜잭션 커밋 시점에 INSERT SQL 실행하지만 IDENTITY 전략을 사용하면 em.persist() 시점에 즉시 INSERT SQL 실행하고 DB 에서 식별자를 조회한다.
                • AUTO_INCREMENT 는 데이터베이스에 INSERT SQL 을 실행한 이후에 ID 값을 알 수 있음

                • 주의점 : 엔티티가 영속 상태가 되려면 식별자가 반드시 필요하다.
                  그런데 IDENTITY 식별자 생성 전략은 엔티티를 데이터베이스에 저장해야 식별자를 구할 수 있으므로 em.persist()를 호출하는 즉시 INSERT SQL 이 데이터베이스에 전달된다.
                  따라서 이 전략은 트랜잭션을 지원하는 쓰기 지연이 동작하지 않는다.

            - SEQUENCE
                • 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(예: 오라클 시퀀스)
                • 직접 시퀀스 오브젝트를 설정하기 위해서는 @SequenceGenerator 필요
                    - name : 식별자 생성기 이름(필수)
                    - sequenceName : 데이터베이스에 등록되어 있는 시퀀스 이름(ddl-auto 를 통해 생성 가능)
                    - initialValue : DDL 생성 시에만 사용됨, 시퀀스 DDL 을 생성할 때 처음 시작하는 수를 지정한다.(기본값 : 1)
                    - allocationSize : 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨), 데이터베이스 시퀀스 값이 1씩 증가하도록 설정되어 있으면 이 값을 반드시 1로 설정해야 한다.(기본값 : 50)
                    - catalog, schema : 데이터베이스 catalog, schema 이름

                • em.persist()를 호출할 때 먼저 데이터베이스 시퀀스를 사용해서 식별자를 조회한다.
                  그리고 조회한 식별자를 엔티티에 할당한 후에 엔티티를 영속성 컨텍스트에 저장합니다.
                  이후 트랜잭션을 커밋해서 플러시가 일어나면 엔티티를 데이터베이스에 저장합니다.
                  
            - TABLE: 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
                • 모든 DB 에서 사용
                • @TableGenerator 필요

    필드와 컬럼 매핑
        • @Column 컬럼 매핑
            - name : 필드와 매핑할 테이블의 컬럼 이름(기본값 : 객체의 필드 이름)
            - insertable, updatable : 등록, 변경 가능 여부(기본값 : TRUE)
            - nullable(DDL) : null 값의 허용 여부를 설정한다. false 로 설정하면 DDL 생성 시에 not null 제약조건이 붙는다.
            - unique(DDL) : @Table 의 uniqueConstraints 와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용한다.
            - columnDefinition(DDL) : 데이터베이스 컬럼 정보를 직접 줄 수 있다.(ex) varchar(100) default ‘EMPTY')
            - length(DDL) : 문자 길이 제약조건, String 타입에만 사용한다.(기본값 : 255)
            - precision, scale(DDL) : BigDecimal 타입에서 사용한다.(BigInteger 도 사용할 수 있다.)
              precision 은 소수점을 포함한 전체 자릿수를, scale 은 소수의 자릿수다. 참고로 double, float 타입에는 적용되지 않는다. 아주 큰 숫자나 정밀한 소수를 다루어야 할 때만 사용한다.
              (기본값 : precision=19, scale=2)

        @Temporal : 날짜 타입(java.util.Date, java.util.Calendar)을 매핑할 때 사용
            - TemporalType.DATE: 날짜, 데이터베이스 date 타입과 매핑(예: 2013–10–11)
            - TemporalType.TIME: 시간, 데이터베이스 time 타입과 매핑(예: 11:11:11)
            - TemporalType.TIMESTAMP: 날짜와 시간, 데이터베이스 timestamp 타입과 매핑(예: 2013–10–11 11:11:11)
            - 참고: LocalDate, LocalDateTime 을 사용할 때는 생략 가능

        @Enumerated : enum 타입 매핑
            - EnumType.ORDINAL: enum 순서를 데이터베이스에 저장
            - EnumType.STRING: enum 이름을 데이터베이스에 저장
            - 기본값은 EnumType.ORDINAL 이지만 ORDINAL 을 사용하지 말자

        @Lob : 데이터베이스 BLOB, CLOB 타입과 매핑
            - @Lob 에는 지정할 수 있는 속성이 없다.
            - 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
                • CLOB: String, char[], java.sql.CLOB
                • BLOB: byte[], java.sql. BLOB

        @Transient 특정 필드를 컬럼에 매핑하지 않음(매핑 무시)
            - 필드 매핑X
            - 데이터베이스에 저장X, 조회X
            - 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용
    */
}
