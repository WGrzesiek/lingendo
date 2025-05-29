package com.learnwords.vocabularyreadservice.configuration;

import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.VocabularyDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaListenerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    private <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> type) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(type);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> createKafkaListenerFactory(
            ConsumerFactory<String, T> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, SentenceDto> sentenceConsumerFactory() {
        return createConsumerFactory(SentenceDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SentenceDto> sentenceKafkaListenerFactory() {
        return createKafkaListenerFactory(sentenceConsumerFactory());
    }

    @Bean
    public ConsumerFactory<String, VocabularyDto> vocabularyConsumerFactory() {
        return createConsumerFactory(VocabularyDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VocabularyDto> vocabularyKafkaListenerFactory() {
        return createKafkaListenerFactory(vocabularyConsumerFactory());
    }
}