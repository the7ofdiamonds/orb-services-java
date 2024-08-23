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

import tech.orbfin.api.productsservices.model.request.RequestProvider;
import tech.orbfin.api.productsservices.model.request.RequestService;

import tech.orbfin.api.productsservices.model.response.ResponseProviders;
import tech.orbfin.api.productsservices.model.response.ResponseServiceRequest;

import tech.orbfin.api.productsservices.repositories.IRepositoryProviders;
import tech.orbfin.api.productsservices.repositories.IRepositoryServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Transactional
@org.springframework.stereotype.Service
public class Providers {
    private final IRepositoryServices iRepositoryServices;
    private final IRepositoryProviders iRepositoryProviders;
    private final EntityManager entityManager;
    private final KafkaTemplate<String, RequestProvider> kafkaTemplate;

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
            String country = address.getCountry();

            if (streetAddress != null && !streetAddress.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("streetAddress"), streetAddress));
            }

            if (city != null && !city.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("city"), city));
            }

            if (state != null && !state.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("state"), state));
            }

            if (zipcode != null && !zipcode.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("zipcode"), zipcode));
            }

            if (country != null && !country.trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("country"), country));
            }

            query.where(predicate);
            List<Address> addressList = entityManager.createQuery(query).getResultList();

            log.info("Found {} matching addresses", addressList.size());
            return addressList;
        } catch (Exception e) {
            log.error("Error querying addresses", e);
            throw new Exception("Error querying addresses: " + e.getMessage(), e);
        }
    }

    public ResponseProviders by(Double price, String type, Address address, Coordinates coordinates) throws Exception {
        try {
            List<Provider> providers = new ArrayList<>();
            List<Long> providerList = new ArrayList<>();

//            if (coordinates != null) {
//                List<Provider> providersByCoords = byCoordinates(coordinates);
//                providers.addAll(providersByCoords);
//            }

            if (address != null && !address.isEmpty()) {
                List<Address> providersByAddress = byAddress(address);

                for (Address addr : providersByAddress) {
                    Provider provider = addr.getProvider();
                    Long providerID = provider.getId();

                    List<Long> services = new ArrayList<>();

                    String serviceIDs = iRepositoryProviders.getProviderServices(providerID);

                    if (serviceIDs != null && !serviceIDs.isEmpty()) {
                        serviceIDs = serviceIDs.replaceAll("^'|'$", "").trim();
                        services = Arrays.stream(serviceIDs.split(","))
                                .map(String::trim)
                                .map(Long::parseLong)
                                .collect(Collectors.toList());
                    }

                    for(Long serviceID : services) {
                        Service service = iRepositoryServices.getServiceByID(serviceID);

                        if (service.getType().equals(type)) {
                            providers.add(provider);
                        }

                        if (price != null && price > 0) {
                            if (price <= service.getPrice()) {
                                providerList.add(provider.getId());
                                break;
                            }
                        } else {
                            providerList.add(provider.getId());
                        }
                    }
                }
            }

            if (providerList.isEmpty()) {
                return ResponseProviders.builder()
                        .errorMessage("There are no providers of this service available at this time.")
                        .statusCode(HttpStatus.OK.value())
                        .build();
            }

            return ResponseProviders.builder()
                    .providers(providerList)
                    .statusCode(HttpStatus.OK.value())
                    .build();

        } catch (Exception e) {
            log.error("Error fetching providers", e);
            throw new Exception("Error fetching providers: " + e.getMessage(), e);
        }
    }

    public ResponseServiceRequest request(RequestService request) throws Exception {
        try {
            Long id = request.getId();
            String type = request.getType();
            String date = request.getDate();
            String time = request.getTime();
            Double price = request.getPrice();
            Address address = request.getAddress();
            Coordinates coordinates = request.getCoordinates();

            String errorMessage = null;

            if (id != null) {
                Service service = iRepositoryServices.getServiceByID(id);

                if (service == null || service.getPrice() < 0) {
                    errorMessage = "A service is required to schedule.";
                }
            }

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

            List<Long> providerIDList = responseProviders.getProviders();

           if(providerIDList.size() > 0) {
               RequestProvider requestProvider = RequestProvider.builder()
                       .id(id)
                       .type(type)
                       .date(date)
                       .time(time)
                       .price(price)
                       .address(address)
                       .coordinates(coordinates)
                       .providers(providerIDList)
                       .build();

               kafkaTemplate.send(ConfigKafkaTopics.NOTARY_REQUEST, requestProvider);
           }

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
