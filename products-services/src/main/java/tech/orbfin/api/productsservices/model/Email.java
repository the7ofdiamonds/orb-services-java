package tech.orbfin.api.productsservices.model;

import jakarta.persistence.*;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
@Table(name = "provider_email")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.validation.constraints.Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
}
