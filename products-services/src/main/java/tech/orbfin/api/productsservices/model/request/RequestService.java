package tech.orbfin.api.productsservices.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RequestService {
    @JsonProperty("service_id")
    private Long serviceID;

    @JsonProperty("type")
    private String type;

    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("coordinates")
    private Coordinates coordinates;

    @JsonProperty("property_id")
    private Long propertyID;

    @JsonProperty("business_id")
    private Long businessID;

    @JsonProperty("provider_id")
    private Long providerID;
}
