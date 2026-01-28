package com.example.datajpa.projection;

public interface UsernameOnly {
    //조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회(Projection)
    String getUsername();
}
