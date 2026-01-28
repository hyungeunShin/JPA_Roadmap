package com.example.datajpa.projection;

public record UsernameOnlyDTO(String username) {
    //생성자의 파라미터 이름으로 매칭

    @Override
    public String username() {
        return username;
    }

    @Override
    public String toString() {
        return "UsernameOnlyDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}
