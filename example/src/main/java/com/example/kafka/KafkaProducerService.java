package com.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    @Value("${topic.name}")
    private String topic;

    private final KafkaTemplate<String, String> template;

    public void sendString(String s) {
        log.info("KafkaProducerService.sendString : {}", s);
        template.send(topic, s).thenAccept(result -> {
            RecordMetadata metadata = result.getRecordMetadata();
            log.info("metadata.offset : {}", metadata.offset());
            log.info("metadata.topic : {}", metadata.topic());
            log.info("metadata.partition : {}", metadata.partition());
        }).exceptionally(ex -> {
            log.info("KafkaProducerService.sendString 오류", ex);
            return null;
        });
    }
}
