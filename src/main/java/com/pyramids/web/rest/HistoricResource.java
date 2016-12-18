package com.pyramids.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.pyramids.domain.Historic;
import com.pyramids.service.HistoricService;
import com.pyramids.web.rest.util.HeaderUtil;
import com.pyramids.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Historic.
 */
@RestController
@RequestMapping("/api")
public class HistoricResource {

    private final Logger log = LoggerFactory.getLogger(HistoricResource.class);
        
    @Inject
    private HistoricService historicService;

    /**
     * POST  /historics : Create a new historic.
     *
     * @param historic the historic to create
     * @return the ResponseEntity with status 201 (Created) and with body the new historic, or with status 400 (Bad Request) if the historic has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/historics",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Historic> createHistoric(@Valid @RequestBody Historic historic) throws URISyntaxException {
        log.debug("REST request to save Historic : {}", historic);
        if (historic.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("historic", "idexists", "A new historic cannot already have an ID")).body(null);
        }
        Historic result = historicService.save(historic);
        return ResponseEntity.created(new URI("/api/historics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("historic", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /historics : Updates an existing historic.
     *
     * @param historic the historic to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated historic,
     * or with status 400 (Bad Request) if the historic is not valid,
     * or with status 500 (Internal Server Error) if the historic couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/historics",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Historic> updateHistoric(@Valid @RequestBody Historic historic) throws URISyntaxException {
        log.debug("REST request to update Historic : {}", historic);
        if (historic.getId() == null) {
            return createHistoric(historic);
        }
        Historic result = historicService.save(historic);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("historic", historic.getId().toString()))
            .body(result);
    }

    /**
     * GET  /historics : get all the historics.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of historics in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/historics",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Historic>> getAllHistorics(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Historics");
        Page<Historic> page = historicService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/historics");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /historics/:id : get the "id" historic.
     *
     * @param id the id of the historic to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the historic, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/historics/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Historic> getHistoric(@PathVariable Long id) {
        log.debug("REST request to get Historic : {}", id);
        Historic historic = historicService.findOne(id);
        return Optional.ofNullable(historic)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /historics/:id : delete the "id" historic.
     *
     * @param id the id of the historic to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/historics/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteHistoric(@PathVariable Long id) {
        log.debug("REST request to delete Historic : {}", id);
        historicService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("historic", id.toString())).build();
    }

    /**
     * SEARCH  /_search/historics?query=:query : search for the historic corresponding
     * to the query.
     *
     * @param query the query of the historic search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/historics",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Historic>> searchHistorics(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Historics for query {}", query);
        Page<Historic> page = historicService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/historics");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
