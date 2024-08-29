package tech.orbfin.api.productsservices.services;

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
import tech.orbfin.api.productsservices.model.request.RequestProviders;

import tech.orbfin.api.productsservices.model.response.ResponseProviderRequest;
import tech.orbfin.api.productsservices.model.response.ResponseProviders;
import tech.orbfin.api.productsservices.model.response.ResponseProvidersRequest;

import tech.orbfin.api.productsservices.repositories.IRepositoryProviders;
import tech.orbfin.api.productsservices.repositories.IRepositoryServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Transactional
@org.springframework.stereotype.Service
public class Providers {
    private final Search search;
    private  final ConfigKafkaTopics configKafkaTopics;
    private final IRepositoryServices iRepositoryServices;
    private final IRepositoryProviders iRepositoryProviders;
    private final KafkaTemplate<String, RequestProviders> providersTemplate;
    private final KafkaTemplate<String, RequestProvider> providerTemplate;

    public ResponseProviders by(Double price, String type, Address address, Coordinates coordinates) throws Exception {
        try {
            List<Provider> providers = new ArrayList<>();
            List<Long> providerList = new ArrayList<>();

            if (coordinates != null) {
                List<Provider> providersByCoords = search.byCoordinates(coordinates);
                providers.addAll(providersByCoords);
            }

            if (address != null && !address.isEmpty()) {
                List<Address> providersByAddress = search.byAddress(address);

                for (Address addr : providersByAddress) {
                    Provider provider = addr.getProvider();
                    Long providerID = provider.getId();
                    List<Long> services = new ArrayList<>();
                    String serviceIDs = iRepositoryProviders.getProviderServices(providerID);
                    log.info(String.valueOf(serviceIDs));

                    if (serviceIDs != null && !serviceIDs.isEmpty()) {
                        serviceIDs = serviceIDs.replaceAll("^'|'$", "").trim();
                        services = Arrays.stream(serviceIDs.split(","))
                                .map(String::trim)
                                .map(Long::parseLong)
                                .collect(Collectors.toList());
                    }

                    for(Long serviceID : services) {
                        Service service = iRepositoryServices.getServiceByID(serviceID);
                        log.info(String.valueOf(serviceID));

                        if (service != null) {
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

    public String getSubject(String serviceType, String date, String time) {
        return String.format("A %s was requested for %s at %s", serviceType, date, time);
    }

    public String getMessage(Provider provider, Service service, String date, String time, Address address, Coordinates coordinates) {
        return String.format("Hello %s, A %s was requested for %s at %s. The client would like to meet at %s %s, %s %s %s", provider.getName(), service.getType(), date, time, address.getStreetAddress(), address.getCity(), address.getState(), address.getZipcode(), address.getCountry());
    }

    public ResponseProvidersRequest requestProviders(RequestProviders request) throws Exception {
        try {
            Long id = request.getServiceID();
            String type = request.getType();
            String date = request.getDate();
            String time = request.getTime();
            Double price = request.getPrice();
            Address address = request.getAddress();
            Coordinates coordinates = request.getCoordinates();

            if (id != null) {
                Service service = iRepositoryServices.getServiceByID(id);

                if (service == null || service.getPrice() < 0) {
                   throw new Exception("A service is required to schedule.");
                }
            }

            if (type == null) {
                throw new Exception("A type is required to schedule a service.");
            }

            if (date == null && time == null) {
                throw new Exception("A date and time is required to schedule a service.");
            }

            if (date == null) {
                throw new Exception("A date is required to schedule a service.");
            }

            if (time == null) {
                throw new Exception("A time is required to schedule a service.");
            }

            if (address == null && coordinates == null) {
                throw new Exception("Either an address or coordinates are required to schedule a service.");
            }

            ResponseProviders responseProviders = by(price, type, address, coordinates);

            if(responseProviders.getErrorMessage() != null) {

                ResponseProvidersRequest response = ResponseProvidersRequest.builder()
                        .errorMessage(responseProviders.getErrorMessage())
                        .statusCode(HttpStatus.OK.value())
                        .build();

                return response;
            }

            List<Long> providerIDList = responseProviders.getProviders();

           if(providerIDList.size() > 0) {
               RequestProviders requestProviders = RequestProviders.builder()
                       .id(id)
                       .type(type)
                       .date(date)
                       .time(time)
                       .price(price)
                       .address(address)
                       .coordinates(coordinates)
                       .providers(providerIDList)
                       .build();

               providersTemplate.send(configKafkaTopics.getTopicByType(type), requestProviders);
           }

            ResponseProvidersRequest response = ResponseProvidersRequest.builder()
                    .successMessage("Your request has been received awaiting confirmation please be attentive to your account")
                    .statusCode(HttpStatus.OK.value())
                    .build();

            return response;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResponseProviderRequest requestProvider(RequestProvider request) throws Exception {
        try {
            Long providerID = request.getProviderID();
            Long serviceID = request.getServiceID();
            String date = request.getDate();
            String time = request.getTime();
            Address address = request.getAddress();
            Coordinates coordinates = request.getCoordinates();

            Provider provider;

            if (providerID != null) {
                provider = iRepositoryProviders.getProviderByID(providerID);
            } else {
                throw new Exception("Provider ID is required to contact the provider.");
            }

            if (provider == null) {
                throw new Exception("Provider could not be found.");
            }

            Service service = null;

            if (serviceID != null) {
                service = iRepositoryServices.getServiceByID(serviceID);
            } else {
                throw new Exception("This service is not available at this time.");
            }

            if (service == null || service.getType() == null || service.getPrice() <= 0) {
                throw new Exception("Service could not be found.");
            }

            String serviceType = service.getType();
            String subject = getSubject(serviceType, date, time);
            String message = getMessage(provider, service, date, time, address, coordinates);

            if (subject == "" || message == "") {
                throw new Exception("There was an error creating the message to the provider.");
            }

            RequestProvider requestProvider = RequestProvider.builder()
                    .providerID(providerID)
                    .subject(subject)
                    .message(message)
                    .build();

            providerTemplate.send(ConfigKafkaTopics.PROVIDER_REQUEST, requestProvider);

            ResponseProviderRequest response = ResponseProviderRequest.builder()
                    .successMessage("Your request has been received awaiting a response from the provider.")
                    .statusCode(HttpStatus.OK.value())
                    .build();

            return response;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
