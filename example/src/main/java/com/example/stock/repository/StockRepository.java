package com.example.stock.repository;

import com.example.stock.domain.MarketCategory;
import com.example.stock.domain.QStock;
import com.example.stock.domain.Stock;
import com.example.stock.dto.StockListDTO;
import com.example.util.PaginationInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class StockRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public StockRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public List<Stock> findAll(PaginationInfo<StockListDTO> paginationInfo) {
        String searchType = paginationInfo.getSearchType();
        String searchWord = paginationInfo.getSearchWord();

        QStock stock = QStock.stock;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(stock.stockPK.baseDate.eq(JPAExpressions.select(stock.stockPK.baseDate.max()).from(stock)));

        if("KOSPI".equals(searchType)) {
            builder.and(stock.marketCategory.eq(MarketCategory.KOSPI));
        } else if("KOSDAQ".equals(searchType)) {
            builder.and(stock.marketCategory.eq(MarketCategory.KOSDAQ));
        } else if("KONEX".equals(searchType)) {
            builder.and(stock.marketCategory.eq(MarketCategory.KONEX));
        }

        if(StringUtils.hasText(searchWord)) {
            builder.and(stock.itemName.likeIgnoreCase("%" + searchWord + "%"));
        }

        return query.select(stock)
                    .from(stock)
                    .where(builder)
                    .orderBy(stock.itemName.asc())
                    .limit(paginationInfo.getPageSize())
                    .offset(paginationInfo.getStartRow())
                    .fetch();
    }

    public Long findAllTotalCount(PaginationInfo<StockListDTO> paginationInfo) {
        String searchType = paginationInfo.getSearchType();
        String searchWord = paginationInfo.getSearchWord();

        QStock stock = QStock.stock;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(stock.stockPK.baseDate.eq(JPAExpressions.select(stock.stockPK.baseDate.max()).from(stock)));

        if("KOSPI".equals(searchType)) {
            builder.and(stock.marketCategory.eq(MarketCategory.KOSPI));
        } else if("KOSDAQ".equals(searchType)) {
            builder.and(stock.marketCategory.eq(MarketCategory.KOSDAQ));
        } else if("KONEX".equals(searchType)) {
            builder.and(stock.marketCategory.eq(MarketCategory.KONEX));
        }

        if(StringUtils.hasText(searchWord)) {
            builder.and(stock.itemName.likeIgnoreCase("%" + searchWord + "%"));
        }

        return query.select(stock.count())
                    .from(stock)
                    .where(builder)
                    .fetchOne();
    }

    public List<Stock> findByIsinCode(String isinCode) {
        return em.createQuery("select s from Stock s where s.stockPK.isinCode = :isinCode order by s.stockPK.baseDate", Stock.class)
                 .setParameter("isinCode", isinCode)
                 .getResultList();
    }
}
