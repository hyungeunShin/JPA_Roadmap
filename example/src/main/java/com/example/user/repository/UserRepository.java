package com.example.user.repository;

import com.example.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager em;

    public Long save(User user) {
        em.persist(user);
        return user.getId();
    }

    public Optional<User> findById(Long id) {
        return em.createQuery("select u from User u left join fetch u.profile where u.id = :id", User.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public Optional<User> findByUsername(String username) {
        return em.createQuery("select u from User u left join fetch u.profile where u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    public Optional<User> findByNameAndPhone(String name, String phone) {
        return em.createQuery("select u from User u where u.name = :name and u.phone = :phone", User.class)
                 .setParameter("name", name)
                 .setParameter("phone", phone)
                 .getResultStream()
                 .findFirst();
    }

    public Optional<User> findByUsernameAndNameAndPhone(String username, String name, String phone) {
        return em.createQuery("select u from User u where u.username = :username and u.name = :name and u.phone = :phone", User.class)
                .setParameter("username", username)
                .setParameter("name", name)
                .setParameter("phone", phone)
                .getResultStream()
                .findFirst();
    }
}
