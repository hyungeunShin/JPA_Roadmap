package com.example.jpa.ProxyAndManageRelation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Commit
@Transactional
@SpringBootTest
class ProxyAndManageRelationTest {
    @PersistenceUnit
    EntityManagerFactory emf;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("프록시")
    void proxy() {
        Parent parent = new Parent();
        parent.setName("가나다");
        em.persist(parent);

        em.flush();
        em.clear();

        //select 쿼리 나감
        //em.find(Parent.class, parent.getId());

        log.info("==========");
        //select 쿼리 안나감
        Parent findParent = em.getReference(Parent.class, parent.getId());  //proxy 객체 생성
        log.info("==========");

        //지금 시점에 select 쿼리 나감
        //target 초기화 후 프록시 객체를 통해서 실제 엔티티에 접근(target.getName() 호출)
        log.info("member.name : {}", findParent.getName());

        //class com.example.jpa.ProxyAndManageRelation.Parent$HibernateProxy$DyaWY4se
        log.info("{}", findParent.getClass());

        log.info("isLoaded : {}", emf.getPersistenceUnitUtil().isLoaded(findParent));
    }

    @Test
    @DisplayName("영속성 전이")
    void cascade() {
        Child child1 = new Child();
        Child child2 = new Child();

        Parent parent = new Parent();
        parent.addChild(child1);
        parent.addChild(child2);

        //em.persist(child1);
        //em.persist(child2);
        em.persist(parent);

        em.flush();
        em.clear();

        Parent p = em.find(Parent.class, parent.getId());
        p.getChildren().remove(0);

        //delete from Child where id=?
    }

    @Test
    @DisplayName("지연 로딩")
    void lazyLoading() {
        Parent parent = new Parent();
        parent.setName("가나다");
        em.persist(parent);

        Child child = new Child();
        child.setName("팀");
        child.setParent(parent);
        em.persist(child);

        em.flush();
        em.clear();

        //parent 와 조인 없이 child 만 조회
        Child c = em.find(Child.class, child.getId());

        Parent p = c.getParent();
        //class com.example.jpa.ProxyAndManageRelation.Parent$HibernateProxy$DyaWY4se
        log.info("p : {}", p.getClass());

        //parent 의 select 쿼리 나감
        Hibernate.initialize(p);
    }
}
