package com.example.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KafkaTest {
    @Autowired
    KafkaProducerService producerService;

    @Test
    void produce() {
        producerService.sendString("안녕하세요!!!");
    }
}
