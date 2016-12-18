package com.pyramids.web.rest;

import com.pyramids.JhipsterfourApp;

import com.pyramids.domain.Historic;
import com.pyramids.repository.HistoricRepository;
import com.pyramids.service.HistoricService;
import com.pyramids.repository.search.HistoricSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the HistoricResource REST controller.
 *
 * @see HistoricResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterfourApp.class)
public class HistoricResourceIntTest {

    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATION_DATE_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_CREATION_DATE);

    private static final String DEFAULT_USERNAME = "AAAAA";
    private static final String UPDATED_USERNAME = "BBBBB";

    private static final String DEFAULT_ACTION = "AAAAA";
    private static final String UPDATED_ACTION = "BBBBB";

    private static final String DEFAULT_TABLE_NAME = "AAAAA";
    private static final String UPDATED_TABLE_NAME = "BBBBB";

    private static final Long DEFAULT_RECORD_ID = 1L;
    private static final Long UPDATED_RECORD_ID = 2L;

    @Inject
    private HistoricRepository historicRepository;

    @Inject
    private HistoricService historicService;

    @Inject
    private HistoricSearchRepository historicSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restHistoricMockMvc;

    private Historic historic;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        HistoricResource historicResource = new HistoricResource();
        ReflectionTestUtils.setField(historicResource, "historicService", historicService);
        this.restHistoricMockMvc = MockMvcBuilders.standaloneSetup(historicResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Historic createEntity(EntityManager em) {
        Historic historic = new Historic()
                .creationDate(DEFAULT_CREATION_DATE)
                .username(DEFAULT_USERNAME)
                .action(DEFAULT_ACTION)
                .tableName(DEFAULT_TABLE_NAME)
                .recordId(DEFAULT_RECORD_ID);
        return historic;
    }

    @Before
    public void initTest() {
        historicSearchRepository.deleteAll();
        historic = createEntity(em);
    }

    @Test
    @Transactional
    public void createHistoric() throws Exception {
        int databaseSizeBeforeCreate = historicRepository.findAll().size();

        // Create the Historic

        restHistoricMockMvc.perform(post("/api/historics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(historic)))
                .andExpect(status().isCreated());

        // Validate the Historic in the database
        List<Historic> historics = historicRepository.findAll();
        assertThat(historics).hasSize(databaseSizeBeforeCreate + 1);
        Historic testHistoric = historics.get(historics.size() - 1);
        assertThat(testHistoric.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testHistoric.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testHistoric.getAction()).isEqualTo(DEFAULT_ACTION);
        assertThat(testHistoric.getTableName()).isEqualTo(DEFAULT_TABLE_NAME);
        assertThat(testHistoric.getRecordId()).isEqualTo(DEFAULT_RECORD_ID);

        // Validate the Historic in ElasticSearch
        Historic historicEs = historicSearchRepository.findOne(testHistoric.getId());
        assertThat(historicEs).isEqualToComparingFieldByField(testHistoric);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = historicRepository.findAll().size();
        // set the field null
        historic.setCreationDate(null);

        // Create the Historic, which fails.

        restHistoricMockMvc.perform(post("/api/historics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(historic)))
                .andExpect(status().isBadRequest());

        List<Historic> historics = historicRepository.findAll();
        assertThat(historics).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUsernameIsRequired() throws Exception {
        int databaseSizeBeforeTest = historicRepository.findAll().size();
        // set the field null
        historic.setUsername(null);

        // Create the Historic, which fails.

        restHistoricMockMvc.perform(post("/api/historics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(historic)))
                .andExpect(status().isBadRequest());

        List<Historic> historics = historicRepository.findAll();
        assertThat(historics).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTableNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = historicRepository.findAll().size();
        // set the field null
        historic.setTableName(null);

        // Create the Historic, which fails.

        restHistoricMockMvc.perform(post("/api/historics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(historic)))
                .andExpect(status().isBadRequest());

        List<Historic> historics = historicRepository.findAll();
        assertThat(historics).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRecordIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = historicRepository.findAll().size();
        // set the field null
        historic.setRecordId(null);

        // Create the Historic, which fails.

        restHistoricMockMvc.perform(post("/api/historics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(historic)))
                .andExpect(status().isBadRequest());

        List<Historic> historics = historicRepository.findAll();
        assertThat(historics).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllHistorics() throws Exception {
        // Initialize the database
        historicRepository.saveAndFlush(historic);

        // Get all the historics
        restHistoricMockMvc.perform(get("/api/historics?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(historic.getId().intValue())))
                .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE_STR)))
                .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
                .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
                .andExpect(jsonPath("$.[*].tableName").value(hasItem(DEFAULT_TABLE_NAME.toString())))
                .andExpect(jsonPath("$.[*].recordId").value(hasItem(DEFAULT_RECORD_ID.intValue())));
    }

    @Test
    @Transactional
    public void getHistoric() throws Exception {
        // Initialize the database
        historicRepository.saveAndFlush(historic);

        // Get the historic
        restHistoricMockMvc.perform(get("/api/historics/{id}", historic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(historic.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE_STR))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()))
            .andExpect(jsonPath("$.tableName").value(DEFAULT_TABLE_NAME.toString()))
            .andExpect(jsonPath("$.recordId").value(DEFAULT_RECORD_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingHistoric() throws Exception {
        // Get the historic
        restHistoricMockMvc.perform(get("/api/historics/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHistoric() throws Exception {
        // Initialize the database
        historicService.save(historic);

        int databaseSizeBeforeUpdate = historicRepository.findAll().size();

        // Update the historic
        Historic updatedHistoric = historicRepository.findOne(historic.getId());
        updatedHistoric
                .creationDate(UPDATED_CREATION_DATE)
                .username(UPDATED_USERNAME)
                .action(UPDATED_ACTION)
                .tableName(UPDATED_TABLE_NAME)
                .recordId(UPDATED_RECORD_ID);

        restHistoricMockMvc.perform(put("/api/historics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedHistoric)))
                .andExpect(status().isOk());

        // Validate the Historic in the database
        List<Historic> historics = historicRepository.findAll();
        assertThat(historics).hasSize(databaseSizeBeforeUpdate);
        Historic testHistoric = historics.get(historics.size() - 1);
        assertThat(testHistoric.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testHistoric.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testHistoric.getAction()).isEqualTo(UPDATED_ACTION);
        assertThat(testHistoric.getTableName()).isEqualTo(UPDATED_TABLE_NAME);
        assertThat(testHistoric.getRecordId()).isEqualTo(UPDATED_RECORD_ID);

        // Validate the Historic in ElasticSearch
        Historic historicEs = historicSearchRepository.findOne(testHistoric.getId());
        assertThat(historicEs).isEqualToComparingFieldByField(testHistoric);
    }

    @Test
    @Transactional
    public void deleteHistoric() throws Exception {
        // Initialize the database
        historicService.save(historic);

        int databaseSizeBeforeDelete = historicRepository.findAll().size();

        // Get the historic
        restHistoricMockMvc.perform(delete("/api/historics/{id}", historic.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean historicExistsInEs = historicSearchRepository.exists(historic.getId());
        assertThat(historicExistsInEs).isFalse();

        // Validate the database is empty
        List<Historic> historics = historicRepository.findAll();
        assertThat(historics).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchHistoric() throws Exception {
        // Initialize the database
        historicService.save(historic);

        // Search the historic
        restHistoricMockMvc.perform(get("/api/_search/historics?query=id:" + historic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historic.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE_STR)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].tableName").value(hasItem(DEFAULT_TABLE_NAME.toString())))
            .andExpect(jsonPath("$.[*].recordId").value(hasItem(DEFAULT_RECORD_ID.intValue())));
    }
}
