package com.example.config.batch;

import com.example.stock.dto.APIResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
public class StockItemReader implements ItemReader<APIResponseDTO> {
    private final WebClient webClient;

    @Value("${api.serviceKey}")
    private String serviceKey;

    private int pageNo = 0;
    private boolean isFinished = false;

    public StockItemReader() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        this.webClient = WebClient.builder().uriBuilderFactory(factory).build();
    }

    @Override
    public APIResponseDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("ApiItemReader.read 시작 pageNo : {}", pageNo);

        if(isFinished) {
            log.info("더 이상 읽을 데이터가 없습니다.");
            return null;
        }

        URI uri = getURI(++pageNo);

        APIResponseDTO dto = callAPI(webClient, uri);

        if(dto.getResponse().getBody().getItems().getItem().isEmpty()) {
            isFinished = true;
            return null;
        }

        log.info("ApiItemReader.read 끝 pageNo : {}", pageNo);
        return dto;
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
