package com.pyramids.service.impl;

import com.pyramids.service.HistoricService;
import com.pyramids.domain.Historic;
import com.pyramids.repository.HistoricRepository;
import com.pyramids.repository.search.HistoricSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Historic.
 */
@Service
@Transactional
public class HistoricServiceImpl implements HistoricService{

    private final Logger log = LoggerFactory.getLogger(HistoricServiceImpl.class);
    
    @Inject
    private HistoricRepository historicRepository;

    @Inject
    private HistoricSearchRepository historicSearchRepository;

    /**
     * Save a historic.
     *
     * @param historic the entity to save
     * @return the persisted entity
     */
    public Historic save(Historic historic) {
        log.debug("Request to save Historic : {}", historic);
        Historic result = historicRepository.save(historic);
        historicSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the historics.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Historic> findAll(Pageable pageable) {
        log.debug("Request to get all Historics");
        Page<Historic> result = historicRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one historic by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Historic findOne(Long id) {
        log.debug("Request to get Historic : {}", id);
        Historic historic = historicRepository.findOne(id);
        return historic;
    }

    /**
     *  Delete the  historic by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Historic : {}", id);
        historicRepository.delete(id);
        historicSearchRepository.delete(id);
    }

    /**
     * Search for the historic corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Historic> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Historics for query {}", query);
        Page<Historic> result = historicSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
