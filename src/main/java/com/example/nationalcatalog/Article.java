package com.example.nationalcatalog;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Article {
    private String gtin;
    private String feedId;
}
