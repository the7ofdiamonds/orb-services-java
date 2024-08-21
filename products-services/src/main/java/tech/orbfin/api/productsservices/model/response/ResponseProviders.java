package tech.orbfin.api.productsservices.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.stereotype.Component;
import tech.orbfin.api.productsservices.model.Provider;
import tech.orbfin.api.productsservices.model.Service;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Setter
@Getter
@Component
public class ResponseProviders {
    List<Provider> providers;
    String errorMessage;
    Integer statusCode;
}
