package com.example.attachfile.repository;

import com.example.attachfile.domain.AttachFile;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AttachFileRepository {
    private final EntityManager em;

    public AttachFileRepository(EntityManager em) {
        this.em = em;
    }

    public Optional<AttachFile> findById(Long id) {
        return em.createQuery("select f from AttachFile f where f.id = :id", AttachFile.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }
}
