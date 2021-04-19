package com.example.nationalcatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NcFeedProductCardRequest {

    @JsonProperty("goodIds")
    private final List<String> goodIds;

    @JsonProperty("publicationAgreement")
    private final boolean publicationAgreement;
    
}
