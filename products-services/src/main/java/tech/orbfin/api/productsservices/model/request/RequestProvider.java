package tech.orbfin.api.productsservices.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

import org.springframework.stereotype.Component;

import tech.orbfin.api.productsservices.model.Address;
import tech.orbfin.api.productsservices.model.Coordinates;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Setter
@Getter
@Component
public class RequestProvider {
    @JsonProperty("provider_id")
    private Long providerID;

    @JsonProperty("service_id")
    private Long serviceID;

    @JsonProperty("property_id")
    private Long propertyID;

    @JsonProperty("business_id")
    private Long businessID;

    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("coordinates")
    private Coordinates coordinates;

    @JsonIgnore
    private String serviceType;

    @JsonIgnore
    private String subject;

    @JsonIgnore
    private String message;
}
