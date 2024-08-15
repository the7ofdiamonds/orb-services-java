package tech.orbfin.api.productsservices.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

import org.springframework.stereotype.Component;

import java.util.List;

import tech.orbfin.api.productsservices.model.Service;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Setter
@Getter
@Component
public class ResponseServiceRequest {
    String successMessage;
    String errorMessage;
    Integer statusCode;
}
