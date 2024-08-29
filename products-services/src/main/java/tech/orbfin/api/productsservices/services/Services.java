package tech.orbfin.api.productsservices.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

import tech.orbfin.api.productsservices.model.Address;
import tech.orbfin.api.productsservices.model.Coordinates;
import tech.orbfin.api.productsservices.model.response.ResponseService;
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
    private final EntityManager entityManager;

    public ResponseServices all() throws Exception {
        try {
            List<Service> serviceList = iRepositoryServices.getServices();

            if (serviceList.isEmpty()) {
                ResponseServices response = ResponseServices.builder()
                        .errorMessage("There are no services available at this time.")
                        .statusCode(HttpStatus.OK.value())
                        .build();

                return response;
            }

            ResponseServices response = ResponseServices.builder()
                    .services(serviceList)
                    .statusCode(HttpStatus.OK.value())
                    .build();

            return response;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResponseServices byType(String type) throws Exception {
        try {
            List<Service> serviceList = iRepositoryServices.getServicesByType(type);

            if (serviceList.isEmpty()) {
                ResponseServices response = ResponseServices.builder()
                        .errorMessage("There are no services available at this time.")
                        .statusCode(HttpStatus.OK.value())
                        .build();

                return response;
            }

            ResponseServices response = ResponseServices.builder()
                    .services(serviceList)
                    .statusCode(HttpStatus.OK.value())
                    .build();

            return response;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResponseServices by(String type, Double price, Address address, Coordinates coordinates) throws Exception {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Service> query = cb.createQuery(Service.class);
            Root<Service> root = query.from(Service.class);

            Predicate predicate = cb.conjunction();

            if (type != null) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), type));
            }

            String streetAddress = address.getStreetAddress();
            String city = address.getCity();
            String state = address.getState();
            String zipcode = address.getZipcode();

            if (streetAddress != null) {
                predicate = cb.and(predicate, cb.equal(root.get("streetAddress"), streetAddress));
            }

            if (city != null) {
                predicate = cb.and(predicate, cb.equal(root.get("city"), city));
            }

            if (state != null) {
                predicate = cb.and(predicate, cb.equal(root.get("state"), state));
            }

            if (zipcode != null) {
                predicate = cb.and(predicate, cb.equal(root.get("zipcode"), zipcode));
            }

//        Search by coordinates

            if (price != null && price > 0) {
                predicate = cb.and(predicate, cb.equal(root.get("price"), price));
            }

            query.where(predicate);
            List<Service> serviceList = entityManager.createQuery(query).getResultList();

            if (serviceList.isEmpty()) {
                ResponseServices response = ResponseServices.builder()
                        .errorMessage("There are no services available at this time.")
                        .statusCode(HttpStatus.OK.value())
                        .build();

                return response;
            }

            ResponseServices response = ResponseServices.builder()
                    .services(serviceList)
                    .statusCode(HttpStatus.OK.value())
                    .build();

            return response;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseService byID(Long id) throws Exception {
        try {
            Service service = iRepositoryServices.getServiceByID(id);

            if (service == null) {
                ResponseService response = ResponseService.builder()
                        .errorMessage("There are no services available at this time.")
                        .statusCode(HttpStatus.OK.value())
                        .build();

                return response;
            }

            ResponseService response = ResponseService.builder()
                    .service(service)
                    .statusCode(HttpStatus.OK.value())
                    .build();

            return response;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
