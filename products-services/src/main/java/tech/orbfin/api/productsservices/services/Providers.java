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

import tech.orbfin.api.productsservices.repositories.IRepositoryProviders;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Transactional
@org.springframework.stereotype.Service
public class Providers {
    private final IRepositoryProviders iRepositoryProviders;
    private final EntityManager entityManager;
    private final KafkaTemplate<String, RequestService> kafkaTemplate;

    public List<Provider> byCoordinates(Coordinates coordinates) throws Exception {
        try {
            List<Provider> providers = List.of();

            return providers;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Address> byAddress(Address address) throws Exception {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Address> query = cb.createQuery(Address.class);
            Root<Address> root = query.from(Address.class);

            Predicate predicate = cb.conjunction();

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

            query.where(predicate);
            List<Address> addressList = entityManager.createQuery(query).getResultList();

            return addressList;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResponseProviders by(Double price, String type, Address address, Coordinates coordinates) throws Exception {
        try {
            List<Provider> providers = new ArrayList<>();
            List<Provider> providerList = new ArrayList<>();

            if (coordinates != null) {
                List<Provider> providersByCoords = byCoordinates(coordinates);
            }

            if (address != null && !address.isEmpty()) {
                List<Address> providersByAddress = byAddress(address);
                for (Address addr : providersByAddress) {
                    Provider provider = addr.getProvider();
                    List<Service> services = provider.getServices();

                    for (Service service : services) {
                        String serviceType = service.getType();

                        if (serviceType == type) {
                            providers.add(provider);
                        }
                    }
                }
            }

            if (price != null && price > 0) {
                for (Provider provider : providers) {
                    List<Service> services = provider.getServices();

                    for (Service service : services) {
                        Double providerPrice = service.getPrice();

                        if (price <= providerPrice) {
                            providerList.add(provider);
                        }
                    }
                }
            }

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

            List<Provider> providers = new ArrayList<>();
            Provider provider = iRepositoryProviders.getProviderByID(id);
            providers.add(provider);

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

            ResponseProviders responseProviders = by(price, type, address, coordinates);

            if (responseProviders.getProviders() != null && !responseProviders.getProviders().isEmpty()) {
                providers = responseProviders.getProviders();
            }

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
