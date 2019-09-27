package org.camoiloc.facts.service;

import org.camoiloc.facts.dto.Fact;
import org.camoiloc.facts.dto.Status;
import org.camoiloc.facts.dto.SupportedDirectionsResponse;
import org.camoiloc.facts.dto.TranslationResponse;
import org.camoiloc.facts.exception.UnsupportedDirectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FactsService {

    private static final Logger log = LoggerFactory.getLogger(FactsService.class);

    private static final int FACTS_TO_FETCH = 1000;
    private static final String RANDOM_FACT_URL = "https://uselessfacts.jsph.pl/random.json";

    private final RestTemplate restTemplate;
    private final Map<String, Fact> facts = new ConcurrentHashMap<>(FACTS_TO_FETCH);
    private final Map<String, Map<String, Fact>> factTranslations = new ConcurrentHashMap<>(FACTS_TO_FETCH);
    private final DefaultUriBuilderFactory uriBuilderFactory;

    @Value("${yandex.api.key}")
    private String yandexApiKey;

    private Status status = Status.INITIALISED;
    private Set<String> supportedDirections;

    public FactsService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.defaultMessageConverters().build();
        this.uriBuilderFactory = new DefaultUriBuilderFactory();
    }

    @PostConstruct
    protected void initialise() {
        CompletableFuture.runAsync(() -> retrieveFacts(), Executors.newSingleThreadExecutor());
    }

    private void retrieveFacts() {
        log.info("Retrieving facts...");
        status = Status.RETRIEVING;
        try {
            CompletableFuture.allOf(IntStream.range(0, FACTS_TO_FETCH)
                                             .parallel()
                                             .mapToObj(i -> retrieveRandomFact(i).thenAccept(this::storeFact))
                                             .toArray(CompletableFuture[]::new)).thenAccept(v -> {
                status = Status.COMPLETED;
                log.info("{} random facts successfully retrieved, of which {} are unique!",
                         FACTS_TO_FETCH,
                         facts.size());
            }).get(5L, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            status = Status.ERROR;
            log.error("Failed to retrieve facts due to interruption!", e);
        } catch (ExecutionException e) {
            status = Status.ERROR;
            log.error("Failed to retrieve facts due to exception during execution!", e);
        } catch (TimeoutException e) {
            status = Status.ERROR;
            log.error("Failed to retrieve facts due to a timeout!", e);
        }
    }

    private void storeFact(Fact fact) {
        Fact duplicateFact = facts.put(fact.getId(), fact);
        if (duplicateFact != null) {
            log.info("Got duplicate fact {}", duplicateFact.getId());
        }
        String language = normalizeLanguage(fact.getLanguage());
        fact.setLanguage(language);
        factTranslations.computeIfAbsent(fact.getId(), id -> new ConcurrentHashMap<>()).put(language, fact);
    }

    private CompletableFuture<Fact> retrieveRandomFact(int factNumber) {
        log.info("Retrieving random fact #{}", factNumber);
        try {
            Fact fact = this.restTemplate.getForObject(RANDOM_FACT_URL, Fact.class);
            log.info("Random fact #{} retrieved - {}", factNumber, fact.getId());
            return CompletableFuture.completedFuture(fact);
        } catch (Exception e) {
            log.error("Failed to retrieve fact #{}", factNumber, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public Status getStatus() {
        return status;
    }

    public Set<String> getFactIDs() {
        return facts.keySet();
    }

    public int getTotalFacts() {
        return FACTS_TO_FETCH;
    }

    public int getUniqueFacts() {
        return facts.size();
    }

    public Fact getFact(String id) {
        return facts.get(id);
    }

    public Fact getFact(String id, String language) throws UnsupportedDirectionException {
        if (language == null) {
            //get original fact
            return getFact(id);
        }
        Map<String, Fact> factTranslationsMap = factTranslations.get(id);
        if (factTranslationsMap == null) {
            //fact doesn't exist
            return null;
        }
        language = normalizeLanguage(language);
        Fact fact = factTranslationsMap.get(language);
        if (fact == null) {
            Fact originalFact = facts.get(id);
            if (originalFact == null) {
                return null;
            }
            try {
                return translateFact(originalFact, language);
            } catch (RestClientException e) {
                log.error("Failed to translate fact [{}]", id, e);
                throw e;
            }
        }
        return fact;
    }

    private void ensureDirectionSupported(String direction) throws UnsupportedDirectionException {
        if (supportedDirections == null) {
            supportedDirections = retrieveSupportedDirections();
        }
        if (supportedDirections == null || !supportedDirections.contains(direction)) {
            throw new UnsupportedDirectionException(direction);
        }
    }

    private Set<String> retrieveSupportedDirections() {
        log.info("Retrieving supported languages...");
        URI uri = getTranslationApiUriBuilder("getLangs").build();
        SupportedDirectionsResponse response = restTemplate.getForObject(uri, SupportedDirectionsResponse.class);
        log.info("Successfully retrieved {} supported directions!", response.getDirections().size());
        return response.getDirections();
    }

    private UriBuilder getTranslationApiUriBuilder(String method) {
        return uriBuilderFactory.builder()
                                .scheme("https")
                                .host("translate.yandex.net")
                                .pathSegment("api")
                                .pathSegment("v1.5")
                                .pathSegment("tr.json")
                                .pathSegment(method)
                                .queryParam("key", yandexApiKey);
    }

    private Fact translateFact(Fact fact, String language) throws UnsupportedDirectionException {
        String direction = fact.getLanguage() + '-' + language;
        ensureDirectionSupported(direction);
        log.info("Translating fact [{}] from [{}] to [{}]...", fact.getId(), fact.getLanguage(), language);
        URI
                uri =
                getTranslationApiUriBuilder("translate").queryParam("text", fact.getText())
                                                        .queryParam("lang", direction)
                                                        .build();
        TranslationResponse response = restTemplate.getForObject(uri, TranslationResponse.class);

        Fact clone = fact.clone();
        clone.setLanguage(language);
        clone.setText(response.getText().stream().collect(Collectors.joining()));

        log.info("Successfully translated fact [{}]!", fact.getId());
        Fact
                duplicateTranslation =
                factTranslations.computeIfAbsent(fact.getId(), _id -> new ConcurrentHashMap<>()).put(language, clone);
        if (duplicateTranslation != null) {
            log.warn("Translation of fact [{}] to language [{}] already exists!", fact.getId(), language);
        }
        return clone;
    }

    private String normalizeLanguage(String language) {
        //expected format ISO 639-1
        //TODO: normalize language?
        return language;
    }
}
