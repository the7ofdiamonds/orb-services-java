package tech.orbfin.api.productsservices.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;

import lombok.*;

import java.util.List;

import tech.orbfin.api.productsservices.model.Email;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Entity
@Table(name = "providers")
public class Provider {
    @Id
    Long id;
    String created;
    String updated;
    String name;
    String bio;
    String logo;
    String url;
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    @jakarta.validation.constraints.Email(message = "Email should be valid")
    private List<Email> emails;
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Service> services;
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Address> address;
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Coordinates> coordinates;
}
