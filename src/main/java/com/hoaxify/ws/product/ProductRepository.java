package com.hoaxify.ws.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryId(Long categoryId, Pageable page);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE :pattern OR LOWER(CAST(p.description AS String)) LIKE :pattern")
    Page<Product> searchByPattern(@Param("pattern") String pattern, Pageable page);
}
