package tech.orbfin.api.productsservices.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long addressId;

    @Column(name = "street_address")
    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;
    private String country;
}
