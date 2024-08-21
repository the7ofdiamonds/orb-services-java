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
import tech.orbfin.api.productsservices.model.Provider;
import tech.orbfin.api.productsservices.model.Service;
import tech.orbfin.api.productsservices.model.request.RequestService;
import tech.orbfin.api.productsservices.model.response.ResponseProviders;
import tech.orbfin.api.productsservices.model.response.ResponseServiceRequest;
import tech.orbfin.api.productsservices.model.response.ResponseServices;

import tech.orbfin.api.productsservices.repositories.IRepositoryServices;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Transactional
@org.springframework.stereotype.Service
public class Providers {
    private final IRepositoryServices iRepositoryServices;
    private final EntityManager entityManager;
    private final KafkaTemplate<String, RequestService> kafkaTemplate;

    public ResponseProviders by(String type, Double price, Address address, Coordinates coordinates) throws Exception {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Provider> query = cb.createQuery(Provider.class);
            Root<Provider> root = query.from(Provider.class);

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
            List<Provider> providerList = entityManager.createQuery(query).getResultList();

            if (providerList.isEmpty()) {
                ResponseProviders response = ResponseProviders.builder()
                        .errorMessage("There are no providers of this service available at this time.")
                        .statusCode(HttpStatus.OK.value())
                        .build();

                return response;
            }

            ResponseProviders response = ResponseProviders.builder()
                    .providers(providerList)
                    .statusCode(HttpStatus.OK.value())
                    .build();

            return response;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseServiceRequest request(RequestService request) throws Exception {
        try {
            String id = request.getId();
            String type = request.getType();
            String date = request.getDate();
            String time = request.getTime();
            Double price = request.getPrice();
            Address address = request.getAddress();
            Coordinates coordinates = request.getCoordinates();

            String errorMessage = null;
//            Request to particular service provider using id

            if (type == null) {
                errorMessage = "A type is required to schedule a service.";
            }

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

            if (errorMessage != null) {
                ResponseServiceRequest response = ResponseServiceRequest.builder()
                        .errorMessage(errorMessage)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build();

                return response;
            }

            ResponseProviders responseProviders = by(type, price, address, coordinates);
            List<Provider> providers = responseProviders.getProviders();

            if (providers == null || providers.isEmpty()) {
                String cautionMessage = "Services requested are currently unavailable you will be notified when any changes have been made.";
                ResponseServiceRequest response = ResponseServiceRequest.builder()
                        .cautionMessage(cautionMessage)
                        .statusCode(HttpStatus.OK.value())
                        .build();

                return response;
            }

            kafkaTemplate.send(ConfigKafkaTopics.NOTARY_REQUEST, request);

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
