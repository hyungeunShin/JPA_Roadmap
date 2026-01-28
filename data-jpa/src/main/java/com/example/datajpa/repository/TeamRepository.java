package com.example.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.datajpa.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
