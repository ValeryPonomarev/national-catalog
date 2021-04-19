package com.example.nationalcatalog;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.UTF_8;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/v3")
public class RestController {

    private final Set<Article> articleSet = new HashSet<>();

    @Value("classpath:feed-product-response")
    private Resource feedProductResponseTemplate;
    @Value("classpath:feed-id-response")
    private Resource feedIdResponseTemplate;
    @Value("classpath:feed-status-response")
    private Resource feedStatusResponseTemplate;
    @Value("classpath:feed-product-document-response")
    private Resource feedProductDocumentResponseTemplate;
    @Value("classpath:feed-sign-response")
    private Resource feedSignResponseTemplate;

    @GetMapping(value = "/feed-product", produces = "application/json; charset=utf-8")
    public ResponseEntity<String> feedProduct(
            @RequestParam(value = "apikey") String authKey,
            @RequestParam(value = "gtin") String gtin) {
        if (checkAuth(authKey)) {
            return new ResponseEntity<>("National Catalog API error", HttpStatus.BAD_REQUEST);
        }

        String randomFeedId = Integer.toString(randomInt(1, 1000));
        articleSet.add(new Article(gtin, randomFeedId));
        String response = asString(feedProductResponseTemplate)
            .replace("!_GOOD_ID", randomFeedId)
            .replace("!_GTIN", gtin);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/feed", produces = "application/json; charset=utf-8")
    public ResponseEntity<String> moderation(
        @RequestBody Collection<FeedRequestDto> body,
        @RequestParam(value = "authkey", defaultValue = "${national-catalog.authKey}") String authKey) {
        if (checkAuth(authKey)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(asString(feedIdResponseTemplate).replace("!_FEED_ID", Integer.toString(2136)));
    }

    @GetMapping("/feed-status")
    public ResponseEntity<String> getFeedStatus(
        @RequestParam(value = "apikey") String authKey,
        @RequestParam("feed_id") int feedId) {
        if (checkAuth(authKey)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(asString(feedStatusResponseTemplate).replace("!_FEED_ID", Integer.toString(feedId)));
    }

    @PostMapping(value = "/feed-product-document", produces = "application/json; charset=utf-8")
    public ResponseEntity<String> getProductDocument(
        @RequestParam(value = "apikey") String authKey,
        @RequestBody NcFeedProductCardRequest body) {
        if (checkAuth(authKey)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        String template = asString(feedProductDocumentResponseTemplate);
        for (String goodId : body.getGoodIds()) {
            template = template.replaceFirst("!_GOOD_ID", goodId);
        }
        return ResponseEntity.ok(template);
    }

    @PostMapping(value = "/feed-product-sign-pkcs", produces = "application/json; charset=utf-8")
    public ResponseEntity<String> signingProduct(
        @RequestParam(value = "apikey") String authKey,
        @RequestBody List<NcSignProductRequest> body) {
        if (checkAuth(authKey)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        String template = asString(feedSignResponseTemplate);
        for (NcSignProductRequest ncSignProductRequest : body) {
            template = template.replaceFirst("!_GOOD_ID", String.valueOf(ncSignProductRequest.getGoodId()));
        }
        System.out.println(template);
        return ResponseEntity.ok(template);
    }

    private boolean checkAuth(@RequestParam("authkey") String authKey) {
        return !authKey.equals("");
    }

    private int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    @SneakyThrows
    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

}
