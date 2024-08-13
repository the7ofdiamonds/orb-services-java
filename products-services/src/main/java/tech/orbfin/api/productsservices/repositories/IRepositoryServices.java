package tech.orbfin.api.productsservices.repositories;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import tech.orbfin.api.productsservices.model.Service;

import java.util.List;

@Repository
public interface IRepositoryServices extends JpaRepository<Service, Long> {
    @Transactional
    @Query(nativeQuery = true, value = "CALL getServices()")
    public List<Service> getServices();
}
