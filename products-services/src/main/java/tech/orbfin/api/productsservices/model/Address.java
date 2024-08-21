package tech.orbfin.api.productsservices.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "service_address")
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

    @OneToOne
    @JoinColumn(name = "provider_id", referencedColumnName = "id")
    private Provider provider;

    public Boolean isEmpty() {
        if (
            streetAddress == "" || streetAddress.isEmpty() &&
            city == "" || city.isEmpty() &&
            state == "" || state.isEmpty() &&
            zipcode == "" || zipcode.isEmpty() &&
            country == "" || country.isEmpty()
        ) {
            return true;
        } else {
            return false;
        }
    }
}
