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

import org.springframework.kafka.core.KafkaTemplate;

import tech.orbfin.api.productsservices.configurations.ConfigKafkaTopics;
import tech.orbfin.api.productsservices.model.Address;
import tech.orbfin.api.productsservices.model.Coordinates;
import tech.orbfin.api.productsservices.model.request.RequestService;
import tech.orbfin.api.productsservices.model.request.RequestServices;
import tech.orbfin.api.productsservices.model.response.ResponseService;
import tech.orbfin.api.productsservices.model.response.ResponseServiceRequest;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;

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

            log.info(String.valueOf(serviceList.get(0)));
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

    public ResponseServices by(RequestServices request) {
        String type = request.getType();
        String streetAddress = request.getStreetAddress();
        String city = request.getCity();
        String state = request.getState();
        String zipcode = request.getZipcode();
        Integer price = request.getPrice();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Service> query = cb.createQuery(Service.class);
        Root<Service> root = query.from(Service.class);

        Predicate predicate = cb.conjunction();

        if (type != null && !type.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("type"), type));
        }

        if (streetAddress != null && !streetAddress.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("streetAddress"), streetAddress));
        }

        if (city != null && !city.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("city"), city));
        }

        if (state != null && !state.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("state"), state));
        }

        if (zipcode != null && !zipcode.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("zipcode"), zipcode));
        }

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
    }

    public ResponseService byID(String id) throws Exception {
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

    public ResponseServiceRequest request(RequestService request) throws Exception {
        try {
            String id = request.getId();
            String date = request.getDate();
            String time = request.getTime();
            Address address = request.getAddress();
            Coordinates coordinates = request.getCoordinates();

            String errorMessage = null;

            if (date == null && time == null) {
                errorMessage = "A date and time is required to schedule a service.";
            }

            if (date == null) {
                errorMessage = "A date is required to schedule a service.";
            }

            if (time == null) {
                errorMessage = "A time is required to schedule a service.";
            }

            if (address == null && coordinates == null) {
                errorMessage = "Either an address or coordinates are required to schedule a service.";
            }

            String streetAddress = address.getStreetAddress();
            String city = address.getCity();
            String state = address.getState();
            String zipcode = address.getZipcode();
            String county = address.getCounty();

            if (errorMessage != null) {
                ResponseServiceRequest response = ResponseServiceRequest.builder()
                        .errorMessage(errorMessage)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build();

                return response;
            }

//            Search for services
            RequestServices requestServices = RequestServices.builder()
                    .streetAddress(streetAddress)
                    .city(city)
                    .state(state)
                    .zipcode(zipcode)
                    .county(county)
                    .build();

            ResponseServices responseServices = by(requestServices);
            List<Service> services = responseServices.getServices();

            if (services.isEmpty()) {
                errorMessage = "Services requested are currently unavailable you will be notified when any changes have been made.";
                ResponseServiceRequest response = ResponseServiceRequest.builder()
                        .errorMessage(errorMessage)
                        .statusCode(HttpStatus.OK.value())
                        .build();

                return response;
            }

//            Send notification to services close by
            String email = "notaryclientemail@gmail.com";
            kafkaTemplate.send(ConfigKafkaTopics.NOTARY_REQUEST, email);

            ResponseServiceRequest response = ResponseServiceRequest.builder()
                .successMessage("Your request has been received awaiting confirmation please be attentive to your account")
                .statusCode(HttpStatus.OK.value())
                .build();

            return response;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
