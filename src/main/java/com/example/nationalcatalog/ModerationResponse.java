package com.example.nationalcatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonRootName("result")
public class ModerationResponse {
    @JsonProperty("feed_id")
    private int feedId;
}
