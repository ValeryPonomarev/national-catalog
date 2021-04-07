package com.example.nationalcatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FeedRequestDto {

    @JsonProperty("good_id")
    private int goodId;

    @JsonProperty("moderation")
    private int moderation;

}
