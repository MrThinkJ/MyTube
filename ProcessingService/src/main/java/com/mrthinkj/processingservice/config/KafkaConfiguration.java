package com.mrthinkj.processingservice.config;

import com.mrthinkj.core.entity.VideoEvent;
import com.mrthinkj.core.entity.VideoState;
import com.mrthinkj.core.exception.NotRetryableException;
import com.mrthinkj.core.exception.RetryableException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.kafka.consumer.isolation-level}")
    private String isolationLevel;
    @Value("${consumer.trusted.packages}")
    private String trustedPackage;
    @Value("${spring.kafka.bootstrap-servers}")
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
    ConsumerFactory<String, Object> consumerFactory(){
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configs.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configs.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory(ConsumerFactory<String, Object> consumerFactory,
                                                                             KafkaTemplate<String, Object> kafkaTemplate){
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(kafkaTemplate),
            new FixedBackOff(1000, 5)
        );
        errorHandler.addRetryableExceptions(RetryableException.class);
        errorHandler.addNotRetryableExceptions(NotRetryableException.class);

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    Map<String, Object> producerConfigs(){
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
    KafkaTemplate<String, Object> kafkaTemplate(){
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs()));
    }

    @Bean
    KafkaTemplate<String, VideoState> videoEventKafkaTemplate(){
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs()));
    }
}