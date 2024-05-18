package com.example.stock.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class APIResponseDTO {
    private Response response;

    public boolean isSuccess() {
        return response != null && "00".equals(response.getHeader().getResultCode());
    }

    @Getter
    @Setter
    @ToString
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    @Setter
    @ToString
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @Setter
    @ToString
    public static class Body {
        private int numOfRows;
        private int pageNo;
        private int totalCount;
        private Items items;
    }

    @Getter
    @Setter
    @ToString
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @Setter
    @ToString
    public static class Item {
        private String basDt;
        private String srtnCd;
        private String isinCd;
        private String itmsNm;
        private String mrktCtg;
        private int clpr;
        private int vs;
        private Double fltRt;
        private int mkp;
        private int hipr;
        private int lopr;
        private Long trqu;
        private Long trPrc;
        private Long lstgStCnt;
        private Long mrktTotAmt;
    }
}
