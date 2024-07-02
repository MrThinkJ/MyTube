package com.mrthinkj.videoservice.config;

import com.mrthinkj.core.entity.VideoEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Value("${spring.kafka.producer.bootstrap-servers}")
    String bootstrapServer;
    @Value("${spring.kafka.producer.acks}")
    String acks;
    @Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
    Integer deliveryTimeout;
    @Value("${spring.kafka.producer.properties.linger.ms}")
    Integer linger;
    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    Integer requestTimeout;
    @Value("${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    Integer maxInFlightRequests;
    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    Boolean idempotence;
    @Bean
    Map<String, Object> kafkaConfigs(){
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configs.put(ProducerConfig.ACKS_CONFIG, acks);
        configs.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        configs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        configs.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        configs.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequests);
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
        return configs;
    }

    @Bean
    KafkaTemplate<String, VideoEvent> kafkaTemplate(){
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaConfigs()));
    }

    @Bean
    public NewTopic videoEventTopic(){
        return TopicBuilder.name("video-events-topic")
                .replicas(3)
                .partitions(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean NewTopic videoResultEventTopic(){
        return TopicBuilder.name("video-result-events-topic")
                .replicas(3)
                .partitions(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }
}
