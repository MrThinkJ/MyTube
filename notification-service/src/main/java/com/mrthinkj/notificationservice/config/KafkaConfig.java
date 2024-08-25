package com.mrthinkj.notificationservice.config;

import com.mrthinkj.core.entity.NotificationEvent;
import com.mrthinkj.core.exception.NotRetryableException;
import com.mrthinkj.core.exception.RetryableException;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.consumer.isolation-level}")
    private String isolationLevel;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${consumer.trusted.packages}")
    private String trustedPackage;

    @Bean
    Map<String, Object> consumerConfigs(){
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configs.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
        configs.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);
        return configs;
    }

    @Bean
    ConsumerFactory<String, Object> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            KafkaTemplate<String, Object> kafkaTemplate
    ){
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(5000, 3)
        );
        errorHandler.addNotRetryableExceptions(NotRetryableException.class);
        errorHandler.addRetryableExceptions(RetryableException.class);

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic notificationEventTopic(){
        return TopicBuilder.name("notification-events-topic")
                .replicas(1)
                .partitions(3)
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }
}
