package com.pyramids.repository.search;

import com.pyramids.domain.Historic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Historic entity.
 */
public interface HistoricSearchRepository extends ElasticsearchRepository<Historic, Long> {
}
