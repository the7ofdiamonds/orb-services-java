package tech.orbfin.api.productsservices.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import tech.orbfin.api.productsservices.model.request.RequestProviders;
import tech.orbfin.api.productsservices.model.request.RequestProvider;

@Configuration
public class ConfigKafka {
    @Value("${spring.kafka.hostURL}")
    private String hostURL;

    @Bean
    public AdminClient createAdminClient() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, hostURL);

        return AdminClient.create(properties);
    }

    @Bean
    public ProducerFactory<String, RequestProviders> requestProvidersProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hostURL);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "RequestProviders:tech.orbfin.api.productsservices.model.request.RequestProviders");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, RequestProviders> requestProvidersTemplate() {
        return new KafkaTemplate<>(requestProvidersProducerFactory());
    }

    @Bean
    public ProducerFactory<String, RequestProvider> requestProviderProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hostURL);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "RequestProvider:tech.orbfin.api.productsservices.model.request.RequestProvider");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, RequestProvider> requestProviderTemplate() {
        return new KafkaTemplate<>(requestProviderProducerFactory());
    }

    @Bean
    public ProducerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hostURL);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public NewTopic provider() {
        return TopicBuilder
                .name(ConfigKafkaTopics.PROVIDER_REQUEST)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notary() {
        return TopicBuilder
                .name(ConfigKafkaTopics.NOTARY_REQUEST)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic realEstateAppraisal() {
        return TopicBuilder
                .name(ConfigKafkaTopics.REAL_ESTATE_APPRAISAL_REQUEST)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
