package tech.orbfin.api.productsservices.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Setter
@Getter
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class Location {
    @JsonProperty("address")
    @ManyToOne
    @JoinColumn(name = "AddressID")
    private Address address;

    @JsonProperty("coordinates")
    @ManyToOne
    @JoinColumn(name = "CoordinatesID")
    private Coordinates coordinates;
}
