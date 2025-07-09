package ru.kolosov.empty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kolosov.empty.dto.ProductDTO;
import ru.kolosov.empty.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> getProductByName(String name);

    @Query("from Product where name in :names")
    Optional<List<Product>> findProductsByName(@Param("names") List<String> names);

    boolean existsByName(String name);
}
