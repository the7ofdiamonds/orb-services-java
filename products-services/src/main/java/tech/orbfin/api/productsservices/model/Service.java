package tech.orbfin.api.productsservices.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Entity
@Table(name = "services")
public class Service {
    @Id
    String id;
    String created;
    String updated;
    String type;
    String name;
    String content;
    String description;
    Double price;
    List<String> features;
    String onboarding_link;
    String icon;
    String button_icon;
    String action_word;
    String price_id;
    String url;
    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
}