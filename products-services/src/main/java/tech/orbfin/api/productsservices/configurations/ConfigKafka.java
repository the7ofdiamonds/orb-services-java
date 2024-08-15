package tech.orbfin.api.productsservices.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class ConfigKafka {
    @Bean
    public NewTopic notary() {
        return TopicBuilder
                .name(ConfigKafkaTopics.NOTARY_REQUEST)
                .build();
    }
}
