package tech.orbfin.api.productsservices.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import tech.orbfin.api.productsservices.model.response.ResponseServices;
import tech.orbfin.api.productsservices.model.Service;
import tech.orbfin.api.productsservices.repositories.IRepositoryServices;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Transactional
@org.springframework.stereotype.Service
public class Services {
    private final IRepositoryServices iRepositoryServices;
    public ResponseServices all() throws Exception {
        try {
            List<Service> serviceList = iRepositoryServices.getServices();

            ResponseServices response = ResponseServices.builder()
                    .services(serviceList)
                    .statusCode(HttpStatus.OK.value())
                    .build();

            return response;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
