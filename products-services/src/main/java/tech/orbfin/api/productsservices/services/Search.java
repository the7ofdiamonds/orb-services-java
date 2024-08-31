package tech.orbfin.api.productsservices.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import tech.orbfin.api.productsservices.model.Address;
import tech.orbfin.api.productsservices.model.Coordinates;
import tech.orbfin.api.productsservices.model.Provider;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Transactional
@org.springframework.stereotype.Service
public class Search {
    private final EntityManager entityManager;

    public List<Provider> byCoordinates(Coordinates coordinates) throws Exception {
        try {
            List<Provider> providers = new ArrayList<>();

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
}
