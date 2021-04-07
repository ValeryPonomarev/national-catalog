package com.example.nationalcatalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private Set<Article> articleSet = new HashSet<>();

    @Value("classpath:feed-product-response")
    private Resource feedProductResponseTemplate;
    @Value("classpath:feed-status-response")
    private Resource feedStatusResponseTemplate;

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

    @PostMapping("/feed")
    public ResponseEntity<Collection<ModerationResponse>> moderation(
        @RequestBody Collection<FeedRequestDto> body,
        @RequestParam(value = "authkey", defaultValue = "${national-catalog.authKey}") String authKey) {
        if (checkAuth(authKey)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<ModerationResponse> result = new ArrayList<>(body.size());
        for (FeedRequestDto dto : body) {
            articleSet.stream().filter(it -> it.getFeedId().equals(Integer.toString(dto.getGoodId()))).findFirst()
                .ifPresent(it -> result.add(new ModerationResponse(Integer.parseInt(it.getFeedId()))));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/feed-status")
    public ResponseEntity<String> getFeedStatus(
        @RequestParam(value = "apikey") String authKey,
        @RequestParam("feed_id") int feedId) {
        if (checkAuth(authKey)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(asString(feedProductResponseTemplate).replace("!_FEED_ID", Integer.toString(feedId)));
    }

    @PostMapping("/feed-product-sign-pkcs")
    public ResponseEntity<String> signingProduct(
        @RequestParam(value = "apikey") String authKey,
        @RequestBody FeedRequestDto body) {
        if (checkAuth(authKey)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok("ok");
    }

    private boolean checkAuth(@RequestParam("authkey") String authKey) {
        return !authKey.equals("to73e6sj4gb9n259");
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
