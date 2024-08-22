package tech.orbfin.api.productsservices.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "provider_coordinates")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
}
