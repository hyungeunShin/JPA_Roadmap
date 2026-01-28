package com.example.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.dto.MemberTeamDTO;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDTO> search(MemberSearchCondition condition);

    Page<MemberTeamDTO> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

    Page<MemberTeamDTO> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
