package com.pyramids.repository;

import com.pyramids.domain.Historic;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Historic entity.
 */
@SuppressWarnings("unused")
public interface HistoricRepository extends JpaRepository<Historic,Long> {

}
