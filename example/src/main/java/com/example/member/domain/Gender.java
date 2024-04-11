package com.example.member.domain;

public enum Gender {
    MALE("남자"), FEMALE("여자");

    private final String description;

    private Gender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
