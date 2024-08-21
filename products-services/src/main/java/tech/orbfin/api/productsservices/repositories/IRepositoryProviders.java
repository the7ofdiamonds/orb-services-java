package tech.orbfin.api.productsservices.repositories;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tech.orbfin.api.productsservices.model.Provider;

import java.util.List;

public interface IRepositoryProviders extends JpaRepository<Provider, Long> {
    @Transactional
    @Query(nativeQuery = true, value = "CALL getProviders()")
    public List<Provider> getProviders();

    @Transactional
    @Query(nativeQuery = true, value = "CALL getProviderByID(:p_id)")
    public Provider getProviderByID(@Param("p_id") String id);
}
