package tech.orbfin.api.productsservices.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

import org.springframework.stereotype.Component;

import java.util.List;

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
public class RequestProviders {
    private Long id;
    private String type;
    private Long serviceID;
    private List<Long> providers;
    private String date;
    private String time;
    private Double price;
    private Address address;
    private Coordinates coordinates;
}
