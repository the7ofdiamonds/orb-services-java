package tech.orbfin.api.productsservices.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ToString(exclude = {"emails", "services", "address", "coordinates"})
@Getter
@Setter
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
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @jakarta.validation.constraints.Email(message = "Email should be valid")
    @JsonManagedReference
    private List<Email> emails;
    @ElementCollection
    @CollectionTable(name = "services", joinColumns = @JoinColumn(name = "provider_id"))
    List<Long> services;
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Address> address;
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Coordinates> coordinates;
}
