package com.example.stock.repository;

import com.example.stock.domain.Stock;
import com.example.stock.domain.StockPK;
import org.springframework.data.repository.CrudRepository;

public interface StockJpaRepository extends CrudRepository<Stock, StockPK> {
}
