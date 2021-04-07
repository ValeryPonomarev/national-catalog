package com.example.nationalcatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName("result")
public class NcModerationResponse {

    @JsonProperty("feed_id")
    private final int feedId;

}
