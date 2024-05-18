package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {
    //@KafkaListener(topics = "message", groupId = "test", containerFactory = "kafkaListener")
    public void receiveString(String message) {
        log.info("KafkaConsumerService.receiveString : {}", message);
    }
}
