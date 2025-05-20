package com.example.lleirgarcia;

import com.example.lleirgarcia.services.BasicConsumerService;
import com.example.lleirgarcia.services.BasicProducerService;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@EmbeddedKafka(topics = "${spring.kafka.topic.demo}", partitions = 1)
public class EmbeddedKafkaTest {
    @Autowired
    private BasicProducerService producer;

    @Autowired
    private BasicConsumerService consumer;

    @Test
    public void testKafkaMessageFlow() {
        String testMessage = "Test message";
        producer.sendMessage(testMessage);
    }

    @Test
    public void testMessageFlow() {
        String testMessage = "Hello this is a Kafka message!";
        producer.sendMessage(testMessage);

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> consumer.getMessages().contains(testMessage));

        assertTrue(consumer.getMessages().contains(testMessage));
    }
}
