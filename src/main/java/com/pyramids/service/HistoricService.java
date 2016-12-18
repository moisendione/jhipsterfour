package com.pyramids.service;

import com.pyramids.domain.Historic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Historic.
 */
public interface HistoricService {

    /**
     * Save a historic.
     *
     * @param historic the entity to save
     * @return the persisted entity
     */
    Historic save(Historic historic);

    /**
     *  Get all the historics.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Historic> findAll(Pageable pageable);

    /**
     *  Get the "id" historic.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Historic findOne(Long id);

    /**
     *  Delete the "id" historic.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the historic corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Historic> search(String query, Pageable pageable);
}
