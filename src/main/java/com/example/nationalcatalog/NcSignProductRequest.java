package com.example.nationalcatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NcSignProductRequest {

    @JsonProperty("goodId")
    private final int goodId;

    @JsonProperty("base64Xml")
    private final String base64Xml;

    @JsonProperty("signature")
    private final String signature;

}
