package tech.orbfin.api.productsservices.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Setter
@Getter
@Component
public class ResponseProviderRequest {
    String successMessage;
    String cautionMessage;
    String errorMessage;
    Integer statusCode;
}