package com.example.lleirgarcia;

import com.example.lleirgarcia.services.BasicConsumerService;
import com.example.lleirgarcia.services.BasicProducerService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class KafkaIntegrationTest {

    @Container
    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.9.1")
                    .asCompatibleSubstituteFor("apache/kafka")
    );

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.0")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private BasicProducerService producer;

    @Autowired
    private BasicConsumerService consumer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Test
    public void testProducerAndConsumerWithRealKafka() {
        String testMessage = "💬 Hello, message to test integrations!";
        producer.sendMessage(testMessage);

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(200))
                .until(() -> consumer.getMessages().contains(testMessage));

        assertTrue(consumer.getMessages().contains(testMessage));
    }

    @BeforeEach
    public void clearRedis() {
        redisTemplate.delete("kafka:messages");
    }

    @Test
    public void testKafkaToRedisIntegration() {
        String message = "🧪 Message to kafka and redis";
        producer.sendMessage(message);

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until(() -> consumer.getMessagesFromRedis().contains(message));

        assertTrue(consumer.getMessagesFromRedis().contains(message));
    }

    @Test
    public void testMultipleMessagesStoredInRedis() {
        List<String> messages = List.of("msg1", "msg2", "msg3");
        messages.forEach(producer::sendMessage);

        Awaitility.await()
                .atMost(1, TimeUnit.SECONDS)
                .until(() -> consumer.getMessagesFromRedis().size() >= 3);

        List<String> stored = consumer.getMessagesFromRedis();
        assertTrue(stored.containsAll(messages));
    }

    @Test
    public void testMessageLost() {
        String lostMessage = "⚠️ message lost";

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> !consumer.getMessagesFromRedis().contains(lostMessage));

        assertFalse(consumer.getMessagesFromRedis().contains(lostMessage));
    }

}
