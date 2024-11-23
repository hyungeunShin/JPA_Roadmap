package com.example.attachfile.repository;

import com.example.attachfile.domain.AttachFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachFileJpaRepository extends JpaRepository<AttachFile, Long> {

}
