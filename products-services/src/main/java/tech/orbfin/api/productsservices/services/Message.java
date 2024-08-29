package tech.orbfin.api.productsservices.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.orbfin.api.productsservices.model.Address;
import tech.orbfin.api.productsservices.model.Coordinates;
import tech.orbfin.api.productsservices.model.Service;
import tech.orbfin.api.productsservices.repositories.IRepositoryServices;

@Slf4j
@AllArgsConstructor
@Transactional
@org.springframework.stereotype.Service
public class Message {
    private final IRepositoryServices iRepositoryServices;

    public String service(Long serviceID, String date, String time, Address address, Coordinates coordinates) {
        Service service = iRepositoryServices.getServiceByID(serviceID);

        return "";
    }

    public String property(Long propertyID, String date, String time) {
        return "";
    }

    public String business(Long businessID, String date, String time) {
        return "";
    }
}
