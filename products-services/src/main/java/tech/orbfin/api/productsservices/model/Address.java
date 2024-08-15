package tech.orbfin.api.productsservices.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
public class Address {
//    @Column(name = "street_address")
    private String streetAddress;

    private String city;
    private String state;
    private String zipcode;
    private String county;
}
