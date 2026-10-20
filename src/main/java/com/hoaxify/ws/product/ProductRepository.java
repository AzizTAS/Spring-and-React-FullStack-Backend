package com.hoaxify.ws.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    String TR_FROM = "çğıöşüÇĞİÖŞÜ";
    String TR_TO = "cgiosuCGIOSU";

    Page<Product> findByCategoryId(Long categoryId, Pageable page);

    @Query("SELECT p FROM Product p WHERE "
            + "LOWER(FUNCTION('TRANSLATE', p.name, '" + TR_FROM + "', '" + TR_TO + "')) LIKE :pattern "
            + "OR LOWER(FUNCTION('TRANSLATE', CAST(p.description AS String), '" + TR_FROM + "', '" + TR_TO + "')) LIKE :pattern")
    Page<Product> searchByPattern(@Param("pattern") String pattern, Pageable page);
}
