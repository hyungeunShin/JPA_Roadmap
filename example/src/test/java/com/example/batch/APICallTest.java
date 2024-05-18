package com.example.batch;

import com.example.stock.dto.APIResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
@SpringBootTest
class APICallTest {
    @Value("${api.serviceKey}")
    private String serviceKey;

    @Test
    void test() {
        URI uri = getURI(1);

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        WebClient webClient = WebClient.builder().uriBuilderFactory(factory).build();

        APIResponseDTO dto = callAPI(webClient, uri);

        log.info("{}", dto);
    }

    private URI getURI(int pageNo) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService")
                .path("/getStockPriceInfo")
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", 500)
                .queryParam("resultType", "json")
                .queryParam("basDt", getBasDt());

        return builder.build(true).toUri();
    }

    private APIResponseDTO callAPI(WebClient webClient, URI uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new RuntimeException("API 요청 실패")))
                .bodyToMono(APIResponseDTO.class)
                .block();
    }

    private String getBasDt() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return formatter.format(calendar.getTime());
    }
}
