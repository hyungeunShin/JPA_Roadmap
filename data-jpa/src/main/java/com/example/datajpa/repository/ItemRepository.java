package com.example.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.datajpa.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
