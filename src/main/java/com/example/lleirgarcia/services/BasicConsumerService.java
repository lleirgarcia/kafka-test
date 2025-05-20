package com.example.lleirgarcia.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Component
public class BasicConsumerService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final List<String> messages = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "${spring.kafka.topic.demo}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        messages.add(message);
        redisTemplate.opsForList().rightPush("kafka:messages", message);  // persist
        System.out.println("✅ Received and saved: " + message);
    }

    public List<String> getMessagesFromRedis() {
        return redisTemplate.opsForList().range("kafka:messages", 0, -1);
    }

}
