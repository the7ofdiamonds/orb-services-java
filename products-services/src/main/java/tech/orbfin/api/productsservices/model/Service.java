package tech.orbfin.api.productsservices.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
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
@Table(name = "services")
public class Service {
    @Id
    String id;
    String created;
    String updated;
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

    @Column(name = "street_address")
    private String streetAddress;

    private String city;
    private String state;
    private String zipcode;
    private String county;

    Double latitude;
    Double longitude;
//    Coordinates coordinates;
}
