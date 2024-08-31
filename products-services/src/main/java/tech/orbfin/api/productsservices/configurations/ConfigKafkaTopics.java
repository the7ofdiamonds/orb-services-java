package tech.orbfin.api.productsservices.configurations;

import org.springframework.stereotype.Component;

@Component
public class ConfigKafkaTopics {
    public final static String NOTARY_REQUEST = "notary-requested";
    public final static String REAL_ESTATE_APPRAISAL_REQUEST = "real-estate-appraisal-requested";
    public final static String PROVIDER_REQUEST = "provider-requested";

    public String getTopicByType(String type) {
        return switch (type) {
            case "notary" -> NOTARY_REQUEST;
            case "realestateappraiser" -> REAL_ESTATE_APPRAISAL_REQUEST;
            default -> PROVIDER_REQUEST;
        };
    }
}
