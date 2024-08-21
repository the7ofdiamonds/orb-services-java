package tech.orbfin.api.productsservices.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Setter
@Getter
@Component
@Entity
@Table(name = "providers")
public class Provider {
    @Id
    String id;
    String created;
    String updated;
    String name;
    String bio;
    String logo;
    String url;
    @OneToMany
    @JoinColumn(name = "services", referencedColumnName = "id")
    List<Service> services;
    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    Address address;
    @OneToOne
    @JoinColumn(name = "coordinates_id", referencedColumnName = "id")
    Coordinates coordinates;
}
