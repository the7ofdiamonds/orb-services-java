package tech.orbfin.api.productsservices.services;

import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.http.HttpStatus;

import tech.orbfin.api.productsservices.configurations.ConfigKafka;
import tech.orbfin.api.productsservices.configurations.ConfigKafkaTopics;
import tech.orbfin.api.productsservices.model.Address;
import tech.orbfin.api.productsservices.model.Coordinates;
import tech.orbfin.api.productsservices.model.Provider;

import tech.orbfin.api.productsservices.model.request.RequestServices;
import tech.orbfin.api.productsservices.model.response.ResponseService;
import tech.orbfin.api.productsservices.model.response.ResponseServiceRequest;
import tech.orbfin.api.productsservices.model.response.ResponseServices;
import tech.orbfin.api.productsservices.model.Service;

import tech.orbfin.api.productsservices.repositories.IRepositoryServices;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Transactional
@org.springframework.stereotype.Service
public class Services {
    private final IRepositoryServices iRepositoryServices;
    private final EntityManager entityManager;
    private final Search search;
    private final ConfigKafkaTopics configKafkaTopics;
    private final ConfigKafka configKafka;

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

    public ResponseServices by(String type, Double price) throws Exception {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Service> query = cb.createQuery(Service.class);
            Root<Service> root = query.from(Service.class);

            Predicate predicate = cb.conjunction();

            if (type != null) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), type));
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
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseServiceRequest requestServices(RequestServices request) throws Exception {
        try {
            Long id = request.getId();
            String type = request.getType();
            String date = request.getDate();
            String time = request.getTime();
            Double price = request.getPrice();
            Address address = request.getAddress();
            Coordinates coordinates = request.getCoordinates();

            Service service = iRepositoryServices.getServiceByID(id);
            String serviceType = service.getType();

            if (!type.equals(serviceType)) {
                throw new Exception("This service type does not match our records.");
            }

            ResponseServices responseServices = by(type, price);

            if (responseServices.getErrorMessage() != null) {
                throw new Exception(responseServices.getErrorMessage());
            }

            if (address == null || address.isEmpty() && coordinates == null || coordinates.isEmpty()) {
                throw new Exception("Either coordinates or an address is required to request services.");
            }

            List<Long> providers = new ArrayList<>();

            if (!address.isEmpty()) {
                List<Address> locations = search.byAddress(address);

                for(Address addr : locations) {
                    Provider provider = addr.getProvider();
                    providers.add(provider.getId());
                }
            }

            if (!coordinates.isEmpty()) {
                List<Provider> providersList = search.byCoordinates(coordinates);

                for(Provider provider : providersList) {
                    List<Long> services = provider.getServices();

                    for(Long servID : services) {
                        if(id.equals(servID)) {
                            providers.add(provider.getId());
                        }
                    }
                }
            }

            if (providers.size() > 0) {
                RequestServices requestServices = RequestServices.builder()
                        .id(id)
                        .type(type)
                        .date(date)
                        .time(time)
                        .price(price)
                        .address(address)
                        .coordinates(coordinates)
                        .providers(providers)
                        .build();

                configKafka.requestServicesTemplate().send(configKafkaTopics.getTopicByType(type), requestServices);
            } else {
                throw new Exception("No providers are available try again at a later time.");
            }

            ResponseServiceRequest response = ResponseServiceRequest.builder()
                .successMessage("This service is available in your area providers have been contacted and will be with you shortly.")
                .statusCode(HttpStatus.OK.value())
                .build();

            return response;
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
