package com.example.common.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {
    private String postCode;

    private String address;

    private String detailAddress;

    public Address() {

    }

    public Address(String postCode, String address, String detailAddress) {
        this.postCode = postCode;
        this.address = address;
        this.detailAddress = detailAddress;
    }
}
