package tech.orbfin.api.productsservices.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "provider_address")
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

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    @JsonIgnore
    private Provider provider;

    public Boolean isEmpty() {
        if (
            (streetAddress == null || streetAddress.isEmpty()) &&
                    (city == null || city.isEmpty()) &&
                    (state == null || state.isEmpty()) &&
                    (zipcode == null || zipcode.isEmpty()) &&
                    (country == null || country.isEmpty())
        ) {
            return true;
        } else {
            return false;
        }
    }
}
